package no.nav.omsorgspengerapi.docs

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.info.License
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.annotations.servers.Server
import org.springframework.stereotype.Component

const val SELVBETJENING_ID_TOKEN_SCHEME = "selvbetjening-idtoken"

@SecurityScheme(
        name = SELVBETJENING_ID_TOKEN_SCHEME,
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "jwt"
)
@OpenAPIDefinition(
        info = Info(title = "Omsorgspengesøknad API",
                version = "0.0.1",
                contact = Contact(name = "Arbeids- og velferdsdirektoratet", url = "https://www.nav.no"),
                description = "API for innsending av søknad om omsorgspenger.",
                license = License(name = "MIT", url = "https://github.com/navikt/omsorgspenger-api/blob/master/LICENCE")
        ),
        servers = [
            Server(url = "http://localhost:8080", description = "Lokalt Miljø"),
            Server(url = "https://omsorgspengesoknad-api-q.nav.no", description = "Staging Miljø")
        ]
)
@Component
class OpenApiConfiguration {

}