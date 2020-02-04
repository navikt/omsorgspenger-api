package no.nav.omsorgspenger.general.auth

class CookieNotSetException(cookieName : String) : RuntimeException("Ingen cookie med navnet '$cookieName' satt.")