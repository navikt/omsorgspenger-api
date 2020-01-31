package no.nav.omsorgspengerapi.barn

import no.nav.omsorgspengerapi.general.CallId
import no.nav.omsorgspengerapi.general.auth.IdToken
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class BarnService(
    private val barnGateway: BarnGateway
) {
    private companion object {
        private val logger: Logger = LoggerFactory.getLogger(BarnService::class.java)
    }

    internal suspend fun hentNaaverendeBarn(
        idToken: IdToken,
        callId: CallId
    ): List<Barn> = try {
        barnGateway.hentBarn(
            idToken = idToken,
            callId = callId
            ).map { it.tilBarn() }
        } catch (cause: Throwable) {
            logger.error("Feil ved henting av barn, returnerer en tom liste", cause)
            emptyList<Barn>()
        }

    private fun BarnGateway.BarnOppslagDTO.tilBarn() = Barn(
        fødselsdato = fødselsdato,
        navn = tilFullNavn(fornavn, mellomnavn, etternavn),
        aktørId = aktør_id
    )

    private fun tilFullNavn(fornavn: String, mellomnavn: String?, etternavn: String) = when {
        mellomnavn.isNullOrBlank() -> "$fornavn $etternavn"
        else -> "$fornavn $mellomnavn $etternavn"
    }
}