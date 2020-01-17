package no.nav.omsorgspengerapi.soker.api

import no.nav.helse.soker.Søker
import no.nav.omsorgspengerapi.soker.lookup.SøkerOppslagsDTO
import no.nav.omsorgspengerapi.soker.lookup.SøkerOppslagsService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class SøkerService(private val søkerOppslagsService: SøkerOppslagsService) {

    fun getSøker(): Mono<Søker> {
        return søkerOppslagsService.slåOppSøker()
                .map { søkerOppslagsDTO: SøkerOppslagsDTO -> søkerOppslagsDTO.tilSøker() }
    }
}

private fun SøkerOppslagsDTO.tilSøker(): Søker {
    return Søker(
            aktørId = this.aktørId,
            fornavn = fornavn,
            mellomnavn = mellomnavn,
            etternavn = etternavn,
            fødselsdato = fødselsdato
    )
}
