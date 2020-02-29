package io.acrosafe.wallet.btc.exception;

public class FeeRecordNotFoundException extends Exception
{
    private static final long serialVersionUID = -1252444294101895291L;

    public FeeRecordNotFoundException()
    {
        super();
    }

    /**
     * Constructs new FeeRecordNotFoundException.
     *
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public FeeRecordNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * Constructs new FeeRecordNotFoundException.
     *
     * @param message
     * @param cause
     */
    public FeeRecordNotFoundException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Constructs new FeeRecordNotFoundException.
     *
     * @param message
     */
    public FeeRecordNotFoundException(String message)
    {
        super(message);
    }

    /**
     * Constructs new FeeRecordNotFoundException.
     *
     * @param cause
     */
    public FeeRecordNotFoundException(Throwable cause)
    {
        super(cause);
    }
}
