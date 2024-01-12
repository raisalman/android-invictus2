package net.invictusmanagement.invictuslifestyle.customviews;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DecimalDigitsInputFilter implements InputFilter {

    Pattern mPattern;

    public DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
        mPattern = Pattern.compile("[0-9]{0,9}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?");
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

        Matcher matcher = mPattern.matcher(removeCommas(dest.toString()));
        if (!matcher.matches())
            return "";
        return null;
    }

    private String removeCommas(String amount) {
        String amountReturn = amount;
        if ((amountReturn.contains(","))) {
            amountReturn = amountReturn.replace(",", "");
        }
        return amountReturn;
    }
}