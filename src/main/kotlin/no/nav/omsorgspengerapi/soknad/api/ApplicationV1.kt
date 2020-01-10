package no.nav.omsorgspengerapi.soknad.api

import no.nav.omsorgspengerapi.barn.api.ChildV1
import no.nav.omsorgspengerapi.soknad.mottak.Utenlandsopphold
import java.net.URL

data class ApplicationV1(
        val newVersion: Boolean,
        val sprak: String,
        val erYrkesaktiv: Boolean,
        val kroniskEllerFunksjonshemming: Boolean,
        val barn: ChildV1,
        val sammeAddresse: Boolean?,
        val delerOmsorg: Boolean?,
        val relasjonTilBarnet: ApplicantChildRelations? = null,
        val legeerklaring: List<URL>,
        val samvarsavtale: List<URL>?,
        val medlemskap: Medlemskap,
        val harForstattRettigheterOgPlikter: Boolean,
        val harBekreftetOpplysninger: Boolean,
        val utenlandsopphold: List<Utenlandsopphold>
)

class Medlemskap(
        val harBoddIUtlandetSiste12Mnd: Boolean,
        val utenlandsoppholdSiste12Mnd: List<Utenlandsopphold> = listOf(),
        val skalBoIUtlandetNeste12Mnd: Boolean,
        val utenlandsoppholdNeste12Mnd: List<Utenlandsopphold> = listOf()
)

enum class ApplicantChildRelations(relasjon: String) {
    MOR("mor"),
    FAR("far"),
    ADOPTIVFORELDER("adoptivforelder"),
    SAMVÆRSFORELDER("samværsforelder"),
    STEFORELDER("steforelder"),
    FOSTERFORELDER("fosterforelder")
}

