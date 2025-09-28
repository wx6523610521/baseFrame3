package work.chncyl.base.global.config.doc;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class SwaggerGroupConfigEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String group;

    private String pathsToMatch;

    private String packagesToScan;
}