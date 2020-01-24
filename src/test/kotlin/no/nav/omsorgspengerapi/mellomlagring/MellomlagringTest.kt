package no.nav.omsorgspengerapi.mellomlagring

import no.nav.omsorgspengerapi.redis.RedisStore
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles


@SpringBootTest
@ActiveProfiles("test")
class MellomlagringTest {
    @Autowired
    lateinit var mellomlagringService: MellomlagringService
    @Autowired
    lateinit var redisStore: RedisStore

    @Test
    internal fun `mellomlagre verdier`() {
        mellomlagringService.setMellomlagring("test", "test")

        val mellomlagring = mellomlagringService.getMellomlagring("test")

        assertEquals("test", mellomlagring)
    }

    @Test
    internal fun `verdier skal være krypterte`() {

        mellomlagringService.setMellomlagring("test", "test")

        val mellomlagring = mellomlagringService.getMellomlagring("test")
        assertNotNull(redisStore.get("test"))
        assertNotEquals(mellomlagring, redisStore.get("test"))
    }

}