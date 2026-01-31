package com.fivault.fivault.util;

import java.util.UUID;

public class StringUtil {
    public static boolean isValidUUID(String slug) {
        try {
            UUID.fromString(slug);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
