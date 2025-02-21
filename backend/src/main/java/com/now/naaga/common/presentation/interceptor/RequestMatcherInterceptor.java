package com.now.naaga.common.presentation.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.ArrayList;
import java.util.List;

public class RequestMatcherInterceptor implements HandlerInterceptor {

    private static final List<HttpMethod> ALL_HTTP_METHODS = new ArrayList<>(List.of(HttpMethod.values()));

    private final HandlerInterceptor handlerInterceptor;

    private final List<RequestPattern> includedRequestPatterns = new ArrayList<>();

    private final List<RequestPattern> excludedRequestPatterns = new ArrayList<>();


    public RequestMatcherInterceptor(final HandlerInterceptor handlerInterceptor) {
        this.handlerInterceptor = handlerInterceptor;
    }

    @Override
    public boolean preHandle(final HttpServletRequest request,
                             final HttpServletResponse response,
                             final Object handler) throws Exception {
        if (isIncludedRequestPattern(request)) {
            return handlerInterceptor.preHandle(request, response, handler);
        }
        return true;
    }

    private boolean isIncludedRequestPattern(final HttpServletRequest request) {
        final String requestPath = request.getServletPath();
        final String requestMethod = request.getMethod();

        final boolean isIncludedPattern = includedRequestPatterns.stream()
                .anyMatch(requestPattern -> requestPattern.match(requestPath, requestMethod));

        final boolean isNotExcludedPattern = excludedRequestPatterns.stream()
                .noneMatch(requestPattern -> requestPattern.match(requestPath, requestMethod));

        return isIncludedPattern && isNotExcludedPattern;
    }

    public RequestMatcherInterceptor includeRequestPattern(final String requestPathPattern,
                                                           final HttpMethod... requestMethods) {
        final List<HttpMethod> mappingRequestMethods = decideRequestMethods(requestMethods);

        for (HttpMethod httpMethod : mappingRequestMethods) {
            this.includedRequestPatterns.add(new RequestPattern(requestPathPattern, httpMethod));
        }

        return this;
    }

    public RequestMatcherInterceptor excludeRequestPattern(final String requestPathPattern,
                                                           final HttpMethod... requestMethods) {
        final List<HttpMethod> mappingRequestMethods = decideRequestMethods(requestMethods);

        for (HttpMethod httpMethod : mappingRequestMethods) {
            this.excludedRequestPatterns.add(new RequestPattern(requestPathPattern, httpMethod));
        }

        return this;
    }

    private List<HttpMethod> decideRequestMethods(final HttpMethod[] requestMethods) {
        final List<HttpMethod> httpMethods = new ArrayList<>(List.of(requestMethods));

        if (httpMethods.isEmpty()) {
            httpMethods.addAll(ALL_HTTP_METHODS);
        }

        return httpMethods;
    }
}
