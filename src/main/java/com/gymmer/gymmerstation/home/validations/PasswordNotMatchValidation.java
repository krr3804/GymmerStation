package com.gymmer.gymmerstation.home.validations;

public class PasswordNotMatchValidation {
    private static final String NOT_MATCH = "Password Does Not Match!";

    public static void checkPasswordMismatch(String password, String confirmPassword) {
        if(!password.equals(confirmPassword)) {
            throw new IllegalArgumentException(NOT_MATCH);
        }
    }
}
