package com.invaderx.firebasetrigger.Models;

public class Products {
    private String pId;
    private String pName;
    private String pCost;
    private int pBid;

    public Products(String pId, String pName, String pCost, int pBid) {
        this.pId = pId;
        this.pName = pName;
        this.pCost = pCost;
        this.pBid = pBid;
    }

    public Products() {

    }

    public String getpId() {
        return pId;
    }

    public String getpName() {
        return pName;
    }

    public String getpCost() {
        return pCost;
    }

    public int getpBid() {
        return pBid;
    }
}
