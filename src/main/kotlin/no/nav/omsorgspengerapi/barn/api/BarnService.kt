package no.nav.omsorgspengerapi.barn.api

import kotlinx.coroutines.reactor.mono
import no.nav.omsorgspengerapi.barn.lookup.BarnOppslagDTO
import no.nav.omsorgspengerapi.barn.lookup.BarnOppslagsService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class BarnService(private val barnOppslagsService: BarnOppslagsService) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(BarnService::class.java)
    }


    fun getBarn(): Flux<Barn> = barnOppslagsService.slåOppBarn()
            .map { barnOppslagDTO: BarnOppslagDTO -> barnOppslagDTO.tilBarn() }
            .doOnError { cause: Throwable ->
                run {
                    log.error("Oppslag av barn feilet. Returnerer tom liste. {}", cause)
                    mono { emptyList<BarnOppslagDTO>() }
                }
            }
}

private fun BarnOppslagDTO.tilBarn(): Barn = Barn(
        navn = fulltNavn(),
        fødselsdato = fødselsdato.toString(),
        aktørId = aktørId
)

private fun BarnOppslagDTO.fulltNavn(): String = if (mellomnavn == null) {
    "${fornavn} ${etternavn}"
} else {
    "${fornavn} ${mellomnavn} ${etternavn}"
}
