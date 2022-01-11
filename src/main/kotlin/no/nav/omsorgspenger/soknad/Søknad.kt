package no.nav.omsorgspenger.soknad

import com.fasterxml.jackson.annotation.JsonAlias
import no.nav.k9.søknad.Søknad
import no.nav.omsorgspenger.barn.BarnOppslag
import no.nav.omsorgspenger.soker.Søker
import java.net.URL
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*

data class Søknad(
    val nyVersjon: Boolean,
    val søknadId: String = UUID.randomUUID().toString(),
    val mottatt: ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC),
    val språk: String,
    val kroniskEllerFunksjonshemming: Boolean,
    var barn: Barn,
    val sammeAdresse: Boolean?,
    val relasjonTilBarnet: SøkerBarnRelasjon? = null,
    val legeerklæring: List<URL>,
    val samværsavtale: List<URL>? = null,
    val harForståttRettigheterOgPlikter: Boolean,
    val harBekreftetOpplysninger: Boolean
) {

    infix fun oppdaterBarnsNorskIdentifikatorFra(listeOverBarnOppslag: List<BarnOppslag>) {
        if(this.barn.manglerNorskIdentifikator()){
            barn oppdaterNorskIdentifikatorMed listeOverBarnOppslag.hentNorskIdentifikatorForBarn(barn.aktørId)
        }
    }

    fun tilKomplettSøknad(
        søker: Søker,
        k9Format: Søknad
    ) = KomplettSøknad(
        nyVersjon = nyVersjon,
        språk = språk,
        søknadId = søknadId,
        mottatt = mottatt,
        kroniskEllerFunksjonshemming = kroniskEllerFunksjonshemming,
        søker = søker,
        barn = barn,
        relasjonTilBarnet = relasjonTilBarnet,
        sammeAdresse = sammeAdresse,
        legeerklæringVedleggId = legeerklæring.map { it.vedleggId() },
        samværsavtaleVedleggId = samværsavtale?.map { it.vedleggId() } ?: listOf(),
        harForståttRettigheterOgPlikter = harForståttRettigheterOgPlikter,
        harBekreftetOpplysninger = harBekreftetOpplysninger,
        k9FormatSøknad = k9Format
    )
}

enum class SøkerBarnRelasjon() {
    @JsonAlias("mor")
    MOR(),
    @JsonAlias("far")
    FAR(),
    @JsonAlias("adoptivforelder")
    ADOPTIVFORELDER(),
    @JsonAlias("fosterforelder")
    FOSTERFORELDER()
}

fun URL.vedleggId(): String = this.toString().substringAfterLast("/")

private fun List<BarnOppslag>.hentNorskIdentifikatorForBarn(aktørId: String?) = find { it.aktørId == aktørId }?.identitetsnummer