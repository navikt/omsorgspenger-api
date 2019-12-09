package no.nav.omsorgspengerapi.soker.api

import no.nav.helse.soker.ApplicantV1
import no.nav.omsorgspengerapi.soker.lookup.ApplicanLookupDTO
import no.nav.omsorgspengerapi.soker.lookup.ApplicantLookupService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ApplicantService(private val applicantLookupService: ApplicantLookupService) {

    fun getApplicant(): Mono<ApplicantV1> {
        return applicantLookupService.lookupApplicant()
                .map { applicantLookUpDTO: ApplicanLookupDTO -> applicantLookUpDTO.toApplicantV1() }
    }
}

private fun ApplicanLookupDTO.toApplicantV1(): ApplicantV1 {
    return ApplicantV1(
            aktoer_id = this.aktoer_id,
            fornavn = fornavn,
            mellomnavn = mellomnavn,
            etternavn = etternavn,
            fodselsdato = fodselsdato
    )
}
