package no.nav.helse

import no.nav.omsorgspengerapi.soker.Søker
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SokerTest {
    @Test
    fun `En person som fyller 18 i morgen kan ikke søke`() {
        val soker = Søker(
            aktoerId = "1234",
            fodselsnummer = "29099012345",
            fodselsdato = LocalDate.now().minusYears(18).plusDays(1)
        )
        assertFalse(soker.myndig)
    }

    @Test
    fun `En person som fyller 18 i dag kan søke`() {
        val soker = Søker(
            aktoerId = "5678",
            fodselsnummer = "29099012345",
            fodselsdato = LocalDate.now().minusYears(18)
        )
        assertTrue(soker.myndig)
    }
}