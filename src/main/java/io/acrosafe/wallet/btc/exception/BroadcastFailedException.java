package io.acrosafe.wallet.btc.exception;

public class BroadcastFailedException extends Exception
{
    /**
     * Constructs new BroadcastFailedException instance.
     */
    public BroadcastFailedException()
    {
        super();
    }

    /**
     * Constructs new BroadcastFailedException.
     *
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public BroadcastFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * Constructs new BroadcastFailedException.
     *
     * @param message
     * @param cause
     */
    public BroadcastFailedException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Constructs new BroadcastFailedException.
     *
     * @param message
     */
    public BroadcastFailedException(String message)
    {
        super(message);
    }

    /**
     * Constructs new BroadcastFailedException.
     *
     * @param cause
     */
    public BroadcastFailedException(Throwable cause)
    {
        super(cause);
    }
}
