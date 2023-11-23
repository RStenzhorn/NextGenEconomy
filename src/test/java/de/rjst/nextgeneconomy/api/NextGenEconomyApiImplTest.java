package de.rjst.nextgeneconomy.api;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class NextGenEconomyApiImplTest {

    private static final BigDecimal ANY_DECIMAL = new BigDecimal("100012300.203");
    private static final String GER_RESULT = "100.012.300,20";
    private static final String US_RESULT = "100,012,300.203";

    @Test
    void getFormattedDecimalGER() {
        final NumberFormat numberFormat =  NumberFormat.getNumberInstance(Locale.GERMAN);
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);
        final String result = numberFormat.format(ANY_DECIMAL);
        assertThat(result).isEqualTo(GER_RESULT);
    }

    @Test
    void getFormattedDecimalUS() {
        final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(3);
        final String result = numberFormat.format(ANY_DECIMAL);
        assertThat(result).isEqualTo(US_RESULT);
    }
}