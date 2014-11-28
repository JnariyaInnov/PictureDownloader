package com.archee.picturedownloader.views;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.EditText;


public class ClearableEditText extends EditText {

    public ClearableEditText(Context context, AttributeSet attr) {
        super(context, attr);
        setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        //setFilters(new InputFilter[]{new InputFilter.LengthFilter(100), new WackyCaps(this)});
    }

    public void clearText() {
        setText("");
    }

    /**
     * Input filter that transforms text into a variation of lower case and upper case letters
     */
    private static class WackyCaps implements InputFilter {

        EditText editText;

        public WackyCaps(EditText editText) {
            this.editText = editText;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            CharSequence text = editText.getText();
            String lastChar = "";

            if (text.length() != 0) {
                lastChar = String.valueOf(text.charAt(text.toString().length() - 1));
            }

            boolean lastCharUpperCase = lastChar.contentEquals(lastChar.toUpperCase());

            char[] v = new char[end - start];
            TextUtils.getChars(source, start, end, v, 0);

            for (int j = 0; j < v.length; j++) {
                if (j%2 == (lastCharUpperCase ? 1 : 0)) {
                    v[j] = String.valueOf(v[j]).toUpperCase().toCharArray()[0];
                }

            }

            return new String(v);
        }
    }
}
