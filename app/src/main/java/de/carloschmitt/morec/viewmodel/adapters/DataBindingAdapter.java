package de.carloschmitt.morec.adapters;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

public class DataBindingAdapter {

    @SuppressLint("ClickableViewAccessibility")
    @BindingAdapter("OnTouchListener")
    public static void setViewOnTouch(View view, View.OnTouchListener listener) {
        view.setOnTouchListener(listener);
    }
}
