package com.gym.crm.logging;

import org.slf4j.MDC;

import java.util.UUID;

public final class TransactionContext {

    public static final String TRANSACTION_ID = "transactionId";

    private TransactionContext() {
    }

    public static String start() {
        String transactionId = UUID.randomUUID().toString();

        MDC.put(TRANSACTION_ID, transactionId);

        return transactionId;
    }

    public static String current() {
        return MDC.get(TRANSACTION_ID);
    }

    public static void clear() {
        MDC.remove(TRANSACTION_ID);
    }
}