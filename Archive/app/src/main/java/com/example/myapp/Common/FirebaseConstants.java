package com.example.myapp.Common;

import android.content.Context;

import com.example.myapp.R;

public class FirebaseConstants {

    public static String USER_TABLE_NAME = "User";
    public static String CATEGORY_TABLE_NAME = "Category";
    public static String FOOD_TABLE_NAME = "Foods";
    public static String ORDER_TABLE_NAME = "Placed_Orders";
    public static String FOOD_LIST_REF;
    public static String ORDER_LIST_REF;
    public static String STROAGE_IMAGE_REF;


    public static String foodListReference(String categoryId) {
        FOOD_LIST_REF = CATEGORY_TABLE_NAME + "/" + categoryId + "/foodList";
        return FOOD_LIST_REF;
    }

    public static String productListRef(String categoryId) {
        FOOD_LIST_REF = FOOD_TABLE_NAME + "/" + categoryId;
        return FOOD_LIST_REF;
    }

    public static String orderListRef(String ownerId) {
        ORDER_LIST_REF = ORDER_TABLE_NAME + "/" + ownerId;
        return ORDER_LIST_REF;
    }

    public static String updateOrderStatus(String owner, String orderId) {
        return ORDER_TABLE_NAME + "/" + owner + "/" + orderId;
    }

    public static String strogaeImageReferance(Context mContext, String typeName) {
        STROAGE_IMAGE_REF = mContext.getResources().getString(R.string.app_name)
                + "/images/" + typeName + "/";
        return STROAGE_IMAGE_REF;
    }

}
