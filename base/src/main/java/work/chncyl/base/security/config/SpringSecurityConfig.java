package work.chncyl.base.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;
import work.chncyl.base.global.tools.requestTool.filter.CustomRequestFilter;
import work.chncyl.base.security.SecurityHandlerConfig;
import work.chncyl.base.security.annotation.AnonymousAccess;
import work.chncyl.base.security.filter.CustomUsernamePasswordAuthenticationFilter;
import work.chncyl.base.security.filter.JwtAuthenticationFilter;
import work.chncyl.base.security.filter.VerifyCodeFilter;
import work.chncyl.base.security.processor.ClientLoginAuthProvider;
import work.chncyl.base.security.utils.CheckPwdUtils;
import work.chncyl.base.security.utils.JwtUtil;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * SpringSecurity配置
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig {
    /**
     * 登录接口地址
     */
    public static String loginUrl = "/TokenAuth/login";

    private final RequestMappingHandlerMapping handlerMapping;

    private final UserDetailsService userDetailsService;

    private final JwtUtil jwtUtil;

    private final CheckPwdUtils checkPwdUtils;

    private final AuthenticationConfiguration authenticationConfiguration;

    /**
     * 匿名接口
     */
    private static final String[] ANONYMOUS_URL = new String[]{
            "/",
            // Swagger基础路径
            "/swagger-ui.html",
            "/swagger-ui/ **",          // Swagger UI 3.x资源
            "/doc.html",               // Knife4j文档页
            "/knife4j/ **",             // Knife4j静态资源
            "/favicon.ico",
            "/webjars/ **",             // WebJars资源（含Swagger UI依赖）
            "/**.html", "/**.css", "/**.js",
            // OpenAPI文档数据
            "/swagger-resources/ **",
            "/springdoc/**",
            "/v3/**",
            "/v3/api-docs/**",
            "/webjars/**",// API文档JSON
            "/web/login"
    };
    /**
     * 限制为GET请求的匿名接口
     */
    private static final String[] GET_ANONYMOUS_URL = new String[]{};
    /**
     * 限制为POST请求的匿名接口
     */
    private static final String[] POST_ANONYMOUS_URL = new String[]{};

    private JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(this.jwtUtil);
    }

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz
                        // 有AnonymousAccess注解的接口
                        .requestMatchers(getAnonymousUrls())
                        .permitAll()
                        // 指定的匿名接口
                        .requestMatchers(ANONYMOUS_URL)
                        .permitAll()
                        // 限制get请求匿名接口
                        .requestMatchers(HttpMethod.GET, GET_ANONYMOUS_URL)
                        .permitAll()
                        // 限制post请求匿名接口
                        .requestMatchers(HttpMethod.POST, POST_ANONYMOUS_URL)
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                // 配置session管理器，设置session为无状态，此时对登录成功的用户不会创建Session
                .sessionManagement(sessionManage -> sessionManage.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 登出配置
                .logout(out -> out.addLogoutHandler(SecurityHandlerConfig.logoutHandler())
                        .logoutUrl("/api/v1/auth/logout")
                        .logoutSuccessHandler(SecurityHandlerConfig.logoutSuccessHandler()))
                // 配置登录信息
                .formLogin(login -> login.loginProcessingUrl(loginUrl)
                        .passwordParameter("password")
                        .usernameParameter("userName")
                        .successHandler(SecurityHandlerConfig.loginSuccessHandler())
                )
                // 配置认证管理器
                .authenticationProvider(authenticationProvider())
                .addFilterAt(new CustomUsernamePasswordAuthenticationFilter(loginUrl,authenticationConfiguration.getAuthenticationManager()), UsernamePasswordAuthenticationFilter.class)
                // 增加jwt验证过滤器
                .addFilterBefore(jwtAuthenticationFilter(), CustomUsernamePasswordAuthenticationFilter.class)
                /*
                    增加自封装请求过滤器，实现请求体的多次读取
                    当前仅限制登录请求允许多次读取，不允许其他请求允许多次读取，可在CustomRequestFilter内配置
                 */
                .addFilterBefore(new CustomRequestFilter(), CustomUsernamePasswordAuthenticationFilter.class)
                // 增加验证码验证过滤器
                .addFilterBefore(new VerifyCodeFilter(), CustomUsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * 获取AnonymousAccess注解标注的匿名接口
     */
    private String[] getAnonymousUrls() {
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = this.handlerMapping.getHandlerMethods();
        Set<String> allAnonymousAccess = new HashSet<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> infoEntry : handlerMethods.entrySet()) {
            HandlerMethod value = infoEntry.getValue();
            AnonymousAccess annotation = Optional.ofNullable(value.getBean().getClass().getAnnotation(AnonymousAccess.class)).orElse(value.getMethodAnnotation(AnonymousAccess.class));
            if (annotation != null) {
                if (infoEntry.getKey().getPatternsCondition() != null) {
                    allAnonymousAccess.addAll(infoEntry.getKey().getPatternsCondition().getPatterns());
                    continue;
                }
                if (infoEntry.getKey().getPathPatternsCondition() != null)
                    allAnonymousAccess.addAll(infoEntry.getKey().getPathPatternsCondition().getPatterns().stream().map(PathPattern::getPatternString).toList());
            }
        }
        return allAnonymousAccess.toArray(new String[0]);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        ClientLoginAuthProvider provider = new ClientLoginAuthProvider(passwordEncoder(), this.checkPwdUtils);
        provider.setUserDetailsService(this.userDetailsService);
        return provider;
    }

}