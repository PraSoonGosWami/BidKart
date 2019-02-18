package com.invaderx.firebasetrigger.Models;


public class UserProfile {
    private String uid;
    private String phone;
    private int wallet;

    public UserProfile(String uid, String phone, int wallet) {
        this.uid = uid;
        this.phone = phone;
        this.wallet = wallet;
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
}
