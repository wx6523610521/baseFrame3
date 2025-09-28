package work.chncyl.base.global.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import work.chncyl.base.global.config.WxUtils;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "wx.mp")
public class WxMpProperties {

    private boolean enable;
    /**
     * 是否使用redis存储access token
     */
    private boolean useRedis;

    /**
     * redis 配置
     */
    private RedisConfig redisConfig;
    /**
     * 多个公众号配置信息
     */
    private List<MpConfig> configs;

    @Override
    public String toString() {
        return WxUtils.toJson(this);
    }

    @Data
    public static class RedisConfig {
        /**
         * redis服务器 主机地址
         */
        private String host;

        /**
         * redis服务器 端口号
         */
        private Integer port;

        /**
         * redis服务器 密码
         */
        private String password;

        /**
         * redis 服务连接超时时间
         */
        private Integer timeout;

        private Integer database;
    }

    @Data
    public static class MesTemplate {
        private String id;
        private String name;
    }

    @Data
    public static class MpConfig {
        /**
         * 设置微信公众号的appid
         */
        private String appId;

        /**
         * 设置微信公众号的app secret
         */
        private String secret;

        /**
         * 设置微信公众号的token
         */
        private String token;

        /**
         * 设置微信公众号的EncodingAESKey
         */
        private String aesKey;
        //app名称
        private String appName;
        //公众号前端路径
        private String rooUrl;

        private List<MesTemplate> messageTemplate;
    }
}
