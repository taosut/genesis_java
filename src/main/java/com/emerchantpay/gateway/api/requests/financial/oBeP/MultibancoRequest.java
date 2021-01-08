package com.emerchantpay.gateway.api.requests.financial.oBeP;

import com.emerchantpay.gateway.api.Request;
import com.emerchantpay.gateway.api.RequestBuilder;
import com.emerchantpay.gateway.api.constants.TransactionTypes;
import com.emerchantpay.gateway.api.exceptions.InvalidParamException;
import com.emerchantpay.gateway.api.interfaces.BillingAddressAttributes;
import com.emerchantpay.gateway.api.interfaces.ShippingAddressAttributes;
import com.emerchantpay.gateway.api.interfaces.customerinfo.CustomerInfoAttributes;
import com.emerchantpay.gateway.api.interfaces.financial.AsyncAttributes;
import com.emerchantpay.gateway.api.interfaces.financial.PaymentAttributes;
import com.emerchantpay.gateway.api.validation.GenesisValidator;
import com.emerchantpay.gateway.api.validation.RequiredParameters;
import com.emerchantpay.gateway.util.Country;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultibancoRequest extends Request implements PaymentAttributes, CustomerInfoAttributes, AsyncAttributes, BillingAddressAttributes, ShippingAddressAttributes {

    private static final String transactionType = TransactionTypes.MULTIBANCO;
    private String currency;
    private BigDecimal amount;

    // Required params
    private HashMap<String, String> requiredParams = new HashMap<String, String>();

    // GenesisValidator
    private GenesisValidator validator = new GenesisValidator();

    public MultibancoRequest() {
        super();
    }

    @Override
    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public MultibancoRequest setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    @Override
    public String getCurrency() {
        return currency;
    }

    @Override
    public MultibancoRequest setCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    @Override
    public String getTransactionType() {
        return transactionType;
    }

    @Override
    public String toXML() {
        return buildRequest("payment_transaction").toXML();
    }

    @Override
    public String toQueryString(String root) {
        return buildRequest(root).toQueryString();
    }

    private void checkRequiredCountry() {
        // Validate country Germany
        if (!Country.Portugal.getCode().equals(getBillingCountryCode())) {
            throw new InvalidParamException("Invalid country. Allowed country is: ["
                    + Country.Portugal.getCode() + "]");
        }
    }

    protected RequestBuilder buildRequest(String root) {

        // Set required params
        requiredParams.put(RequiredParameters.transactionId, getTransactionId());
        requiredParams.put(RequiredParameters.currency, getCurrency());
        requiredParams.put(RequiredParameters.amount, (getAmount() != null ? getAmount().toString() : null));
        requiredParams.put(RequiredParameters.returnSuccessUrl, getReturnSuccessUrl());
        requiredParams.put(RequiredParameters.returnFailureUrl, getReturnFailureUrl());
        requiredParams.put(RequiredParameters.billingAddress, getBillingPrimaryAddress());
        requiredParams.put(RequiredParameters.country, getBillingCountryCode());

        // Validate request
        validator.isValidRequest(requiredParams);

        checkRequiredCountry();

        return new RequestBuilder(root).addElement("transaction_type", transactionType)
                .addElement(buildBaseParams().toXML())
                .addElement(buildPaymentParams().toXML())
                .addElement(buildAsyncParams().toXML())
                .addElement(buildCustomerInfoParams().toXML())
                .addElement("billing_address", buildBillingAddress().toXML())
                .addElement("shipping_address", buildShippingAddress().toXML());
    }

    public List<Map.Entry<String, Object>> getElements() {
        return buildRequest("payment_transaction").getElements();
    }

}