package no.nav.omsorgspengerapi.barn.api

data class ChildV1(
        val navn: String? = null,
        val fodselsdato: String? = null,
        val fodselsnummer: String? = null,
        val alternativId: String? = null,
        val aktoerId: String? = null
) {
    override fun toString(): String {
        return "BarnV1(navn=$navn, aktoerId=$aktoerId)"
    }
}