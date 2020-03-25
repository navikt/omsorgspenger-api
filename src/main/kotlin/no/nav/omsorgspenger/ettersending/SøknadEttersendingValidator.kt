package no.nav.omsorgspenger.ettersending

import no.nav.helse.dusseldorf.ktor.core.*
import no.nav.omsorgspenger.vedlegg.Vedlegg
import java.net.URL

private const val MAX_VEDLEGG_SIZE = 24 * 1024 * 1024 // 3 vedlegg på 8 MB //TODO: Må oppdaters i henhold til hvor mange vedlegg vi tillater, sjekk med klient
private val vedleggTooLargeProblemDetails = DefaultProblemDetails( //TODO: Denne feilmeldingen må oppdaters i henhold til MAX_VEDLEGG_SIZE
    title = "attachments-too-large",
    status = 413,
    detail = "Totale størreslsen på alle vedlegg overstiger maks på 24 MB."
)

internal fun SøknadEttersending.valider() {
    val violations: MutableSet<Violation> = mutableSetOf<Violation>()

    //TODO:Validering

    if (violations.isNotEmpty()) {
        throw Throwblem(ValidationProblemDetails(violations))
    }
}

internal fun List<Vedlegg>.validerVedlegg(vedleggUrler: List<URL>) {
    if (size != vedleggUrler.size) {
        throw Throwblem(
            ValidationProblemDetails(
                violations = setOf(
                    Violation(
                        parameterName = "vedlegg",
                        parameterType = ParameterType.ENTITY,
                        reason = "Mottok referanse til ${vedleggUrler.size} vedlegg, men fant kun $size vedlegg.",
                        invalidValue = vedleggUrler
                    )
                )
            )
        )
    }
    validerTotalStorresle()
}

private fun List<Vedlegg>.validerTotalStorresle() {
    val totalSize = sumBy { it.content.size }
    if (totalSize > MAX_VEDLEGG_SIZE) {
        throw Throwblem(vedleggTooLargeProblemDetails)
    }
}