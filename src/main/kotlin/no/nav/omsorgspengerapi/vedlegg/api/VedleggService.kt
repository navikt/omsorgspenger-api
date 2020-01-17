package no.nav.omsorgspengerapi.vedlegg.api

import no.nav.omsorgspengerapi.vedlegg.dokument.DocumentJsonDTO
import no.nav.omsorgspengerapi.vedlegg.dokument.DokumentFilDTO
import no.nav.omsorgspengerapi.vedlegg.dokument.K9DocumentService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.net.URL

@Service
class VedleggService(private val k9DocumentService: K9DocumentService) {
    private val supportedContentTypes = listOf("application/pdf", "image/jpeg", "image/png")

    companion object {
        private val log: Logger = LoggerFactory.getLogger(VedleggService::class.java)
    }

    fun lagreVedlegg(vedlegg: Vedlegg): Mono<VedleggId> = if (!vedlegg.isSupportedContentType()) {
        Mono.error<VedleggId>(VedleggtypeIkkeSupportertException("Vedlegg med type '${vedlegg.contentType} ' er ikke supportert. Supporterte typer: $supportedContentTypes"))
    } else {
        k9DocumentService.lastOppDokument(DokumentFilDTO(
                title = vedlegg.title,
                contentType = vedlegg.contentType,
                content = vedlegg.content
        ))
                .map { VedleggId(id = it.id) }
    }

    fun hentVedleggSomJson(vedleggId: String): Mono<VedleggJson> =
            k9DocumentService.hentDokumentSomJson(vedleggId)
                    .map {
                        VedleggJson(
                                title = it.title,
                                contentType = it.contentType,
                                content = it.content
                        )
                    }

    fun hentVedlegg(vedleggURLer: List<URL>): Flux<DocumentJsonDTO> =
            k9DocumentService.hentDokumenter(ids = vedleggIdFraURL(vedleggURLer))

    fun slettVedlegg(vedleggId: String): Mono<Void> = k9DocumentService.slettDokument(vedleggId)

    private fun Vedlegg.isSupportedContentType(): Boolean = supportedContentTypes.contains(contentType)

    private fun vedleggIdFraURL(urls: List<URL>): List<String> = urls.map { it.path.substringAfterLast("/") }
}