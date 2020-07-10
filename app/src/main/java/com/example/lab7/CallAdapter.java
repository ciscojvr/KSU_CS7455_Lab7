package com.example.lab7;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

public class CallAdapter extends ArrayAdapter<Call> {

    private Context mContext;
    private List<Call> callList = new ArrayList<>();


    public CallAdapter(@NonNull Context context, ArrayList<Call> list) {
        super(context,  0, list);
        mContext = context;
        callList = list;
    }

    @Override
    public int getCount() {
        return callList.size();
    }

    @Override
    public Call getItem(int position) {
        return callList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.call_item_layout, parent, false);
        }

        Call currentCall = callList.get(position);

        TextView callPhoneNumber = (TextView)listItem.findViewById(R.id.textView_phoneNumber);
        callPhoneNumber.setText(currentCall.getCallPhoneNumber());

        TextView callDate = (TextView) listItem.findViewById(R.id.textView_callDate);
        callDate.setText(currentCall.getCallDate());

        Button deleteBtn = (Button) listItem.findViewById(R.id.button_deleteCall);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = getItem(position).getCallPhoneNumber();
                String callDate = getItem(position).getCallDate();

                callList.remove(position);

                String queryString = "NUMBER=" + phoneNumber;

                Uri callUri = Uri.parse("content://call_log/calls");
                ContentResolver resolver = getContext().getContentResolver();
                resolver.delete(callUri, queryString, null);

                Toast.makeText(getContext(), "Removed the number: " + phoneNumber, Toast.LENGTH_LONG).show();

                notifyDataSetChanged();
            }
        });

        return listItem;
    }
}
