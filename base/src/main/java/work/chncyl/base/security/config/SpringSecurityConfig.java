package work.chncyl.base.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;
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

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig {
    @Value("${loginUrl}")
    private String loginUrl;

    private final RequestMappingHandlerMapping handlerMapping;

    private final UserDetailsService userDetailsService;

    private final JwtUtil jwtUtil;

    private final CheckPwdUtils checkPwdUtils;

    private static final String[] ANONYMOUS_URL = new String[]{};
    private static final String[] GET_ANONYMOUS_URL = new String[]{
            "/",
            "/swagger-ui.html",
            "/swagger-ui/",
            "/doc.html",
            "/favicon.ico",
            "/webjars/**",
            "/**.html",
            "/**.css",
            "/**.js",
            "/swagger-resources/**",
            "/springdoc/**",
            "/v3/**",
            "/web/login"};
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
                        //接口文档、静态资源
                        .requestMatchers(HttpMethod.GET, GET_ANONYMOUS_URL)
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, POST_ANONYMOUS_URL)
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                // 配置session管理器，设置session为无状态，此时对登录成功的用户不会创建Session
                .sessionManagement(sessionManage -> sessionManage.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .logout(out -> out.addLogoutHandler(SecurityHandlerConfig.logoutHandler())
                        .logoutUrl("/api/v1/auth/logout")
                        .logoutSuccessHandler(SecurityHandlerConfig.logoutSuccessHandler()))
                // 配置登录信息
                .formLogin(login -> (login.loginProcessingUrl("/TokenAuth/Sigin")
                        .passwordParameter("password")
                        .usernameParameter("userName")
                        .successHandler(SecurityHandlerConfig.loginSuccessHandler()))
                        .loginPage(this.loginUrl))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter(), CustomUsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new VerifyCodeFilter(), CustomUsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

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
        return allAnonymousAccess.<String>toArray(new String[0]);
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