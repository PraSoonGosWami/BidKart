package com.invaderx.firebasetrigger.Models;

public class Transactions {
    private String selleruid;
    private String bidderuid;
    private String sellername;
    private String biddername;
    private String amount;
    private String proName;
    private String tID;

    public Transactions() {

    }

    public Transactions(String selleruid, String bidderuid, String sellername, String biddername, String amount, String proName, String tID) {
        this.selleruid = selleruid;
        this.bidderuid = bidderuid;
        this.sellername = sellername;
        this.biddername = biddername;
        this.amount = amount;
        this.proName = proName;
        this.tID = tID;
    }

    public String getSelleruid() {
        return selleruid;
    }

    public String getBidderuid() {
        return bidderuid;
    }

    public String getSellername() {
        return sellername;
    }

    public String getBiddername() {
        return biddername;
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
}
