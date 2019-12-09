package no.nav.omsorgspengerapi.barn.lookup

import com.fasterxml.jackson.annotation.JsonProperty

data class ChildLookupResponse(
        @JsonProperty("barn") val children: List<ChildLookupDTO>)