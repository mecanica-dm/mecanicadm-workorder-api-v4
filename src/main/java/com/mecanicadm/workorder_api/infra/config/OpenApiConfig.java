package com.mecanicadm.workorder_api.infra.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("Mecânica DM API")
                        .version("0.0.1")
                        .description("API para o sistema de gestão de mecânicas"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

    @Bean
    public OperationCustomizer addAcceptLanguageHeader() {
        return (operation, handlerMethod) -> {
            if (operation.getParameters() == null) {
                operation.setParameters(new java.util.ArrayList<>());
            }

            operation.addParametersItem(new HeaderParameter()
                    .name("Accept-Language")
                    .description("Idioma para as mensagens de retorno (pt-BR, es, en)")
                    .required(false)
                    .schema(new StringSchema()._default("pt-BR")));

            return operation;
        };
    }

    @Bean
    public OpenApiCustomizer customizePageable() {
        return openApi -> openApi.getPaths().values().stream()
                .flatMap(pathItem -> pathItem.readOperations().stream())
                .filter(operation -> operation.getParameters() != null)
                .forEach(operation -> operation.getParameters().forEach(this::updatePageableParameter));
    }

    private void updatePageableParameter(Parameter parameter) {
        switch (parameter.getName()) {
            case "page" -> {
                parameter.setDescription("Número da página (base zero)");
                parameter.setExample(0);
            }
            case "size" -> {
                parameter.setDescription("Número de itens por página");
                parameter.setExample(20);
            }
            case "sort" -> {
                parameter.setDescription("Critério de ordenação no formato: propriedade,asc ou propriedade,desc. Exemplo: name,asc");
                parameter.setSchema(new StringSchema());
                parameter.setExplode(false);
            }

            default -> {
                // No action
            }
        }
    }
}
