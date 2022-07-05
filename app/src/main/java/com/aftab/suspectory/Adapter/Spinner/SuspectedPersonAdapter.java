package com.aftab.suspectory.Adapter.Spinner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aftab.suspectory.Model.DeviceInfo;
import com.aftab.suspectory.R;

import java.util.ArrayList;


public class SuspectedPersonAdapter extends BaseAdapter {
    Context context;
    ArrayList<DeviceInfo> deviceInfoArrayList;
    LayoutInflater inflter;

    public SuspectedPersonAdapter(Context applicationContext, ArrayList<DeviceInfo> deviceInfos) {
        this.context = applicationContext;
        inflter = (LayoutInflater.from(applicationContext));
        deviceInfoArrayList = deviceInfos;
    }

    @Override
    public int getCount() {
        return deviceInfoArrayList.size() ;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint({"InflateParams", "ViewHolder"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.suspected_person_spinner, null);
        TextView names = view.findViewById(R.id.textView);

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) names.getLayoutParams();
        params.setMarginStart(10);

        /*String name=deviceInfoArrayList.get(i).getDeviceName();

        if (name!=null){


        }*/

        names.setText(deviceInfoArrayList.get(i).getDeviceName());
        return view;
    }


}