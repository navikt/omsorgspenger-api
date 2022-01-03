package no.nav.omsorgspenger.redis

import io.lettuce.core.RedisClient

internal object RedisConfig {

    internal fun redisClient(redisHost: String, redisPort: Int): RedisClient {
        return RedisClient.create("redis://${redisHost}:${redisPort}")
    }

}