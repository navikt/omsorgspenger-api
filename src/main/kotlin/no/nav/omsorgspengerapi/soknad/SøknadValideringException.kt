package no.nav.omsorgspengerapi.soknad

class SøknadValideringException(message: String, val violations: MutableSet<Violation>) : RuntimeException(message)