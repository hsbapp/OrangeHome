package com.gmoonxs.www.orangehome;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by 威 on 2016/8/1.
 */
public class LogsAdapter extends BaseAdapter {
    private Context mContext;
    private Cursor mLogCursor;
    private LayoutInflater inflater;
    int num=0;

    public LogsAdapter(Context context,Cursor cursor) {

        mContext = context;
        mLogCursor = cursor;
        inflater=LayoutInflater.from(context);
        num=mLogCursor.getCount();
    }
    @Override
    public int getCount() {
        return mLogCursor.getCount();
    }
    @Override
    public Object getItem(int position) {
        return null;
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final LogListViewHolder logListViewHolder;
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.log_item_layout, parent, false);
            logListViewHolder=new LogListViewHolder();
            assert view!=null;
            logListViewHolder.log_item_log_time=(TextView)view.findViewById(R.id.log_item_log_time);
            logListViewHolder.log_item_log_content=(TextView)view.findViewById(R.id.log_item_log_content);
            view.setTag(logListViewHolder);
        } else {
            logListViewHolder = (LogListViewHolder) view.getTag();
        }

        mLogCursor.moveToPosition(num-position-1);
        logListViewHolder.log_item_log_time.setText(mLogCursor.getString(1));
        logListViewHolder.log_item_log_content.setText(mLogCursor.getString(2));
        return view;
    }
}
class LogListViewHolder {
    TextView log_item_log_time;
    TextView log_item_log_content;
}
