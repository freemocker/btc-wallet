package io.acrosafe.wallet.btc.exception;

public class InvalidRecipientException extends Exception
{
    private static final long serialVersionUID = -2990124762366892206L;

    /**
     * Constructs new InvalidRecipientException instance.
     */
    public InvalidRecipientException()
    {
        super();
    }

    /**
     * Constructs new InvalidRecipientException.
     *
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public InvalidRecipientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * Constructs new InvalidRecipientException.
     *
     * @param message
     * @param cause
     */
    public InvalidRecipientException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Constructs new InvalidRecipientException.
     *
     * @param message
     */
    public InvalidRecipientException(String message)
    {
        super(message);
    }

    /**
     * Constructs new InvalidRecipientException.
     *
     * @param cause
     */
    public InvalidRecipientException(Throwable cause)
    {
        super(cause);
    }
}
