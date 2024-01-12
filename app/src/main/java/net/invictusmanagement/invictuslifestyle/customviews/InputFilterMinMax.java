package net.invictusmanagement.invictuslifestyle.customviews;

import android.text.InputFilter;
import android.text.Spanned;

public class InputFilterMinMax implements InputFilter {

    private final double min;
    private final double max;

    public InputFilterMinMax(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public InputFilterMinMax(String min, String max) {
        this.min = Integer.parseInt(min);
        this.max = Integer.parseInt(max);
    }

    public InputFilterMinMax(double min, double max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            /*int input = Integer.parseInt(removeCommas(dest.toString()) + removeCommas(source.toString()));*/
            double input = Double.parseDouble(removeCommas(dest.toString()) + removeCommas(source.toString()));
            if (isInRange(min, max, input))
                return null;
        } catch (NumberFormatException nfe) {
        }
        return "";
    }

    private boolean isInRange(double a, double b, double c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }

    private String removeCommas(String amount) {
        String amountReturn = amount;
        if ((amountReturn.contains(","))) {
            amountReturn = amountReturn.replace(",", "");
        }
        return amountReturn;
    }
}
