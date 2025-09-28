package jakarta.transaction;

/**
 * Specifies a timeout for a transaction. This option is
 * always a hint and may be ignored by the implementation.
 */
public class TransactionTimeout implements TransactionOption {
    private final int milliseconds;

    private TransactionTimeout(int milliseconds) {
        this.milliseconds = milliseconds;
    }

    /**
     * Specify a timeout in seconds.
     * (Abbreviation of {@link #seconds(int)}.)
     */
    public static TransactionTimeout s(int seconds) {
        return new TransactionTimeout(seconds*1_000);
    }

    /**
     * Specify a timeout in milliseconds.
     * (Abbreviation of {@link #milliseconds(int)}.)
     */
    public static TransactionTimeout ms(int milliseconds) {
        return new TransactionTimeout(milliseconds);
    }

    /**
     * Specify a timeout in seconds.
     */
    public static TransactionTimeout seconds(int seconds) {
        return new TransactionTimeout(seconds*1_000);
    }

    /**
     * Specify a timeout in milliseconds.
     */
    public static TransactionTimeout milliseconds(int milliseconds) {
        return new TransactionTimeout(milliseconds);
    }

    /**
     * The timeout in milliseconds.
     */
    public int milliseconds() {
        return milliseconds;
    }
}
