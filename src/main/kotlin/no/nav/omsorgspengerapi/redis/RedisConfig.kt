package no.nav.omsorgspengerapi.redis

import io.lettuce.core.RedisClient
import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(RedisProperties::class)
class RedisConfig(private val redisConfigurationProperties: RedisConfigurationProperties) {

    @Bean
    fun redisClient(properties: RedisProperties): RedisClient {
        redisConfigurationProperties.startInMemoryRedisIfMocked()

        return RedisClient.create("redis://${properties.host}:${properties.port}")
    }

}