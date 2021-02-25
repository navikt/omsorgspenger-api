package no.nav.omsorgspenger

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.helse.dusseldorf.ktor.jackson.dusseldorfConfigured
import no.nav.omsorgspenger.soknad.BarnDetaljer
import no.nav.omsorgspenger.soknad.SøkerBarnRelasjon
import no.nav.omsorgspenger.soknad.Søknad
import java.net.URL

class SøknadUtils {
    companion object {

        fun gyldigSøknad(legeerklæringURL: String? = null, samværsavtalURL: String? = null): Søknad {
            val legeerklæring = if (legeerklæringURL != null) listOf(URL(legeerklæringURL)) else listOf()
            val samværsavtale = if (samværsavtalURL != null) listOf(URL(samværsavtalURL)) else listOf()

            return Søknad(
                nyVersjon = false,
                språk = "nb",
                kroniskEllerFunksjonshemming = false,
                barn = BarnDetaljer(
                    norskIdentifikator = "02119970078",
                    navn = "Ole Dole Doffen"
                ),
                sammeAdresse = true,
                relasjonTilBarnet = SøkerBarnRelasjon.FAR,
                legeerklæring = legeerklæring,
                samværsavtale = samværsavtale,
                harForståttRettigheterOgPlikter = true,
                harBekreftetOpplysninger = true
            )
        }
    }
}

val objectMapper: ObjectMapper = jacksonObjectMapper().dusseldorfConfigured()
    .setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE)
    .configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false)

fun Any.somJson() = objectMapper.writeValueAsString(this)
