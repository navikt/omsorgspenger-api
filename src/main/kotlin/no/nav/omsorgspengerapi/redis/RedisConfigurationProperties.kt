package no.nav.omsorgspengerapi.redis

import no.nav.omsorgspengerapi.redis.RedisMockUtil.startRedisMocked
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import kotlin.properties.Delegates

@Component
@ConfigurationProperties(prefix = "state")
class RedisConfigurationProperties {
    var redisMocked: Boolean by Delegates.notNull()

    fun startInMemoryRedisIfMocked() {
        if (redisMocked) {
            startRedisMocked()
        }
    }
}