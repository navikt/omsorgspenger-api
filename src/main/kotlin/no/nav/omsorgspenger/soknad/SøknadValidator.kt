package no.nav.omsorgspenger.soknad

import no.nav.helse.dusseldorf.ktor.core.*
import no.nav.k9.søknad.ytelse.omsorgspenger.utvidetrett.v1.OmsorgspengerKroniskSyktBarn
import no.nav.omsorgspenger.vedlegg.Vedlegg
import java.net.URL

private const val MAX_VEDLEGG_SIZE = 24 * 1024 * 1024 // 3 vedlegg på 8 MB

private val vedleggTooLargeProblemDetails = DefaultProblemDetails(
    title = "attachments-too-large",
    status = 413,
    detail = "Totale størreslsen på alle vedlegg overstiger maks på 24 MB."
)

internal fun Søknad.valider(k9FormatSøknad: no.nav.k9.søknad.Søknad) {
    val violations: MutableSet<Violation> = this.barn.valider(relasjonTilBarnet = relasjonTilBarnet?.name)

    /*
    // legeerklaring
    if (legeerklæring.isEmpty()) {
        violations.add(
            Violation(
                parameterName = "legeerklaring",
                parameterType = ParameterType.ENTITY,
                reason = "Det må sendes minst et vedlegg for legeerklaring.",
                invalidValue = legeerklæring
            )
        )
    }
    */

    if (samværsavtale != null) {
        if (samværsavtale.isEmpty()) {
            violations.add(
                Violation(
                    parameterName = "samvarsavtale",
                    parameterType = ParameterType.ENTITY,
                    reason = "Det må sendes minst et vedlegg for samvarsavtale.",
                    invalidValue = samværsavtale
                )
            )
        }
    }

    legeerklæring.mapIndexed { index, url ->
        val path = url.path
        // Kan oppstå url = null etter Jackson deserialisering
        if (!path.matches(Regex("/vedlegg/.*"))) {
            violations.add(
                Violation(
                    parameterName = "legeerklæring[$index]",
                    parameterType = ParameterType.ENTITY,
                    reason = "Ikke gyldig vedlegg URL.",
                    invalidValue = url
                )
            )
        }
    }

    samværsavtale?.mapIndexed { index, url ->
        if (url == null) {
            violations.add(
                Violation(
                    parameterName = "samværsavtale[$index]",
                    parameterType = ParameterType.ENTITY,
                    reason = "Ikke gyldig vedlegg URL.",
                    invalidValue = null
                )
            )
        } else {
            val path = url.path
            // Kan oppstå url = null etter Jackson deserialisering
            if (!path.matches(Regex("/vedlegg/.*"))) {
                violations.add(
                    Violation(
                        parameterName = "samværsavtale[$index]",
                        parameterType = ParameterType.ENTITY,
                        reason = "Ikke gyldig vedlegg URL.",
                        invalidValue = url
                    )
                )
            } else {
            }
        }
    }

    if (!harBekreftetOpplysninger) {
        violations.add(
            Violation(
                parameterName = "harBekreftetOpplysninger",
                parameterType = ParameterType.ENTITY,
                reason = "Opplysningene må bekreftes for å sende inn søknad.",
                invalidValue = false

            )
        )
    }

    if (!harForståttRettigheterOgPlikter) {
        violations.add(
            Violation(
                parameterName = "harForståttRettigheterOgPlikter",
                parameterType = ParameterType.ENTITY,
                reason = "Må ha forstått rettigheter og plikter for å sende inn søknad.",
                invalidValue = false
            )
        )
    }

    violations.addAll(validerK9Format(k9FormatSøknad))

    if (violations.isNotEmpty()) {
        throw Throwblem(ValidationProblemDetails(violations))
    }
}

private fun validerK9Format(k9FormatSøknad: no.nav.k9.søknad.Søknad): MutableSet<Violation> =
    OmsorgspengerKroniskSyktBarn.MinValidator().valider(k9FormatSøknad.getYtelse<OmsorgspengerKroniskSyktBarn>()).map {
        Violation(
            parameterName = it.felt,
            parameterType = ParameterType.ENTITY,
            reason = it.feilmelding,
            invalidValue = "K9-format feilkode: ${it.feilkode}"
        )
    }.sortedBy { it.reason }.toMutableSet()

private fun Barn.valider(relasjonTilBarnet: String?): MutableSet<Violation> {
    val violations = mutableSetOf<Violation>()

    if (norskIdentifikator.isNullOrBlank() || (!norskIdentifikator!!.erGyldigNorskIdentifikator())) {
        violations.add(
            Violation(
                parameterName = "barn.norskIdentifikator",
                parameterType = ParameterType.ENTITY,
                reason = "Ikke gyldig norskIdentifikator.",
                invalidValue = norskIdentifikator
            )
        )
    }
    if (norskIdentifikator.isNullOrBlank() && aktørId.isNullOrBlank()) {
        violations.add(
            Violation(
                parameterName = "barn",
                parameterType = ParameterType.ENTITY,
                reason = "Ikke tillatt med barn som mangler norskIdentifikator og aktørID.",
                invalidValue = norskIdentifikator
            )
        )
    }

    if (navn.erBlankEllerLengreEnn(100)) {
        violations.add(
            Violation(
                parameterName = "barn.navn",
                parameterType = ParameterType.ENTITY,
                reason = "Navn på barnet kan ikke være tomt, og kan maks være 100 tegn.",
                invalidValue = navn
            )
        )
    }

    if ((relasjonTilBarnet != null) && (relasjonTilBarnet.erBlankEllerLengreEnn(100))) {
        violations.add(
            Violation(
                parameterName = "relasjon_til_barnet",
                parameterType = ParameterType.ENTITY,
                reason = "Relasjon til barnet kan ikke være tom og være mindre enn 100 tegn.",
                invalidValue = relasjonTilBarnet
            )
        )
    }

    return violations
}

private fun String.erBlankEllerLengreEnn(maxLength: Int): Boolean = isBlank() || length > maxLength

internal fun List<Vedlegg>.validerVedlegg(path: String, vedleggUrler: List<URL>) {
    if (size != vedleggUrler.size) {
        throw Throwblem(
            ValidationProblemDetails(
                violations = setOf(
                    Violation(
                        parameterName = "$path",
                        parameterType = ParameterType.ENTITY,
                        reason = "Mottok referanse til ${vedleggUrler.size} vedlegg, men fant kun $size vedlegg.",
                        invalidValue = vedleggUrler
                    )
                )
            )
        )
    }
    validerTotalStørresle()
}

fun List<Vedlegg>.validerTotalStørresle() {
    val totalSize = sumOf { it.content.size }
    if (totalSize > MAX_VEDLEGG_SIZE) {
        throw Throwblem(vedleggTooLargeProblemDetails)
    }
}
