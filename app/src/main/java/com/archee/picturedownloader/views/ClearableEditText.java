package com.archee.picturedownloader.views;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.widget.EditText;


public class ClearableEditText extends EditText {

    public ClearableEditText(Context context, AttributeSet attr) {
        super(context, attr);
        setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }

    public void clearText() {
        setText("");
    }
}
