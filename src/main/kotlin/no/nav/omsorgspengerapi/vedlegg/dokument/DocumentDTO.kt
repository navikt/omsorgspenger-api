package no.nav.omsorgspengerapi.vedlegg.dokument

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.core.io.buffer.DataBuffer
import reactor.core.publisher.Flux

data class DokumentFilDTO(
        val content: Flux<DataBuffer>,
        val contentType: String,
        val title: String
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DokumentFilDTO

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

data class DocumentJsonDTO(
        val content: ByteArray,
        @JsonProperty("content_type") val contentType: String,
        val title: String
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DocumentJsonDTO

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

data class DocumentIdDTO(val id: String)