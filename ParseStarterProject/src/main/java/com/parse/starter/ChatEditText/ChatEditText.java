package com.parse.starter.ChatEditText;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

public class ChatEditText extends EditText{
    public ChatEditText(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    private KeyImeChange keyImeChangeListener;

    public void setKeyImeChangeListener(KeyImeChange listener)
    {
        keyImeChangeListener = listener;
    }

    public interface KeyImeChange
    {
        public void onKeyIme(int keyCode, KeyEvent event);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyImeChangeListener != null)
        {
            keyImeChangeListener.onKeyIme(keyCode, event);
        }
        return false;
    }
}
