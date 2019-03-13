package com.invaderx.firebasetrigger.Models;


public class UserProfile {
    private String uid;
    private String phone;
    private int wallet;
    private String uToken;

    public UserProfile(String uid, String phone, int wallet, String uToken) {
        this.uid = uid;
        this.phone = phone;
        this.wallet = wallet;
        this.uToken = uToken;
    }

    public UserProfile() {

    }

    public String getUid() {
        return uid;
    }

    public String getPhone() {
        return phone;
    }

    public int getWallet() {
        return wallet;
    }

    public String getuToken() {
        return uToken;
    }
}
