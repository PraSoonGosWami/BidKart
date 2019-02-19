package com.invaderx.firebasetrigger.Models;

public class Category {
    private String imgURL;
    private String categoryId;
    private String categoryName;

    public Category(String imgURL, String categoryId, String categoryName) {
        this.imgURL = imgURL;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public Category(){
    }

    public String getImgURL() {
        return imgURL;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }
}
