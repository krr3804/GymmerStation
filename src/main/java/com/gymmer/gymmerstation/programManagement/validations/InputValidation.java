package com.gymmer.gymmerstation.programManagement.validations;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class InputValidation {
    private static final String INPUT_BLANK = " Is Blank!";
    private static final String REST_TIME = "Rest Time Is 00:00!";

    public static void inputBlankValidation(LinkedHashMap<String, String> map) {
        StringBuilder sb = new StringBuilder();
        for(Map.Entry entry : map.entrySet()) {
            if(entry.getValue().toString().isBlank()) {
                sb.append(entry.getKey()).append(", ");
            }
        }
        if(!sb.toString().isBlank()) {
            sb.delete(sb.length()-2,sb.length());
            sb.append(INPUT_BLANK);
            throw new IllegalArgumentException(sb.toString());
        }
    }

    public static void inputMismatchValidationRestTime(String input) {
        if(input.equals("00:00")) {
            throw new IllegalArgumentException(REST_TIME);
        }
    }
}
