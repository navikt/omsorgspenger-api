package no.nav.omsorgspengerapi.common

import org.springframework.http.HttpHeaders

object NavHeaders : HttpHeaders() {
    val XCorrelationId = "X-Correlation-ID"
}