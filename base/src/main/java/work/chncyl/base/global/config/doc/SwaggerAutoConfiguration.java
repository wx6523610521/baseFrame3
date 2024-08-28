package work.chncyl.base.global.config.doc;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * 接口文档配置
 */
@Configuration
@ConditionalOnProperty(prefix = "swagger", name = {"enabled"}, havingValue = "true", matchIfMissing = true)
@Slf4j
@RequiredArgsConstructor
public class SwaggerAutoConfiguration {

    private SwaggerConfig swaggerConfig;


    @Bean
    public OpenAPI openAPI() {
        log.info("swggger OpenAPI+ ");
        OpenAPI openApi = new OpenAPI();
        openApi.setInfo(getInfo());
        openApi.setExternalDocs(externalDocumentation());
        securitySchemes(openApi);
        return openApi;
    }

    private void securitySchemes(OpenAPI openApi) {
        if (this.swaggerConfig != null && this.swaggerConfig.getSecurity() != null && !this.swaggerConfig.getSecurity().isEmpty())
            this.swaggerConfig.getSecurity().forEach(string -> {
                SecurityScheme securityScheme = new SecurityScheme()
                        .name(string)
                        .bearerFormat("JWT")
                        .type(SecurityScheme.Type.HTTP)
                        .in(SecurityScheme.In.HEADER)
                        .description(string);
                openApi.schemaRequirement(string, securityScheme).addSecurityItem((new SecurityRequirement()).addList(string));
            });
    }

    private Info getInfo() {
        if (this.swaggerConfig == null)
            this.swaggerConfig = new SwaggerConfig();
        Contact contact = (new Contact()).name(!StringUtils.hasLength(this.swaggerConfig.getAuthor()) ? "": this.swaggerConfig.getAuthor());
        return (new Info())
                .title(!StringUtils.hasLength(this.swaggerConfig.getTitle()) ? "": this.swaggerConfig.getTitle())
                        .version(!StringUtils.hasLength(this.swaggerConfig.getVersion()) ? "": this.swaggerConfig.getVersion())
                                .description(!StringUtils.hasLength(this.swaggerConfig.getDescription()) ? "": this.swaggerConfig.getDescription())
                                        .contact(contact);
    }

    private ExternalDocumentation externalDocumentation() {
        return (new ExternalDocumentation())
                .description("")
                .url("https://www.chncyl.work/");
    }
}