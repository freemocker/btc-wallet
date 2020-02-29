package io.acrosafe.wallet.btc.exception;

public class CryptoException extends Exception
{
    private static final long serialVersionUID = -9200768138380084914L;

    /**
     * Constructs new CryptoException instance.
     */
    public CryptoException()
    {
        super();
    }

    /**
     * Constructs new CryptoException.
     *
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public CryptoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * Constructs new CryptoException.
     *
     * @param message
     * @param cause
     */
    public CryptoException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Constructs new CryptoException.
     *
     * @param message
     */
    public CryptoException(String message)
    {
        super(message);
    }

    /**
     * Constructs new CryptoException.
     *
     * @param cause
     */
    public CryptoException(Throwable cause)
    {
        super(cause);
    }
}
