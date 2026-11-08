package org.royshelter;

import java.util.Locale;

public final class ExerciseHelpers {
    private ExerciseHelpers() { }

    /**
     * Trim and lowercase non-null text using a locale-independent rule.
     * For example, "  MIX  " becomes "mix" and whitespace alone becomes "".
     * Returns normalized text without changing the original String.
     */
    public static String normalizeText(String text) {
        return text.trim().toLowerCase(Locale.ROOT);
    }

    /**
     * Return a fresh array of the supported species, in DOG, CAT order; no arguments are needed.
     * For example, a loop over the result visits "DOG" then "CAT".
     * Changing the returned array does not affect later calls.
     */
    public static String[] supportedSpecies() {
        return new String[] {"DOG", "CAT"};
    }
}
