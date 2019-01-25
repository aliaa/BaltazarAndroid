package com.mybaltazar.baltazar2.models;

public class ShopOrder
{
    public enum OrderStatus
    {
        WaitForApprove,
        Rejected,
        Approved,
        Delivered
    }

    public String shopItemId;
    public String shopItemName;
    public String userId;
    public int coinCost;
    public String orderDate;
    public OrderStatus status;
}
