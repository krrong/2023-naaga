package com.now.naaga.common.config;

import com.now.naaga.auth.presentation.argumentresolver.MemberAuthArgumentResolver;
import com.now.naaga.auth.presentation.argumentresolver.PlayerArgumentResolver;
import com.now.naaga.auth.presentation.interceptor.AuthInterceptor;
import com.now.naaga.auth.presentation.interceptor.ManagerAuthInterceptor;
import com.now.naaga.common.presentation.interceptor.RequestMatcherInterceptor;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final PlayerArgumentResolver playerArgumentResolver;

    private final MemberAuthArgumentResolver memberAuthArgumentResolver;

    private final AuthInterceptor authInterceptor;

    private final ManagerAuthInterceptor managerAuthInterceptor;

    @Value("${manager.origin-url}")
    private String managerUriPath;

    public WebConfig(final PlayerArgumentResolver playerArgumentResolver,
                     final MemberAuthArgumentResolver memberAuthArgumentResolver,
                     final AuthInterceptor authInterceptor,
                     final ManagerAuthInterceptor managerAuthInterceptor) {
        this.playerArgumentResolver = playerArgumentResolver;
        this.memberAuthArgumentResolver = memberAuthArgumentResolver;
        this.authInterceptor = authInterceptor;
        this.managerAuthInterceptor = managerAuthInterceptor;
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(mapAuthInterceptor());
        registry.addInterceptor(mapManagerAuthInterceptor());
    }

    private HandlerInterceptor mapAuthInterceptor() {
        return new RequestMatcherInterceptor(authInterceptor)
                .includeRequestPattern("/**")
                .excludeRequestPattern("/h2-console/**")
                .excludeRequestPattern("/auth/**")
                .excludeRequestPattern("/**/*.png")
                .excludeRequestPattern("/**/*.jpg")
                .excludeRequestPattern("/**/*.jpeg")
                .excludeRequestPattern("/**/*.gif")
                .excludeRequestPattern("/**/*.ico")
                .excludeRequestPattern("/ranks")
                .excludeRequestPattern("/**", HttpMethod.OPTIONS)
                .excludeRequestPattern("/temporary-places", HttpMethod.GET)
                .excludeRequestPattern("/places", HttpMethod.POST)
                .excludeRequestPattern("/temporary-places/**", HttpMethod.DELETE);
    }

    private HandlerInterceptor mapManagerAuthInterceptor() {
        return new RequestMatcherInterceptor(managerAuthInterceptor)
                .includeRequestPattern("/temporary-places", HttpMethod.GET)
                .includeRequestPattern("/places", HttpMethod.POST)
                .includeRequestPattern("/temporary-places/**", HttpMethod.DELETE);
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(playerArgumentResolver);
        resolvers.add(memberAuthArgumentResolver);
    }

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(managerUriPath)
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3000);
    }
}
