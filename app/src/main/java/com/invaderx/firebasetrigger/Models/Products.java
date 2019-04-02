package com.invaderx.firebasetrigger.Models;

import java.util.HashMap;

public class Products {
    private String pId;
    private String pName;
    private String pCategory;
    private HashMap<String, Integer> pBid;
    private String bidderUID;
    private String productListImgURL;
    private String sellerName;
    private String basePrice;
    private String sellerUID;
    private String catId;
    private int noOfBids;
    private String searchStr;
    private int expTime;
    private String pDescription;
    private String pCondition;
    private String pStatus;
    private String expDate;


    public Products(String pId, String pName, String pCategory, HashMap<String, Integer> pBid, String bidderUID, String productListImgURL,
                    String sellerName, String basePrice, String sellerUID,
                    String catId, int noOfBids, String searchStr, int expTime, String pDescription,
                    String pCondition, String pStatus, String expDate) {
        this.pId = pId;
        this.pName = pName;
        this.pCategory = pCategory;
        this.pBid = pBid;
        this.bidderUID = bidderUID;
        this.productListImgURL = productListImgURL;
        this.sellerName = sellerName;
        this.basePrice = basePrice;
        this.sellerUID = sellerUID;
        this.catId = catId;
        this.noOfBids = noOfBids;
        this.searchStr = searchStr;
        this.expTime = expTime;
        this.pDescription = pDescription;
        this.pCondition = pCondition;
        this.pStatus = pStatus;
        this.expDate = expDate;
    }

    public Products() {

    }

    public String getpId() {
        return pId;
    }

    public String getpName() {
        return pName;
    }

    public String getpCategory() {
        return pCategory;
    }

    public HashMap<String, Integer> getpBid() {
        return pBid;
    }

    public String getBidderUID() {
        return bidderUID;
    }

    public String getProductListImgURL() {
        return productListImgURL;
    }

    public String getSellerName() {
        return sellerName;
    }

    public String getBasePrice() {
        return basePrice;
    }

    public String getSellerUID() {
        return sellerUID;
    }

    public String getCatId() {
        return catId;
    }

    public int getNoOfBids() {
        return noOfBids;
    }

    public String getSearchStr() {
        return searchStr;
    }

    public int getExpTime() {
        return expTime;
    }

    public String getpDescription() {
        return pDescription;
    }

    public String getExpDate() {
        return expDate;
    }

    public String getpCondition() {
        return pCondition;
    }

    public String getpStatus() {
        return pStatus;
    }
}
