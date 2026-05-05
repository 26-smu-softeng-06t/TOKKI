package com.tokki.config;

import java.util.Map;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice(basePackages = "com.tokki.controller")
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
        Object body,
        MethodParameter returnType,
        MediaType selectedContentType,
        Class<? extends HttpMessageConverter<?>> selectedConverterType,
        ServerHttpRequest request,
        ServerHttpResponse response
    ) {
        if (response.getHeaders().containsKey("X-Skip-Response-Wrap")) {
            return body;
        }
        if (response instanceof org.springframework.http.server.ServletServerHttpResponse servletResponse
            && servletResponse.getServletResponse().getStatus() == HttpStatus.NO_CONTENT.value()) {
            return body;
        }
        if (body instanceof Map<?, ?> map && (map.containsKey("data") || map.containsKey("error"))) {
            return body;
        }
        return Map.of("data", body);
    }
}
