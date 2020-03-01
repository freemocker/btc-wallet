/**
 * MIT License
 *
 * Copyright (c) 2020 acrosafe technologies
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.acrosafe.wallet.btc.exception;

public class TransactionAlreadyBroadcastedException extends Exception
{

    /**
     * Constructs new TransactionAlreadyBroadcastedException instance.
     */
    public TransactionAlreadyBroadcastedException()
    {
        super();
    }

    /**
     * Constructs new TransactionAlreadyBroadcastedException.
     *
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public TransactionAlreadyBroadcastedException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * Constructs new TransactionAlreadyBroadcastedException.
     *
     * @param message
     * @param cause
     */
    public TransactionAlreadyBroadcastedException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Constructs new TransactionAlreadyBroadcastedException.
     *
     * @param message
     */
    public TransactionAlreadyBroadcastedException(String message)
    {
        super(message);
    }

    /**
     * Constructs new TransactionAlreadyBroadcastedException.
     *
     * @param cause
     */
    public TransactionAlreadyBroadcastedException(Throwable cause)
    {
        super(cause);
    }
}
