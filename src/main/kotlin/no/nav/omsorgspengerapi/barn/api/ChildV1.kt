package no.nav.omsorgspengerapi.barn.api

data class ChildV1(
        val navn: String?,
        val fodselsnummer: String?,
        val alternativId: String?,
        val aktoerId: String?
) {
    override fun toString(): String {
        return "BarnV1(navn=$navn, aktoerId=$aktoerId)"
    }
}