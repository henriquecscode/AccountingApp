package com.fivault.fivault.util;

import com.fivault.fivault.database.model.Platform;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SlugUtil {
    public static String generateSlug(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "";
        }

        String normalized = java.text.Normalizer.normalize(name, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", ""); // remove accents
        String slug = normalized.toLowerCase()
                .replaceAll("[^a-z0-9\\-\\s]", "") // keep only letters, numbers, hyphens, spaces
                .replaceAll("[\\s]+", "-")        // spaces → hyphens
                .replaceAll("-{2,}", "-")         // collapse multiple hyphens
                .replaceAll("^-|-$", "");         // remove leading/trailing hyphens

        return slug;
    }

    public static String nextAvailableSlug(String baseSlug, List<String> existingSlugs) {
        int max = 0;
        Pattern pattern = Pattern.compile(Pattern.quote(baseSlug) + "-(\\d+)$");

        for (String s : existingSlugs) {
            if (s.equals(baseSlug)) {
                max = Math.max(max, 1);
            } else {
                Matcher m = pattern.matcher(s);
                if (m.find()) {
                    int n = Integer.parseInt(m.group(1));
                    max = Math.max(max, n);
                }
            }
        }

        return (max == 0) ? baseSlug : baseSlug + "-" + (max + 1);
    }
}
