package com.babel.employeeservice.config;

import com.babel.employeeservice.dto.exception.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.PAYLOAD_TOO_LARGE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Rejects requests whose declared Content-Length exceeds the configured limit before the
 * body is read/parsed, protecting against oversized JSON payloads (e.g. a huge array in
 * POST /v1/employees).
 */
@Component
@Order(1)
public class RequestSizeLimitFilter implements Filter {

    private final ObjectMapper objectMapper;
    private final long maxBodySizeBytes;

    public RequestSizeLimitFilter(ObjectMapper objectMapper,
                                  @Value("${app.request.max-body-size-bytes:1048576}") long maxBodySizeBytes) {
        this.objectMapper = objectMapper;
        this.maxBodySizeBytes = maxBodySizeBytes;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        long contentLength = httpRequest.getContentLengthLong();
        if (contentLength > maxBodySizeBytes) {
            writeTooLarge(httpRequest, httpResponse, contentLength);
            return;
        }
        chain.doFilter(request, response);
    }

    private void writeTooLarge(HttpServletRequest request, HttpServletResponse response, long contentLength)
            throws IOException {
        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(PAYLOAD_TOO_LARGE.value())
                .error(PAYLOAD_TOO_LARGE.getReasonPhrase())
                .message(String.format("Request body (%d bytes) exceeds the maximum allowed size (%d bytes)",
                        contentLength, maxBodySizeBytes))
                .path(request.getRequestURI())
                .build();

        response.setStatus(PAYLOAD_TOO_LARGE.value());
        response.setContentType(APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}
