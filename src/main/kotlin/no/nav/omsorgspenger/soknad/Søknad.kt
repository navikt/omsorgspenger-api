package no.nav.omsorgspenger.soknad

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonFormat
import java.net.URL
import java.time.LocalDate
import java.util.*

data class Søknad(
    val nyVersjon: Boolean,
    val søknadId: String = UUID.randomUUID().toString(),
    val språk: String,
    val kroniskEllerFunksjonshemming: Boolean,
    val barn: BarnDetaljer,
    val sammeAdresse: Boolean?,
    val relasjonTilBarnet: SøkerBarnRelasjon? = null,
    val legeerklæring: List<URL>,
    val samværsavtale: List<URL>? = null,
    val harForståttRettigheterOgPlikter: Boolean,
    val harBekreftetOpplysninger: Boolean
)

data class BarnDetaljer(
    val norskIdentifikator: String? = null,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val fødselsdato: LocalDate? = null,
    val aktørId: String? = null,
    val navn: String? = null
) {
    override fun toString(): String {
        return "BarnDetaljer(aktoerId=${aktørId}, navn=${navn}, fodselsdato=${fødselsdato}"
    }
}

enum class SøkerBarnRelasjon() {
    @JsonAlias("mor")
    MOR(),
    @JsonAlias("far")
    FAR(),
    @JsonAlias("adoptivforelder")
    ADOPTIVFORELDER(),
    @JsonAlias("fosterforelder")
    FOSTERFORELDER()
}