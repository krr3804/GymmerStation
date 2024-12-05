package com.gymmer.gymmerstation.util;

public class CommonValidation {
    private static final String NO_ITEM_SELECTED = "No Item Selected!";
    private static final String INPUT_MISMATCH = " Must Be Numeric!";
    private static final String NEGATIVE_DIGIT = " Must Be No Less Than 1!";

    public static void noItemSelectedValidation(Object selectedItem) {
        if (selectedItem == null) {
            throw new IllegalArgumentException(NO_ITEM_SELECTED);
        }
    }

    public static void noIndexSelectedValidation(int index) {
        if(index < 0) {
            throw new IllegalArgumentException(NO_ITEM_SELECTED);
        }
    }

    public static void inputMismatchValidationNumber(String input, String field) {
        Long num;
        if(input.isBlank()) {
            input = "1";
        }
        try {
            num = Long.parseLong(input);
        } catch (Exception e) {
            throw new IllegalArgumentException(field + INPUT_MISMATCH);
        }

        if(num < 1) {
            throw new IllegalArgumentException(field + NEGATIVE_DIGIT);
        }
    }
}
