package work.chncyl.base.global.config.doc;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "swagger")
@Data
public class SwaggerConfig {
    private String enabled;

    private String title;

    private String description;

    private String author;

    private String version;

    private List<String> security = new ArrayList<>();

    private List<SwaggerGroupConfigEntity> groupConfigs = new ArrayList<>();
}