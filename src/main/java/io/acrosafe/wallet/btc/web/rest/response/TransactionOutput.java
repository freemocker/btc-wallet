package io.acrosafe.wallet.btc.web.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionOutput
{
    @JsonProperty("amount_string")
    private String value;

    @JsonProperty("amount_in_smallest_unit_string")
    private String valueInSmallestUnit;

    @JsonProperty("receive_address")
    private String receiveAddress;

    @JsonProperty("index")
    private Integer index;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValueInSmallestUnit() {
        return valueInSmallestUnit;
    }

    public void setValueInSmallestUnit(String valueInSmallestUnit) {
        this.valueInSmallestUnit = valueInSmallestUnit;
    }

    public String getReceiveAddress() {
        return receiveAddress;
    }

    public void setReceiveAddress(String receiveAddress) {
        this.receiveAddress = receiveAddress;
    }

    public Integer getIndex()
    {
        return index;
    }

    public void setIndex(Integer index)
    {
        this.index = index;
    }
}
