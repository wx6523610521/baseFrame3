package work.chncyl.base.global.config;

import lombok.AllArgsConstructor;
import me.chanjar.weixin.common.redis.JedisWxRedisOps;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import me.chanjar.weixin.mp.config.impl.WxMpRedisConfigImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 微信配置
 */
@AllArgsConstructor
@Configuration
@EnableConfigurationProperties(WxMpProperties.class)
public class WxMpConfiguration {
    private final WxMpProperties properties;

    @Bean
    @ConditionalOnProperty(value = "wx.mp.enable", havingValue = "true")
    public WxMpService wxMpService() {
        final List<WxMpProperties.MpConfig> configs = this.properties.getConfigs();
        if (configs == null) {
            throw new RuntimeException("微信配置信息为空，请检查配置！");
        }

        WxMpService service = new WxMpServiceImpl();
        service.setMultiConfigStorages(configs
                .stream().map(a -> {
                    WxMpDefaultConfigImpl configStorage;
                    if (this.properties.isUseRedis()) {
                        final WxMpProperties.RedisConfig redisConfig = this.properties.getRedisConfig();
                        JedisPoolConfig poolConfig = new JedisPoolConfig();
                        JedisPool jedisPool = null;
                        if (!StringUtils.isEmpty(redisConfig.getPassword())) {
                            jedisPool = new JedisPool(poolConfig, redisConfig.getHost(), redisConfig.getPort(),
                                    redisConfig.getTimeout(), redisConfig.getPassword(), redisConfig.getDatabase());
                        } else {
                            jedisPool = new JedisPool(poolConfig, redisConfig.getHost(), redisConfig.getPort(),
                                    redisConfig.getTimeout(), null, redisConfig.getDatabase());
                        }

                        configStorage = new WxMpRedisConfigImpl(new JedisWxRedisOps(jedisPool), a.getAppId());
                    } else {
                        configStorage = new WxMpDefaultConfigImpl();
                    }

                    configStorage.setAppId(a.getAppId());
                    configStorage.setSecret(a.getSecret());
                    configStorage.setToken(a.getToken());
                    configStorage.setAesKey(a.getAesKey());
                    return configStorage;
                }).collect(Collectors.toMap(WxMpDefaultConfigImpl::getAppId, a -> a, (o, n) -> o)));
        return service;
    }
}
