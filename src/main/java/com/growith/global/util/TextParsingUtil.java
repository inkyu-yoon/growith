package com.growith.global.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class TextParsingUtil {

    public static Map<String, String> parsingFormData(String formData) {
        Map<String, String> map = new HashMap<>();
        String[] splited = formData.split("&");
        for (String s : splited) {
            String[] data = s.split("=");
            if (data.length >= 2) {
                map.put(data[0], data[1]);
            }
        }
        return map;
    }

    public static HashSet<String> parsingViewHistory(String viewHistory) {
        return new HashSet(List.of(viewHistory.split("_")));
    }
}
