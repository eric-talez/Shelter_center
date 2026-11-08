package org.royshelter;

import java.util.Locale;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class ExerciseHelpersTest {
    @Test void trimsAndLowercasesSearchText() {
        assertThat(ExerciseHelpers.normalizeText("  MIX  ")).isEqualTo("mix");
    }
    @Test void whitespaceNormalizesToEmptyText() {
        assertThat(ExerciseHelpers.normalizeText(" \t ")).isEmpty();
    }
    @Test void normalizationIgnoresTheDefaultLocale() {
        var original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            assertThat(ExerciseHelpers.normalizeText("I")).isEqualTo("i");
        } finally {
            Locale.setDefault(original);
        }
    }
    @Test void speciesArrayCanBeChangedWithoutAffectingLaterCalls() {
        var species = ExerciseHelpers.supportedSpecies();
        assertThat(species).containsExactly("DOG", "CAT");
        species[0] = "BIRD";
        assertThat(ExerciseHelpers.supportedSpecies()).containsExactly("DOG", "CAT");
    }
}
