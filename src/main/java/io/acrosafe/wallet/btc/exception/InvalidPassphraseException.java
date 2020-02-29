package io.acrosafe.wallet.btc.exception;

public class InvalidPassphraseException extends Exception
{
    private static final long serialVersionUID = -5543938413910532790L;

    /**
     * Constructs new InvalidWalletPassphraseException instance.
     */
    public InvalidPassphraseException()
    {
        super();
    }

    /**
     * Constructs new InvalidWalletPassphraseException.
     *
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public InvalidPassphraseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * Constructs new InvalidWalletPassphraseException.
     *
     * @param message
     * @param cause
     */
    public InvalidPassphraseException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Constructs new InvalidWalletPassphraseException.
     *
     * @param message
     */
    public InvalidPassphraseException(String message)
    {
        super(message);
    }

    /**
     * Constructs new InvalidWalletPassphraseException.
     *
     * @param cause
     */
    public InvalidPassphraseException(Throwable cause)
    {
        super(cause);
    }
}
