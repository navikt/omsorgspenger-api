package no.nav.omsorgspengerapi.mellomlagring


import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.omsorgspengerapi.common.IdToken
import no.nav.omsorgspengerapi.docs.SELVBETJENING_ID_TOKEN_SCHEME
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
@SecurityRequirement(name = SELVBETJENING_ID_TOKEN_SCHEME)
@Tag(name = "Endepunkt for mellomlagring", description = "Endepunkt for mellomlagring av søknad")
class MellomlagringController(private val mellomlagringService: MellomlagringService) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(MellomlagringController::class.java)
    }

    @GetMapping("/mellomlagring")
    fun getMellomlagring(@RequestHeader ("Authorization") auth: String): String? {
        val mellomlagring = mellomlagringService.getMellomlagring(IdToken(auth).getSubject()!!)
        return when {
            mellomlagring != null -> {
                mellomlagring
            }
            else -> {
                "{}"
            }
        }
    }

    @PostMapping("/mellomlagring")
    fun setMellomlagring(@RequestHeader ("Authorization") auth: String, @RequestBody body: String) {
              return  mellomlagringService.setMellomlagring(IdToken(auth).getSubject()!!, body)
    }

    @DeleteMapping("/mellomlagring")
    fun slettMellomlagring(@RequestHeader ("Authorization") auth: String) {
        return mellomlagringService.slettMellomlagring(IdToken(auth).getSubject()!!)
    }
}