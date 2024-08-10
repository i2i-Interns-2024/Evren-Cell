package com.i2i.aom.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Evrencell",
                        url = "https://github.com/i2i-Interns-2024/Evren-Cell/tree/main"
                ),
                description = "OpenAPI documentation for Evrencell project",
                title = "OpenAPI Specification - Evrencell",
                version = "1.0"
        )
)
public class OpenApiConfiguration {
}
