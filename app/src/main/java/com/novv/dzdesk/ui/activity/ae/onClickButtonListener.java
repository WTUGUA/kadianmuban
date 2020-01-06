package com.novv.dzdesk.ui.activity.ae;


import android.view.View;

public class onClickButtonListener implements View.OnClickListener {

    public static final int BUTTON_TYPE_A = 5;


    private final int mType;

    public onClickButtonListener( int type ) {
        mType = type;
    }

    @Override
    public void onClick(View v) {
        switch(mType) {
            case BUTTON_TYPE_A:
                break;
        }
    }
}
