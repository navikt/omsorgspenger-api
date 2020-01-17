package no.nav.omsorgspengerapi.vedlegg.api

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.omsorgspengerapi.docs.SELVBETJENING_ID_TOKEN_SCHEME
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.codec.multipart.FilePart
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import javax.validation.Valid

@RestController
@Validated
@SecurityRequirement(name = SELVBETJENING_ID_TOKEN_SCHEME)
@Tag(name = "Vedlegg", description = "Endepunkter for å laste opp, hente og slette vedlegg.")
class VedleggController(private val vedleggService: VedleggService) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(VedleggController::class.java)
    }

    @PostMapping("/vedlegg", consumes = ["multipart/form-data"])
    @ResponseStatus(HttpStatus.CREATED)
    fun lastOppVedlegg(@Valid @RequestPart("fil") filePart: FilePart): Mono<VedleggId>? {
        log.info("Vedlefg opplastet")

        val vedlegg = Vedlegg(
                content = filePart.content(),
                contentType = filePart.headers().contentType.toString(),
                title = filePart.filename()
        )
        log.info("Vedlegg mottatt: {}", vedlegg)

        return vedleggService.lagreVedlegg(vedlegg)
                .map { vedleggId: VedleggId ->
                    log.info("Fikk vedlegg med id: {}", vedleggId)
                    vedleggId
                }
    }

    @GetMapping("/vedlegg/{vedleggId}")
    fun hentVedlegg(@PathVariable("vedleggId") vedleggId: String): Mono<VedleggJson> {
        log.info("Henter vedlegg med id: {}", vedleggId)
        return vedleggService.hentVedleggSomJson(vedleggId)
    }

    @DeleteMapping("/vedlegg/{vedleggId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun slettVedlegg(@PathVariable("vedleggId") vedleggId: String): Mono<Void> {
        log.info("Sletter vedlegg med id: {}", vedleggId)
        return vedleggService.slettVedlegg(vedleggId)
    }
}