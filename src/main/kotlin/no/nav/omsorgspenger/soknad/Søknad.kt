package no.nav.omsorgspenger.soknad

import com.fasterxml.jackson.annotation.JsonFormat
import java.net.URL
import java.time.LocalDate

data class Søknad(
    val nyVersjon: Boolean,
    val språk: String,
    val erYrkesaktiv: Boolean,
    val kroniskEllerFunksjonshemming: Boolean,
    val barn: BarnDetaljer,
    val sammeAddresse: Boolean?,
    val delerOmsorg: Boolean?,
    val relasjonTilBarnet: SøkerBarnRelasjon? = null,
    val legeerklæring: List<URL>,
    val samværsavtale: List<URL>?,
    val medlemskap: Medlemskap,
    val harForståttRettigheterOgPlikter: Boolean,
    val harBekreftetOpplysninger: Boolean,
    val utenlandsopphold: List<Utenlandsopphold>
)

class Medlemskap(
    val harBoddIUtlandetSiste12Mnd: Boolean,
    val utenlandsoppholdSiste12Mnd: List<Utenlandsopphold> = listOf(),
    val skalBoIUtlandetNeste12Mnd: Boolean,
    val utenlandsoppholdNeste12Mnd: List<Utenlandsopphold> = listOf()
)

data class BarnDetaljer(
    val fødselsnummer: String? = null,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val fødselsdato: LocalDate? = null,
    val aktørId: String? = null,
    val navn: String? = null
) {
    override fun toString(): String {
        return "BarnDetaljer(aktoerId=${aktørId}, navn=${navn}, fodselsdato=${fødselsdato}"
    }
}

data class Utenlandsopphold(
    @JsonFormat(pattern = "yyyy-MM-dd") val fraOgMed: LocalDate,
    @JsonFormat(pattern = "yyyy-MM-dd") val tilOgMed: LocalDate,
    val landkode: String,
    val landnavn: String
)

enum class SøkerBarnRelasjon(relasjon: String) {
    MOR("mor"),
    FAR("far"),
    ADOPTIVFORELDER("adoptivforelder"),
    SAMVÆRSFORELDER("samværsforelder"),
    STEFORELDER("steforelder"),
    FOSTERFORELDER("fosterforelder")
}

