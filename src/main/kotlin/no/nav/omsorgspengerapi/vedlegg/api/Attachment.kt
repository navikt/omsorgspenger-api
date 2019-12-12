package no.nav.omsorgspengerapi.vedlegg.api

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.core.io.buffer.DataBuffer
import reactor.core.publisher.Flux

data class AttachmentFile(
        val content: Flux<DataBuffer>,
        val contentType: String,
        val title: String
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AttachmentFile

        if (content != other.content) return false
        if (contentType != other.contentType) return false
        if (title != other.title) return false

        return true
    }

    override fun hashCode(): Int {
        var result = content.hashCode()
        result = 31 * result + contentType.hashCode()
        result = 31 * result + title.hashCode()
        return result
    }
}

data class AttachmentJson(
        val content: ByteArray,
        @JsonProperty("content_type") val contentType: String,
        val title: String
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AttachmentJson

        if (!content.contentEquals(other.content)) return false
        if (contentType != other.contentType) return false
        if (title != other.title) return false

        return true
    }

    override fun hashCode(): Int {
        var result = content.contentHashCode()
        result = 31 * result + contentType.hashCode()
        result = 31 * result + title.hashCode()
        return result
    }
}