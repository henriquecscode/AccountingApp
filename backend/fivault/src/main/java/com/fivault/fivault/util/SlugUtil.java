package com.fivault.fivault.util;

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
}
