package no.nav.omsorgspengerapi.soknad.mottak

import no.nav.helse.soker.ApplicantV1
import no.nav.omsorgspengerapi.barn.api.ChildV1
import no.nav.omsorgspengerapi.soknad.api.ApplicantChildRelations
import no.nav.omsorgspengerapi.soknad.api.Medlemskap
import no.nav.omsorgspengerapi.vedlegg.api.AttachmentFile
import java.time.ZonedDateTime

data class CompleteApplicationDTO(
        val newVersion: Boolean,
        val sprak: String,
        val mottatt : ZonedDateTime,
        val erYrkesaktiv: Boolean,
        val kroniskEllerFunksjonshemming: Boolean,
        val barn: ChildV1,
        val soker: ApplicantV1,
        val sammeAddresse: Boolean?,
        val delerOmsorg: Boolean?,
        val relasjonTilBarnet: ApplicantChildRelations? = null,
        val legeerklaring: List<AttachmentFile>,
        val samvarsavtale: List<AttachmentFile>?,
        val medlemskap: Medlemskap,
        val harForstattRettigheterOgPlikter: Boolean,
        val harBekreftetOpplysninger: Boolean
)