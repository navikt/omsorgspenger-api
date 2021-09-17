package no.nav.omsorgspenger

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.helse.dusseldorf.ktor.jackson.dusseldorfConfigured
import no.nav.omsorgspenger.soker.Søker
import no.nav.omsorgspenger.soknad.Barn
import no.nav.omsorgspenger.soknad.SøkerBarnRelasjon
import no.nav.omsorgspenger.soknad.Søknad
import java.net.URL
import java.time.LocalDate

class SøknadUtils {
    companion object {

        val søker = Søker(
            aktørId = "12345",
            fødselsdato = LocalDate.parse("2000-01-01"),
            fornavn = "Kjell",
            fødselsnummer = "26104500284"
        )

        val barn = Barn(
            norskIdentifikator = "02119970078",
            navn = "Ole Dole Doffen"
        )

        fun gyldigSøknad(legeerklæringURL: String? = null, samværsavtalURL: String? = null): Søknad {
            val legeerklæring = if (legeerklæringURL != null) listOf(URL(legeerklæringURL)) else listOf()
            val samværsavtale = if (samværsavtalURL != null) listOf(URL(samværsavtalURL)) else null

            return Søknad(
                nyVersjon = false,
                språk = "nb",
                kroniskEllerFunksjonshemming = false,
                barn = barn,
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
