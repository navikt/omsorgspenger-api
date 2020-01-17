package no.nav.omsorgspengerapi.barn.api

data class Barn(
        val navn: String? = null,
        val fødselsdato: String? = null,
        val fødselsnummer: String? = null,
        val alternativId: String? = null,
        val aktørId: String? = null
) {
    override fun toString(): String {
        return "BarnV1(navn=$navn, aktørId=$aktørId)"
    }
}