package work.chncyl.base.global.config.doc;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * 接口文档配置
 */
@Configuration
@ConditionalOnProperty(prefix = "swagger", name = {"enabled"}, havingValue = "true", matchIfMissing = true)
@Slf4j
@RequiredArgsConstructor
public class SwaggerAutoConfiguration {

    private final SwaggerConfig swaggerConfig;


    /**
     * 创建API应用
     * apiInfo() 增加API相关信息
     * 通过select()函数返回一个ApiSelectorBuilder实例,用来控制哪些接口暴露给Swagger来展现
     * 扫描指定包下的接口
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                // 扫描指定包中的注解
                .apis(RequestHandlerSelectors.basePackage("work.chncyl"))
                // 扫描所有有注解的api，用这种方式更灵活
                // .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * 创建该API的基本信息（这些基本信息会展现在文档页面中）
     */
    private ApiInfo apiInfo() {
        Contact contact = new Contact(!StringUtils.hasLength(this.swaggerConfig.getAuthor()) ? "chncyl" : this.swaggerConfig.getAuthor()
                , !StringUtils.hasLength(this.swaggerConfig.getUrl()) ? "https://github.com/chncyl/spring-boot-starter-swagger" : this.swaggerConfig.getUrl()
                , !StringUtils.hasLength(this.swaggerConfig.getEmail()) ? "chncyl@gmail.com" : this.swaggerConfig.getEmail());

        return new ApiInfoBuilder()
                .title(!StringUtils.hasLength(this.swaggerConfig.getTitle()) ? "" : this.swaggerConfig.getTitle())
                .version(!StringUtils.hasLength(this.swaggerConfig.getVersion()) ? "" : this.swaggerConfig.getVersion())
                .description(!StringUtils.hasLength(this.swaggerConfig.getDescription()) ? "" : this.swaggerConfig.getDescription())
                .contact(contact)
                .version("1.0.0")
                .build();
    }
}