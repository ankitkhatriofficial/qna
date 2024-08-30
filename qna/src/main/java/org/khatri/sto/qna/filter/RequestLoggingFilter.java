package org.khatri.sto.qna.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * @author Ankit Khatri
 */

@Slf4j
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    public static final String REQUEST_ID = "requestId";
    private static final String HTTP_METHOD = "http-method";
    private static final String URI = "uri";
    private static final String CLIENT_IP = "clientIP";
    private static final String STATUS = "status";
    private static final String EXECUTION_TIME = "executionTime";

    @Override
    protected void doFilterInternal(HttpServletRequest httpRequest, HttpServletResponse httpResponse, FilterChain chain) throws ServletException, IOException {
        final long startingTime = System.currentTimeMillis();
        try{
            MDC.put(this.REQUEST_ID, UUID.randomUUID().toString());
            MDC.put("user-id", httpRequest.getHeader("user-id"));
            MDC.put("AMBASSADOR-REQUEST-ID", httpRequest.getHeader("AMBASSADOR-REQUEST-ID"));
            chain.doFilter(httpRequest, httpResponse);
        } finally {
            final long executionTime = System.currentTimeMillis() - startingTime;
            log.info(requestWrapper(httpRequest, httpResponse, executionTime),
                    "Time taken to execute [{}] {} is {} ms ::: sourceIp - {} ::: request status - {}", httpRequest.getMethod(), httpRequest.getRequestURI(),
                    executionTime, httpRequest.getRemoteAddr(), httpResponse.getStatus());
        }
    }

    private LogstashMarker requestWrapper(final HttpServletRequest request, final HttpServletResponse response, final long executionTime){
        LogstashMarker marker = Markers.append(this.HTTP_METHOD, request.getMethod());
        marker.and(Markers.append(this.URI, request.getRequestURI())
                .and(Markers.append(this.CLIENT_IP, getClientIp(request)))
                .and(Markers.append(this.STATUS, response.getStatus()))
                .and(Markers.append(this.EXECUTION_TIME, executionTime)));
        return marker;
    }

    private String getClientIp(final HttpServletRequest request){
        final String X_FORWARDED_FOR = "X-Forwarded-For";
        return request.getHeader(X_FORWARDED_FOR) != null ? request.getHeader(X_FORWARDED_FOR).split(",")[0] : request.getRemoteAddr();
    }
}
