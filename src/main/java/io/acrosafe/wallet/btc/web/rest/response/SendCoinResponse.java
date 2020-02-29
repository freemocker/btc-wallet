package io.acrosafe.wallet.btc.web.rest.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SendCoinResponse extends Response
{
    @JsonProperty("transaction_id")
    private String transactionId;

    @JsonProperty("hex")
    private String hex;

    @JsonProperty("fee")
    private String fee;

    @JsonProperty("total_amout")
    private String totalAmount;

    @JsonProperty("number_blocks")
    private Integer numberBlocks;

    public String getHex()
    {
        return hex;
    }

    public void setHex(String hex)
    {
        this.hex = hex;
    }

    public String getFee()
    {
        return fee;
    }

    public void setFee(String fee)
    {
        this.fee = fee;
    }

    public String getTransactionId()
    {
        return transactionId;
    }

    public void setTransactionId(String transactionId)
    {
        this.transactionId = transactionId;
    }

    public String getTotalAmount()
    {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount)
    {
        this.totalAmount = totalAmount;
    }

    public Integer getNumberBlocks()
    {
        return numberBlocks;
    }

    public void setNumberBlocks(Integer numberBlocks)
    {
        this.numberBlocks = numberBlocks;
    }
}
