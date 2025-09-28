package jakarta.transaction;

/**
 * An option influencing the behavior of {@link TransactionController#inTransaction}.
 * Built-in options control {@linkplain TransactionPropagation transaction propagation},
 * and {@linkplain TransactionTimeout transaction timeout}.
 *
 * @see TransactionController#inTransaction(TransactionOption...)
 * @see TransactionPropagation
 * @see TransactionTimeout
 */
public interface TransactionOption {
}


