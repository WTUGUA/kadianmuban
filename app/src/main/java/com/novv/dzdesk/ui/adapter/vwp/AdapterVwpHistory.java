package com.novv.dzdesk.ui.adapter.vwp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.novv.dzdesk.R;

import java.util.ArrayList;

/**
 * Created by lingyfh on 2017/7/3.
 */
public class AdapterVwpHistory extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> mItems;

    public AdapterVwpHistory(Context context, ArrayList<String> items) {
        this.mContext = context;
        this.mItems = items;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.history_item, null);

        }

        TextView titleTv = (TextView) convertView.findViewById(R.id.lish_tv);
        String keyword = mItems.get(position);
        titleTv.setText(keyword + "");
        return convertView;
    }
}
