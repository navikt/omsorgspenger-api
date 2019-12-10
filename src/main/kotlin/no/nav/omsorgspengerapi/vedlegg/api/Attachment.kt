package no.nav.omsorgspengerapi.vedlegg.api

import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.MediaType
import reactor.core.publisher.Flux

data class Attachment(
        val content: Flux<DataBuffer>,
        val contentType: MediaType,
        val title: String
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Attachment

        if (content != other.content) return false
        if (contentType != other.contentType) return false
        if (title != other.title) return false

        return true
    }
}