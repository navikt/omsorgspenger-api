package no.nav.omsorgspenger.soknad

import no.nav.helse.dusseldorf.ktor.core.*
import no.nav.omsorgspenger.vedlegg.Vedlegg
import java.net.URL
import java.time.format.DateTimeFormatter

private val KUN_SIFFER = Regex("\\d+")
internal val vekttallProviderFnr1: (Int) -> Int = { arrayOf(3, 7, 6, 1, 8, 9, 4, 5, 2).reversedArray()[it] }
internal val vekttallProviderFnr2: (Int) -> Int = { arrayOf(5, 4, 3, 2, 7, 6, 5, 4, 3, 2).reversedArray()[it] }
private val fnrDateFormat = DateTimeFormatter.ofPattern("ddMMyy")

private const val MAX_VEDLEGG_SIZE = 24 * 1024 * 1024 // 3 vedlegg på 8 MB

private val vedleggTooLargeProblemDetails = DefaultProblemDetails(
    title = "attachments-too-large",
    status = 413,
    detail = "Totale størreslsen på alle vedlegg overstiger maks på 24 MB."
)

internal fun Søknad.valider() {
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

// Ser om det er noen valideringsfeil
    if (violations.isNotEmpty()) {
        throw Throwblem(ValidationProblemDetails(violations))
    }
}

private fun BarnDetaljer.gyldigAntallIder(): Boolean {
    val antallIderSatt = listOfNotNull(aktørId, norskIdentifikator).size
    return antallIderSatt == 0 || antallIderSatt == 1
}

private fun BarnDetaljer.valider(relasjonTilBarnet: String?): MutableSet<Violation> {
    val violations = mutableSetOf<Violation>()

    if (norskIdentifikator != null && !norskIdentifikator.erGyldigNorskIdentifikator()) {
        violations.add(
            Violation(
                parameterName = "barn.norskIdentifikator",
                parameterType = ParameterType.ENTITY,
                reason = "Ikke gyldig norskIdentifikator.",
                invalidValue = norskIdentifikator
            )
        )
    }

    /* //TODO 25.02.2021 - Sett på validering når fjerning av fødselsdato er prodsatt i frontend
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
    */

    //TODO 25.02.2021 - Fjerne denne når fødselsdato er fjernet
    if(norskIdentifikator.isNullOrBlank() && fødselsdato == null && aktørId.isNullOrBlank()){
        violations.add(
            Violation(
                parameterName = "barn",
                parameterType = ParameterType.ENTITY,
                reason = "Ikke tillatt med barn som mangler både fødselsdato, aktørID og norskIdentifikator.",
                invalidValue = norskIdentifikator
            )
        )
    }

    if (!gyldigAntallIder()) {
        violations.add(
            Violation(
                parameterName = "barn",
                parameterType = ParameterType.ENTITY,
                reason = "Kan kun sette 'aktørId' eller 'norskIdentifikator' på barnet.",
                invalidValue = null
            )
        )
    }


    val kreverNavnPaaBarnet = norskIdentifikator != null
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

fun String.erKunSiffer() = matches(KUN_SIFFER)

fun String.starterMedFodselsdato(): Boolean {
    // Sjekker ikke hvilket århundre vi skal tolket yy som, kun at det er en gyldig dato.
    // F.eks blir 290990 parset til 2090-09-29, selv om 1990-09-29 var ønskelig.
    // Kunne sett på individsifre (Tre første av personnummer) for å tolke århundre,
    // men virker unødvendig komplekst og sårbart for ev. endringer i fødselsnummeret.
    return try {
        var substring = substring(0, 6)
        val førsteSiffer = (substring[0]).toString().toInt()
        if (førsteSiffer in 4..7) {
            substring = (førsteSiffer - 4).toString() + substring(1, 6)
        }
        fnrDateFormat.parse(substring)

        true
    } catch (cause: Throwable) {
        false
    }
}

private fun String.erBlankEllerLengreEnn(maxLength: Int): Boolean = isBlank() || length > maxLength

internal fun List<Vedlegg>.validerLegeerklæring(vedleggUrler: List<URL>) {
    if (size != vedleggUrler.size) {
        throw Throwblem(
            ValidationProblemDetails(
                violations = setOf(
                    no.nav.helse.dusseldorf.ktor.core.Violation(
                        parameterName = "legeerklæring",
                        parameterType = no.nav.helse.dusseldorf.ktor.core.ParameterType.ENTITY,
                        reason = "Mottok referanse til ${vedleggUrler.size} vedlegg, men fant kun $size vedlegg.",
                        invalidValue = vedleggUrler
                    )
                )
            )
        )
    }
    validerTotalStørresle()
}

internal fun List<Vedlegg>.validerSamværsavtale(vedleggUrler: List<URL>) {
    if (size != vedleggUrler.size) {
        throw Throwblem(
            ValidationProblemDetails(
                violations = setOf(
                    no.nav.helse.dusseldorf.ktor.core.Violation(
                        parameterName = "samværsavtale",
                        parameterType = no.nav.helse.dusseldorf.ktor.core.ParameterType.ENTITY,
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
    val totalSize = sumBy { it.content.size }
    if (totalSize > MAX_VEDLEGG_SIZE) {
        throw Throwblem(vedleggTooLargeProblemDetails)
    }
}

fun String.erGyldigNorskIdentifikator(): Boolean {
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
        }.sum().let(Mod11::kontrollsifferFraSum)
    }


    private fun kontrollsifferFraSum(sum: Int) = sum.rem(11).let { rest ->
        when (rest) {
            0 -> '0'
            1 -> '-'
            else -> "${11 - rest}"[0]
        }
    }
}
