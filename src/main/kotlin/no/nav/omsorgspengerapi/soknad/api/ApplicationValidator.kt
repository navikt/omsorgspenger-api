package no.nav.omsorgspengerapi.soknad.api

import no.nav.omsorgspengerapi.barn.api.ChildV1
import no.nav.omsorgspengerapi.soknad.mottak.Utenlandsopphold
import no.nav.omsorgspengerapi.vedlegg.document.DocumentJson
import java.net.URL
import java.time.format.DateTimeFormatter

private val KUN_SIFFER = Regex("\\d+")
internal val vekttallProviderFnr1: (Int) -> Int = { arrayOf(3, 7, 6, 1, 8, 9, 4, 5, 2).reversedArray()[it] }
internal val vekttallProviderFnr2: (Int) -> Int = { arrayOf(5, 4, 3, 2, 7, 6, 5, 4, 3, 2).reversedArray()[it] }
private val fnrDateFormat = DateTimeFormatter.ofPattern("ddMMyy")

enum class ParameterType {
    QUERY,
    PATH,
    HEADER,
    ENTITY,
    FORM
}

data class Violation(val parameterName: String, val parameterType: ParameterType, val reason: String, val invalidValue: Any? = null)

internal fun ApplicationV1.validate() {
    val violations: MutableSet<Violation> = this.barn.validate(relasjonTilBarnet = relasjonTilBarnet?.name)

    // legeerklaring
    if (legeerklaring.isEmpty()) {
        violations.add(
                Violation(
                        parameterName = "legeerklaring",
                        parameterType = ParameterType.ENTITY,
                        reason = "Det må sendes minst et vedlegg for legeerklaring.",
                        invalidValue = legeerklaring
                )
        )
    }

    // samvarsavtale
    if (samvarsavtale != null) {
        if (samvarsavtale.isEmpty()) {
            violations.add(
                    Violation(
                            parameterName = "samvarsavtale",
                            parameterType = ParameterType.ENTITY,
                            reason = "Det må sendes minst et vedlegg for samvarsavtale.",
                            invalidValue = samvarsavtale
                    )
            )
        }
    }

    legeerklaring.mapIndexed { index, url ->
        val path = url.path
        // Kan oppstå url = null etter Jackson deserialisering
        if (!path.matches(Regex("/vedlegg/.*"))) {
            violations.add(
                    Violation(
                            parameterName = "legeerklaring[$index]",
                            parameterType = ParameterType.ENTITY,
                            reason = "Ikke gyldig vedlegg URL.",
                            invalidValue = url
                    )
            )
        }
    }

    utenlandsopphold.mapIndexed { index, utenlandsopphold ->
        val fraDataErForTilDato = utenlandsopphold.fraOgMed.isBefore(utenlandsopphold.tilOgMed)
        if (!fraDataErForTilDato) {
            violations.add(
                    Violation(
                            parameterName = "Utenlandsopphold[$index]",
                            parameterType = ParameterType.ENTITY,
                            reason = "Til dato kan ikke være før fra dato",
                            invalidValue = "fraOgMed eller tilOgMed"
                    )
            )
        }
        if (utenlandsopphold.landkode.isEmpty()) {
            violations.add(
                    Violation(
                            parameterName = "Utenlandsopphold[$index]",
                            parameterType = ParameterType.ENTITY,
                            reason = "Landkode er ikke satt",
                            invalidValue = "landkode"
                    )
            )
        }
        if (utenlandsopphold.landnavn.isEmpty()) {
            violations.add(
                    Violation(
                            parameterName = "Utenlandsopphold[$index]",
                            parameterType = ParameterType.ENTITY,
                            reason = "Landnavn er ikke satt",
                            invalidValue = "landnavn"
                    )
            )
        }
    }

    if (samvarsavtale != null) {
        samvarsavtale.mapIndexed { index, url ->
            val path = url.path
            // Kan oppstå url = null etter Jackson deserialisering
            if (!path.matches(Regex("/vedlegg/.*"))) {
                violations.add(
                        Violation(
                                parameterName = "samvarsavtale[$index]",
                                parameterType = ParameterType.ENTITY,
                                reason = "Ikke gyldig vedlegg URL.",
                                invalidValue = url
                        )
                )
            }
        }
    }

    // Booleans (For å forsikre at de er satt og ikke blir default false)
    fun booleanIkkeSatt(parameterName: String) {
        violations.add(
                Violation(
                        parameterName = parameterName,
                        parameterType = ParameterType.ENTITY,
                        reason = "Må settes til true eller false.",
                        invalidValue = null

                ))
    }
    if (medlemskap.harBoddIUtlandetSiste12Mnd == null) booleanIkkeSatt("medlemskap.har_bodd_i_utlandet_siste_12_mnd")
    violations.addAll(validerUtenlandopphold(medlemskap.utenlandsoppholdSiste12Mnd))
    if (medlemskap.skalBoIUtlandetNeste12Mnd == null) booleanIkkeSatt("medlemskap.skal_bo_i_utlandet_neste_12_mnd")
    violations.addAll(validerUtenlandopphold(medlemskap.utenlandsoppholdNeste12Mnd))
    if (!harBekreftetOpplysninger) {
        violations.add(
                Violation(
                        parameterName = "har_bekreftet_opplysninger",
                        parameterType = ParameterType.ENTITY,
                        reason = "Opplysningene må bekreftes for å sende inn søknad.",
                        invalidValue = false

                ))
    }
    if (!harForstattRettigheterOgPlikter) {
        violations.add(
                Violation(
                        parameterName = "har_forstatt_rettigheter_og_plikter",
                        parameterType = ParameterType.ENTITY,
                        reason = "Må ha forstått rettigheter og plikter for å sende inn søknad.",
                        invalidValue = false

                ))
    }

// Ser om det er noen valideringsfeil
    if (violations.isNotEmpty()) {
        throw ApplicationValidationException("Failed to validate received application.", violations)
    }
}

private fun ChildV1.validate(relasjonTilBarnet: String?): MutableSet<Violation> {
    val violations = mutableSetOf<Violation>()

    if (fodselsnummer != null && !fodselsnummer.erGyldigFodselsnummer()) {
        violations.add(
                Violation(
                        parameterName = "barn.fodselsnummer",
                        parameterType = ParameterType.ENTITY,
                        reason = "Ikke gyldig fødselsnummer.",
                        invalidValue = fodselsnummer
                )
        )
    }

    if (alternativId != null && (alternativId.erBlankEllerLengreEnn(50) || !alternativId.erKunSiffer())) {
        violations.add(
                Violation(
                        parameterName = "barn.alternativ_id",
                        parameterType = ParameterType.ENTITY,
                        reason = "Ikke gyldig alternativ id. Kan kun inneholde tall og være maks 50 lang.",
                        invalidValue = alternativId
                )
        )
    }

    val kreverNavnPaaBarnet = fodselsnummer != null
    if ((kreverNavnPaaBarnet || navn != null) && (navn == null || navn.erBlankEllerLengreEnn(100))) {
        violations.add(
                Violation(
                        parameterName = "barn.navn",
                        parameterType = ParameterType.ENTITY,
                        reason = "Navn på barnet kan ikke være tomt, og kan maks være 100 tegn.",
                        invalidValue = navn
                )
        )
    }

    val kreverRelasjonPaaBarnet = aktoerId == null
    if ((kreverRelasjonPaaBarnet || relasjonTilBarnet != null) && (relasjonTilBarnet == null || relasjonTilBarnet.erBlankEllerLengreEnn(100))) {
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

fun MutableList<DocumentJson>.validateAttachment(attachmentUrls: List<URL>) {
    if (size != attachmentUrls.size) {
        throw ApplicationValidationException(message = "", violations = mutableSetOf(
                Violation(
                        parameterName = "vedlegg",
                        parameterType = ParameterType.ENTITY,
                        reason = "Mottok referanse til ${attachmentUrls.size} vedlegg, men fant kun $size vedlegg.",
                        invalidValue = attachmentUrls
                )
        ))
    }
    validerTotalStorrelse()
}

private fun validerUtenlandopphold(
        list: List<Utenlandsopphold>
): MutableSet<Violation> {
    val violations = mutableSetOf<Violation>()
    list.mapIndexed { index, utenlandsopphold ->
        val fraDataErEtterTilDato = utenlandsopphold.fraOgMed.isAfter(utenlandsopphold.tilOgMed)
        if (fraDataErEtterTilDato) {
            violations.add(
                    Violation(
                            parameterName = "Utenlandsopphold[$index]",
                            parameterType = ParameterType.ENTITY,
                            reason = "Til dato kan ikke være før fra dato",
                            invalidValue = "fraOgMed eller tilOgMed"
                    )
            )
        }
        if (utenlandsopphold.landkode.isEmpty()) {
            violations.add(
                    Violation(
                            parameterName = "Utenlandsopphold[$index]",
                            parameterType = ParameterType.ENTITY,
                            reason = "Landkode er ikke satt",
                            invalidValue = "landkode"
                    )
            )
        }
        if (utenlandsopphold.landnavn.isEmpty()) {
            violations.add(
                    Violation(
                            parameterName = "Utenlandsopphold[$index]",
                            parameterType = ParameterType.ENTITY,
                            reason = "Landnavn er ikke satt",
                            invalidValue = "landnavn"
                    )
            )
        }
    }
    return violations
}

private fun MutableList<DocumentJson>.validerTotalStorrelse() {
    val MAX_VEDLEGG_SIZE = 24 * 1024 * 1024 // 3 vedlegg på 8 MB

    val totalSize: Int = map { attach -> attach.content.size }.sum()
    if (totalSize > MAX_VEDLEGG_SIZE) {
        throw ApplicationValidationException("Total size of attachments to big.", mutableSetOf(Violation(
                parameterName = "content",
                parameterType = ParameterType.ENTITY,
                reason = "Total file size cannot be more than $MAX_VEDLEGG_SIZE",
                invalidValue = totalSize
        )))
    }
}

fun String.erKunSiffer() = matches(KUN_SIFFER)

private fun String.starterMedFodselsdato(): Boolean {
    // Sjekker ikke hvilket århundre vi skal tolket yy som, kun at det er en gyldig dato.
    // F.eks blir 290990 parset til 2090-09-29, selv om 1990-09-29 var ønskelig.
    // Kunne sett på individsifre (Tre første av personnummer) for å tolke århundre,
    // men virker unødvendig komplekst og sårbart for ev. endringer i fødselsnummeret.
    return try {
        fnrDateFormat.parse(substring(0, 6))
        true
    } catch (cause: Throwable) {
        false
    }
}

private fun String.erBlankEllerLengreEnn(maxLength: Int): Boolean = isBlank() || length > maxLength

fun String.erGyldigFodselsnummer(): Boolean {
    if (length != 11 || !erKunSiffer() || !starterMedFodselsdato()) return false

    val forventetKontrollsifferEn = get(9)

    val kalkulertKontrollsifferEn = Mod11.kontrollsiffer(
            number = substring(0, 9),
            vekttallProvider = vekttallProviderFnr1
    )

    if (kalkulertKontrollsifferEn != forventetKontrollsifferEn) return false

    val forventetKontrollsifferTo = get(10)

    val kalkulertKontrollsifferTo = Mod11.kontrollsiffer(
            number = substring(0, 10),
            vekttallProvider = vekttallProviderFnr2
    )

    return kalkulertKontrollsifferTo == forventetKontrollsifferTo
}

/**
 * https://github.com/navikt/helse-sparkel/blob/2e79217ae00632efdd0d4e68655ada3d7938c4b6/src/main/kotlin/no/nav/helse/ws/organisasjon/Mod11.kt
 * https://www.miles.no/blogg/tema/teknisk/validering-av-norske-data
 */
internal object Mod11 {
    private val defaultVekttallProvider: (Int) -> Int = { 2 + it % 6 }

    internal fun kontrollsiffer(
            number: String,
            vekttallProvider: (Int) -> Int = defaultVekttallProvider
    ): Char {
        return number.reversed().mapIndexed { i, char ->
            Character.getNumericValue(char) * vekttallProvider(i)
        }.sum().let(::kontrollsifferFraSum)
    }


    private fun kontrollsifferFraSum(sum: Int) = sum.rem(11).let { rest ->
        when (rest) {
            0 -> '0'
            1 -> '-'
            else -> "${11 - rest}"[0]
        }
    }
}
