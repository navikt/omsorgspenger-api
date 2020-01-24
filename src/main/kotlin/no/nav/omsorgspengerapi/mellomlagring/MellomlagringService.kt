package no.nav.omsorgspengerapi.mellomlagring

import no.nav.omsorgspengerapi.redis.RedisStore
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Service
import java.util.*
import kotlin.properties.Delegates

@Service
@ConfigurationProperties(prefix = "state")
class MellomlagringService constructor(@Autowired private val redisStore: RedisStore) {
    var passphrase: String by Delegates.notNull()
    private companion object {
        private val log: Logger = LoggerFactory.getLogger(MellomlagringService::class.java)
    }

    private val nøkkelPrefiks = "mellomlagring_"

    fun getMellomlagring(
        fnr: String
    ): String? {
        val krypto = Krypto(passphrase, fnr)
        val encrypted = redisStore.get(nøkkelPrefiks +fnr) ?: return null
        return krypto.decrypt(encrypted)
    }

    fun setMellomlagring(
        fnr: String,
        midlertidigSøknad: String
    ) {
        val krypto = Krypto(passphrase, fnr)
        val expirationDate = Calendar.getInstance().let {
            it.add(Calendar.HOUR, 24)
            it.time
        }
        redisStore.set(nøkkelPrefiks + fnr, krypto.encrypt(midlertidigSøknad),expirationDate)
    }

    fun slettMellomlagring(
        fnr: String
    ) {
        redisStore.delete(nøkkelPrefiks + fnr)
    }
}