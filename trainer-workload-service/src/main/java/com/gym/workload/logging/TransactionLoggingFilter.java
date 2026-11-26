package com.gym.workload.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class TransactionLoggingFilter extends OncePerRequestFilter {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(TransactionLoggingFilter.class);

    public static final String TRANSACTION_ID_HEADER = "X-Transaction-Id";
    public static final String TRANSACTION_ID_MDC = "transactionId";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String transactionId = request.getHeader(TRANSACTION_ID_HEADER);
        if (transactionId == null || transactionId.isBlank()) {
            transactionId = UUID.randomUUID().toString();
        }

        MDC.put(TRANSACTION_ID_MDC, transactionId);
        long startTime = System.currentTimeMillis();

        try {
            LOGGER.info(
                    "Transaction START | transactionId={} | method={} | uri={}",
                    transactionId,
                    request.getMethod(),
                    request.getRequestURI()
            );

            filterChain.doFilter(request, response);

            LOGGER.info(
                    "Transaction END   | transactionId={} | status={} | durationMs={}",
                    transactionId,
                    response.getStatus(),
                    System.currentTimeMillis() - startTime
            );
        } catch (Exception ex) {
            LOGGER.error(
                    "Transaction ERROR | transactionId={} | uri={} | message={}",
                    transactionId,
                    request.getRequestURI(),
                    ex.getMessage(),
                    ex
            );
            throw ex;
        } finally {
            MDC.remove(TRANSACTION_ID_MDC);
        }
    }
}