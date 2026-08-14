package jakarta.transaction;

/**
 * Specifies the behavior of {@link TransactionController#inTransaction}
 * when called in an existing transaction context.
 */
public enum TransactionPropagation implements TransactionOption {
    /**
     * <p>If called outside a transaction context, a new transaction begins;
     * execution continues inside this new transaction context,
     * and the transaction must be completed.</p>
     * <p>If called inside a transaction context, execution must then continue
     * inside this transaction context.</p>
     */
    REQUIRED,
    /**
     * <p>If called outside a transaction context, a new transaction begins;
     * execution continues inside this new transaction context,
     * and the transaction must be completed.</p>
     * <p>If called inside a transaction context, the current transaction
     * is suspended, a new transaction begins, execution continues inside
     * this new transaction context, the transaction must be completed,
     * and the previously suspended transaction must be resumed.</p>
     */
    REQUIRES_NEW,
    /**
     * <p>If called outside a transaction context, a {@link TransactionalException}
     * with a nested {@link TransactionRequiredException} must be thrown.</p>
     * <p>If called inside a transaction context, execution continues in that context.</p>
     */
    MANDATORY
}
