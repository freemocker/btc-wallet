package io.acrosafe.wallet.btc.common;

public enum TransactionStatus
{
    SIGNED("signed"),
    CONFLICT("conflict"),
    FAILED("failed"),
    UNCONFIRMED("unconfirmed"),
    CONFIRMED("confirmed"),
    UNKNOWN("unknown");

    private String status;

    /**
     * Construct new TransactionStatusEnum instance.
     *
     * @param status
     */
    TransactionStatus(String status)
    {
        this.status = status;
    }

    /**
     * Returns transaction status.
     *
     * @return
     */
    public String getStatus()
    {
        return status;
    }
}
