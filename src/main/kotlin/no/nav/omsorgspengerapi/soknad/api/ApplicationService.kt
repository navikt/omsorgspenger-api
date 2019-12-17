package no.nav.omsorgspengerapi.soknad.api

import no.nav.helse.soker.validate
import no.nav.omsorgspengerapi.soker.api.ApplicantService
import no.nav.omsorgspengerapi.soknad.mottak.ApplicationReceiverService
import no.nav.omsorgspengerapi.vedlegg.api.AttachmentService
import no.nav.omsorgspengerapi.vedlegg.document.DocumentJson
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.net.URL

@Service
class ApplicationService(
        private val applicationReceiverService: ApplicationReceiverService,
        private val applicantService: ApplicantService,
        private val attachmentService: AttachmentService
) {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(ApplicationService::class.java)
    }

    fun sendSoknad(applicationV1: ApplicationV1): Mono<Unit> {
        log.info("Getting applicant...")
        val applicant = applicantService.getApplicant()
        log.info("Applicant retrieved.")

        log.info("Validating applicant...")
        applicant.subscribe { it.validate() }
        log.info("Applicant validated.")

        log.info("Getting {} legeerklaringsFiler...", applicationV1.legeerklaring.size)
        val legeerklaringFiler: Flux<DocumentJson> = attachmentService.getAttachments(applicationV1.legeerklaring)
        legeerklaringFiler.collectList().subscribe { log.info("Got {} legeerklaringsFiler.", it.size) }

        log.info("Getting {} samvarsavtaleFiler...", applicationV1.samvarsavtale?.size)
        val samvarsavtaleFiler: Flux<DocumentJson>? = applicationV1.samvarsavtale?.let {
            attachmentService.getAttachments(it)
        }
        if (samvarsavtaleFiler != null) {
            samvarsavtaleFiler.collectList().subscribe { log.info("Got {} samvarsavtaleFiler.", it.size) }
        }

        val attachmentUrls = mutableListOf<URL>()
        attachmentUrls.addAll(applicationV1.legeerklaring)
        attachmentUrls.addAll(applicationV1.samvarsavtale!!)

        val attachments: Mono<MutableList<DocumentJson>> = Flux.concat(legeerklaringFiler, samvarsavtaleFiler).collectList()
        return attachments.map {
            log.info("Validating list of attachments")
            it.validateAttachment(attachmentUrls)
        }
    }
}