package no.nav.omsorgspengerapi.barn.api

import com.fasterxml.jackson.annotation.JsonFormat

data class ChildV1(
        private val navn: String?,
        @JsonFormat(pattern = "yyyy-MM-dd") private val fodselsdato: String?,
        private val aktoerId: String?
) {
    override fun toString(): String {
        return "BarnV1(navn=$navn, aktoerId=$aktoerId)"
    }
}