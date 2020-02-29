package io.acrosafe.wallet.btc.exception;

public class InvalidSymbolException extends Exception
{
    private static final long serialVersionUID = 6867113534483061629L;

    /**
     * Constructs new InvalidCoinSymbolException instance.
     */
    public InvalidSymbolException()
    {
        super();
    }

    /**
     * Constructs new InvalidCoinSymbolException.
     *
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public InvalidSymbolException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * Constructs new InvalidCoinSymbolException.
     *
     * @param message
     * @param cause
     */
    public InvalidSymbolException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Constructs new InvalidCoinSymbolException.
     *
     * @param message
     */
    public InvalidSymbolException(String message)
    {
        super(message);
    }

    /**
     * Constructs new InvalidCoinSymbolException.
     *
     * @param cause
     */
    public InvalidSymbolException(Throwable cause)
    {
        super(cause);
    }
}
