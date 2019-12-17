package no.nav.omsorgspengerapi.barn.api

import kotlinx.coroutines.reactor.mono
import no.nav.omsorgspengerapi.barn.lookup.ChildLookupDTO
import no.nav.omsorgspengerapi.barn.lookup.ChildLookupService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class ChildService(private val childLookupService: ChildLookupService) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(ChildService::class.java)
    }


    fun getChildren(): Flux<ChildV1> = childLookupService.lookupChildren()
            .map { childLookupDTO: ChildLookupDTO -> childLookupDTO.toChildV1() }
            .doOnError { cause: Throwable ->
                run {
                    log.error("Failed to look up children. Returning empty list. {}", cause)
                    mono { emptyList<ChildLookupDTO>() }
                }
            }
}

private fun ChildLookupDTO.toChildV1(): ChildV1 = ChildV1(
        navn = fulltNavn(),
        fodselsdato = fodselsdato.toString(),
        aktoerId = aktoerId
)

private fun ChildLookupDTO.fulltNavn(): String = if (mellomnavn == null) {
    "${fornavn} ${etternavn}"
} else {
    "${fornavn} ${mellomnavn} ${etternavn}"
}
