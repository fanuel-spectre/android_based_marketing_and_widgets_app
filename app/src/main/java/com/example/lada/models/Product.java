package com.example.lada.models;

public class Product {
    private String productId, productTitle, productImage, productCategory, productBrand, productColor,productCondition, productType, productDescription,
            productGender, productDiscountPrice, originalPrice, productDiscountRate, productAdderName, discountAvailable, negotiationAvailable, uid;

    public Product() {


    }

    public Product(String productId, String productTitle, String productImage, String productCategory, String productBrand, String productColor, String productCondition, String productType, String productDescription, String productGender, String productDiscountPrice, String originalPrice, String productDiscountRate, String productAdderName, String discountAvailable, String negotiationAvailable, String uid) {
        this.productId = productId;
        this.productTitle = productTitle;
        this.productImage = productImage;
        this.productCategory = productCategory;
        this.productBrand = productBrand;
        this.productColor = productColor;
        this.productCondition = productCondition;
        this.productType = productType;
        this.productDescription = productDescription;
        this.productGender = productGender;
        this.productDiscountPrice = productDiscountPrice;
        this.originalPrice = originalPrice;
        this.productDiscountRate = productDiscountRate;
        this.productAdderName = productAdderName;
        this.discountAvailable = discountAvailable;
        this.negotiationAvailable = negotiationAvailable;
        this.uid = uid;
    }


    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductBrand() {
        return productBrand;
    }

    public void setProductBrand(String productBrand) {
        this.productBrand = productBrand;
    }

    public String getProductColor() {
        return productColor;
    }

    public void setProductColor(String productColor) {
        this.productColor = productColor;
    }

    public String getProductCondition() {
        return productCondition;
    }

    public void setProductCondition(String productCondition) {
        this.productCondition = productCondition;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductGender() {
        return productGender;
    }

    public void setProductGender(String productGender) {
        this.productGender = productGender;
    }

    public String getProductDiscountPrice() {
        return productDiscountPrice;
    }

    public void setProductDiscountPrice(String productDiscountPrice) {
        this.productDiscountPrice = productDiscountPrice;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getProductDiscountRate() {
        return productDiscountRate;
    }

    public void setProductDiscountRate(String productDiscountRate) {
        this.productDiscountRate = productDiscountRate;
    }

    public String getProductAdderName() {
        return productAdderName;
    }

    public void setProductAdderName(String productAdderName) {
        this.productAdderName = productAdderName;
    }

    public String getDiscountAvailable() {
        return discountAvailable;
    }

    public void setDiscountAvailable(String discountAvailable) {
        this.discountAvailable = discountAvailable;
    }

    public String getNegotiationAvailable() {
        return negotiationAvailable;
    }

    public void setNegotiationAvailable(String negotiationAvailable) {
        this.negotiationAvailable = negotiationAvailable;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}

