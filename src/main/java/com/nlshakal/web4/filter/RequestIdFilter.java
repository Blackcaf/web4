package com.nlshakal.web4.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.UUID;

/**
 * Фильтр для генерации и добавления уникального ID к каждому HTTP запросу.
 * ID используется для трассировки запросов в логах через SLF4J MDC.
 * Если клиент передает X-Request-ID, он используется, иначе генерируется новый UUID.
 */
@Component
public class RequestIdFilter implements Filter {

    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String MDC_REQUEST_ID = "requestId";

    /**
     * Обрабатывает запрос: генерирует/извлекает Request ID и добавляет его в MDC и заголовки ответа.
     *
     * @param request HTTP запрос
     * @param response HTTP ответ
     * @param chain цепочка фильтров
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestId = httpRequest.getHeader(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }

        MDC.put(MDC_REQUEST_ID, requestId);
        httpResponse.setHeader(REQUEST_ID_HEADER, requestId);

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_REQUEST_ID);
        }
    }
}

