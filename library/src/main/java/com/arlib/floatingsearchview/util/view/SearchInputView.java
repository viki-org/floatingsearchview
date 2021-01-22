package com.arlib.floatingsearchview.util.view;

import android.content.Context;
import androidx.appcompat.widget.AppCompatEditText;

import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

public class SearchInputView extends AppCompatEditText {

    private OnKeyboardSearchKeyClickListener mSearchKeyListener;

    private OnKeyboardDismissedListener mOnKeyboardDismissedListener;

    private OnKeyListener mOnKeyListener = new OnKeyListener() {
        public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {

            if (keyCode == KeyEvent.KEYCODE_ENTER && mSearchKeyListener != null) {
                mSearchKeyListener.onSearchKeyClicked();
                return true;
            }
            return false;
        }
    };

    public SearchInputView(Context context) {
        super(context);
        init();
    }

    public SearchInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SearchInputView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setOnKeyListener(mOnKeyListener);

        //disable long press and text selection because of a bunch of crashes: https://console.firebase.google.com/u/1/project/api-project-1068643720791/crashlytics/app/android:com.viki.android/issues?state=open&time=last-seven-days&type=crash&issuesQuery=editor.java
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            setTextIsSelectable(false);
            setLongClickable(false);
        }
        else {
            setTextIsSelectable(true);
        }
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent ev) {
        if (ev.getKeyCode() == KeyEvent.KEYCODE_BACK && mOnKeyboardDismissedListener != null) {
            mOnKeyboardDismissedListener.onKeyboardDismissed();
        }
        return super.onKeyPreIme(keyCode, ev);
    }

    public void setOnKeyboardDismissedListener(OnKeyboardDismissedListener onKeyboardDismissedListener) {
        mOnKeyboardDismissedListener = onKeyboardDismissedListener;
    }

    public void setOnSearchKeyListener(OnKeyboardSearchKeyClickListener searchKeyListener) {
        mSearchKeyListener = searchKeyListener;
    }

    public interface OnKeyboardDismissedListener {
        void onKeyboardDismissed();
    }

    public interface OnKeyboardSearchKeyClickListener {
        void onSearchKeyClicked();
    }
}
