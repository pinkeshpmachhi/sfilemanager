package com.p2m.sfilemanager.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.p2m.sfilemanager.R;

public class CustomAdapterForOptionDialogListView extends BaseAdapter {
    String[] itemOk;
    Context context;

    public CustomAdapterForOptionDialogListView(String[] itemOk, Context context) {
        this.itemOk = itemOk;
        this.context= context;
    }

    @Override
    public int getCount() {
        return itemOk.length;
    }

    @Override
    public Object getItem(int position) {
        return itemOk[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view= View.inflate(context, R.layout.sample_list_in_dialog,null);
        TextView optionsName= view.findViewById(R.id.optionTV);
        optionsName.setText(itemOk[position]);
        return view;
    }
}
