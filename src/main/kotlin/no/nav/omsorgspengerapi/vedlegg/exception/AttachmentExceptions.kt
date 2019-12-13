package no.nav.omsorgspengerapi.vedlegg.exception


class DocumentContentTypeNotSupported(message: String) : RuntimeException(message)
class DocumentNotFoundException(message: String) : RuntimeException(message)

class DocumentUploadFailedException(message: String): RuntimeException(message)
