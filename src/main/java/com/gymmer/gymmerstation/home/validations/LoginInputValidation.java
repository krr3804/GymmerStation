package com.gymmer.gymmerstation.home.validations;

public class LoginInputValidation {
    private static final String IS_BLANK = "User Id or Password Is Empty!";

    public static void checkLoginInput(String userId, String password) {
        if(userId.isBlank() || password.isBlank()) {
            throw new IllegalArgumentException(IS_BLANK);
        }
    }
}
