package no.nav.omsorgspengerapi.soknad.api

import java.lang.RuntimeException

class ApplicationValidationException(message: String, val violations: MutableSet<Violation>): RuntimeException(message)
