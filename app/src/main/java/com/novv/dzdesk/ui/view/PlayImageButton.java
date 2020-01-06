package com.novv.dzdesk.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import com.novv.dzdesk.R;

public class PlayImageButton extends android.support.v7.widget.AppCompatImageButton implements
        OnClickListener {

    private int drawableID = -1;
    private SwitchListner listner;

    public void setSwitchListner(SwitchListner listner) {
        this.listner = listner;
    }

    public interface SwitchListner {

        public void play();

        public void pause();

        public void unPause();
    }

    @Override
    public void setImageResource(int resId) {
        drawableID = resId;
        super.setImageResource(resId);
    }

    public PlayImageButton(Context context) {
        this(context, null, 0);
    }

    public PlayImageButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayImageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        drawableID = R.drawable.play_button;
        setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (drawableID) {
            case R.drawable.play_button:
                setImageResource(R.drawable.pause_button);
                if (listner != null) {
                    listner.play();
                }
                break;
            case R.drawable.pause_button:
                if (listner != null) {
                    listner.pause();
                }
                break;
            case R.drawable.play_button_white:
                setImageResource(R.drawable.pause_button);

                if (listner != null) {
                    listner.unPause();
                }
                break;
            default:
                break;
        }
    }

}

