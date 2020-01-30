package no.nav.omsorgspengerapi.soker

import com.auth0.jwt.JWT
import no.nav.omsorgspengerapi.general.CallId
import no.nav.omsorgspengerapi.general.auth.IdToken

class SøkerService (
    private val søkerGateway: SøkerGateway
) {
    suspend fun getSoker(
        idToken: IdToken,
        callId: CallId
    ): Søker {
        val ident: String = JWT.decode(idToken.value).subject ?: throw IllegalStateException("Token mangler 'sub' claim.")
        return søkerGateway.hentSoker(idToken, callId).tilSøker(ident)
    }

    private fun  SøkerGateway.SokerOppslagRespons.tilSøker(fodselsnummer: String) = Søker(
        aktoerId = aktør_id,
        fodselsnummer = fodselsnummer, // TODO: Bør skifte til "alternativ_id" ?
        fodselsdato = fødselsdato,
        fornavn = fornavn,
        mellomnavn = mellomnavn,
        etternavn = etternavn
    )
}