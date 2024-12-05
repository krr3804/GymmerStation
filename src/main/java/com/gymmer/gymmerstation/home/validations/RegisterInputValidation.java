package com.gymmer.gymmerstation.home.validations;

import java.util.regex.Pattern;

public class RegisterInputValidation {
    private static final String IS_BLANK = " Is Blank!";
    private static final String TYPE_MISMATCH = " Must Consist Of Alphabets And Digits Only!";
    private static final String ID_LENGTH = "User ID Must Be Between 5 - 15 In Length";
    private static final String PW_LENGTH = "Password Must Be Between 8 - 20 In Length";
    private static final String pattern = "^[0-9a-zA-Z]*$";

    public static void checkUserId(String userId) {
        if(userId.isBlank()) {
            throw new IllegalArgumentException("User ID" + IS_BLANK);
        }
        if(!Pattern.matches(pattern,userId)) {
            throw new IllegalArgumentException("User ID" + TYPE_MISMATCH);
        }
        if(userId.length() < 5 || userId.length() > 15) {
            throw new IllegalArgumentException(ID_LENGTH);
        }
    }

    public static void checkPassword(String password) {
        if(password.isBlank()) {
            throw new IllegalArgumentException("Password" + IS_BLANK);
        }
        if(!Pattern.matches(pattern,password)) {
            throw new IllegalArgumentException("Password" + TYPE_MISMATCH);
        }
        if(password.length() < 8 || password.length() > 20) {
            throw new IllegalArgumentException(PW_LENGTH);
        }
    }
}
