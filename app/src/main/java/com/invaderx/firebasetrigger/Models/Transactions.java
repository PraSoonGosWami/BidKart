package com.invaderx.firebasetrigger.Models;

public class Transactions {
    private String selleruid;
    private String bidderuid;
    private String name;
    private String amount;
    private String proName;
    private String tID;
    private String date;

    public Transactions() {

    }

    public Transactions(String selleruid, String bidderuid, String name, String amount, String proName, String tID, String date) {
        this.selleruid = selleruid;
        this.bidderuid = bidderuid;
        this.name = name;
        this.amount = amount;
        this.proName = proName;
        this.tID = tID;
        this.date = date;
    }

    public String getSelleruid() {
        return selleruid;
    }

    public String getBidderuid() {
        return bidderuid;
    }

    public String getName() {
        return name;
    }


    public String getAmount() {
        return amount;
    }

    public String getProName() {
        return proName;
    }

    public String gettID() {
        return tID;
    }

    public String getDate() {
        return date;
    }
}
