package io.acrosafe.wallet.btc.exception;

public class ServiceNotReadyException extends Exception
{
    private static final long serialVersionUID = -5018367491253364123L;

    /**
     * Constructs new ServiceNotReadyException instance.
     */
    public ServiceNotReadyException()
    {
        super();
    }

    /**
     * Constructs new ServiceNotReadyException.
     *
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public ServiceNotReadyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * Constructs new ServiceNotReadyException.
     *
     * @param message
     * @param cause
     */
    public ServiceNotReadyException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Constructs new ServiceNotReadyException.
     *
     * @param message
     */
    public ServiceNotReadyException(String message)
    {
        super(message);
    }

    /**
     * Constructs new ServiceNotReadyException.
     *
     * @param cause
     */
    public ServiceNotReadyException(Throwable cause)
    {
        super(cause);
    }
}
