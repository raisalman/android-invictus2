package net.invictusmanagement.invictuslifestyle.models;

import java.util.Date;

public class PaymentTransactionResponse {
    private String paymentType;
    private String unitNbr;
    private int transactionId;
    private float transactionAmount;
    private float transactionFee;
    private float transactionTotal;
    private String note;
    private boolean isSuccess;
    private Date transactionDate;

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getUnitNbr() {
        return unitNbr;
    }

    public void setUnitNbr(String unitNbr) {
        this.unitNbr = unitNbr;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public float getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(float transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public float getTransactionFee() {
        return transactionFee;
    }

    public void setTransactionFee(float transactionFee) {
        this.transactionFee = transactionFee;
    }

    public float getTransactionTotal() {
        return transactionTotal;
    }

    public void setTransactionTotal(float transactionTotal) {
        this.transactionTotal = transactionTotal;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }
}