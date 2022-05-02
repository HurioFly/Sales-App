package com.example.salesapp.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Order {

    @SerializedName("orderID")
    @Expose
    private int orderID;
    @SerializedName("consigneeName")
    @Expose
    private String consigneeName;
    @SerializedName("consigneeAddress")
    @Expose
    private String consigneeAddress;
    @SerializedName("consigneePhoneNumber")
    @Expose
    private String consigneePhoneNumber;
    @SerializedName("dateCreated")
    @Expose
    private String dateCreated;
    @SerializedName("totalMoney")
    @Expose
    private int totalMoney;
    @SerializedName("paymentMethods")
    @Expose
    private String paymentMethods;
    @SerializedName("orderDetails")
    @Expose
    private List<OrderDetails> orderDetailsList;

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public String getConsigneeName() {
        return consigneeName;
    }

    public void setConsigneeName(String consigneeName) {
        this.consigneeName = consigneeName;
    }

    public String getConsigneeAddress() {
        return consigneeAddress;
    }

    public void setConsigneeAddress(String consigneeAddress) {
        this.consigneeAddress = consigneeAddress;
    }

    public String getConsigneePhoneNumber() {
        return consigneePhoneNumber;
    }

    public void setConsigneePhoneNumber(String consigneePhoneNumber) {
        this.consigneePhoneNumber = consigneePhoneNumber;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public int getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(int totalMoney) {
        this.totalMoney = totalMoney;
    }

    public String getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(String paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public List<OrderDetails> getOrderDetailsList() {
        return orderDetailsList;
    }

    public void setOrderDetailsList(List<OrderDetails> orderDetailsList) {
        this.orderDetailsList = orderDetailsList;
    }
}