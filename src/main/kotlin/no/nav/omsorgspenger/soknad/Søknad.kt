package no.nav.omsorgspenger.soknad

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonValue
import java.net.URL
import java.time.LocalDate

data class Søknad(
    val nyVersjon: Boolean,
    val språk: String,
    val arbeidssituasjon: List<Arbeidssituasjon>? = null, //TODO 23.02.2021 - Fjernes når frontend er oppdatert
    val kroniskEllerFunksjonshemming: Boolean,
    val barn: BarnDetaljer,
    val sammeAdresse: Boolean?,
    val relasjonTilBarnet: SøkerBarnRelasjon? = null,
    val legeerklæring: List<URL>,
    val samværsavtale: List<URL>? = null,
    val medlemskap: Medlemskap? = null, //TODO 23.02.2021 - Fjernes når frontend er oppdatert
    val harForståttRettigheterOgPlikter: Boolean,
    val harBekreftetOpplysninger: Boolean
)

class Medlemskap( //TODO 23.02.2021 - Fjernes når frontend er oppdatert
    val harBoddIUtlandetSiste12Mnd: Boolean,
    val utenlandsoppholdSiste12Mnd: List<Utenlandsopphold> = listOf(),
    val skalBoIUtlandetNeste12Mnd: Boolean,
    val utenlandsoppholdNeste12Mnd: List<Utenlandsopphold> = listOf()
)

data class BarnDetaljer(
    val norskIdentifikator: String? = null,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val fødselsdato: LocalDate? = null, //TODO 23.02.2021 - Fjernes når frontend er oppdatert
    val aktørId: String? = null,
    val navn: String? = null
) {
    override fun toString(): String {
        return "BarnDetaljer(aktoerId=${aktørId}, navn=${navn}, fodselsdato=${fødselsdato}"
    }
}

data class Utenlandsopphold( //TODO 23.02.2021 - Fjernes når frontend er oppdatert
    @JsonFormat(pattern = "yyyy-MM-dd") val fraOgMed: LocalDate,
    @JsonFormat(pattern = "yyyy-MM-dd") val tilOgMed: LocalDate,
    val landkode: String,
    val landnavn: String
)

enum class SøkerBarnRelasjon(@JsonValue val relasjon: String) {
    MOR("mor"),
    FAR("far"),
    ADOPTIVFORELDER("adoptivforelder"),
    SAMVÆRSFORELDER("samværsforelder"),
    STEFORELDER("steforelder"),
    FOSTERFORELDER("fosterforelder")
}

enum class Arbeidssituasjon(){ //TODO 23.02.2021 - Fjernes når frontend er oppdatert
    @JsonAlias("selvstendigNæringsdrivende") SELVSTENDIG_NÆRINGSDRIVENDE,
    @JsonAlias("arbeidstaker") ARBEIDSTAKER,
    @JsonAlias("frilanser") FRILANSER
}