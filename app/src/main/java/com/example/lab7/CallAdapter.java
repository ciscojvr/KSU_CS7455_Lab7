/*

Name: Francisco Ozuna Diaz
Assignment: CS 7455 Lab 7
Lab Date: July 12, 2020 at 11:59 PM
 */

package com.example.lab7;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.provider.CallLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CallAdapter extends ArrayAdapter<Call> {

    private Context mContext;
    private List<Call> callList;


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
        Long callSeconds = currentCall.getCallDate();

        Date callDayTime = new Date(callSeconds);
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy @ hh:mm aa");
        String dateString = formatter.format(callDayTime);

        callDate.setText(dateString);

        Button deleteBtn = (Button) listItem.findViewById(R.id.button_deleteCall);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = getItem(position).getCallPhoneNumber();
                Long callSeconds = getItem(position).getCallDate();
                String callType = getItem(position).getCallType();

                callList.remove(position);

                String queryString = CallLog.Calls.NUMBER + "=? AND " + CallLog.Calls.DATE + "=? AND " + CallLog.Calls.TYPE + "=?";

                Uri callUri = Uri.parse("content://call_log/calls");
                ContentResolver resolver = getContext().getContentResolver();
                resolver.delete(callUri, queryString, new String[]{phoneNumber, String.valueOf(callSeconds), callType});

                switch (Integer.parseInt(callType)) {
                    case 1:
                        Toast.makeText(getContext(), "Removed the number: " + phoneNumber + " from incoming call list.", Toast.LENGTH_LONG).show();
                        break;
                    case 2:
                        Toast.makeText(getContext(), "Removed the number: " + phoneNumber + " from outgoing call list.", Toast.LENGTH_LONG).show();
                        break;
                    case 3:
                        Toast.makeText(getContext(), "Removed the number: " + phoneNumber + " from missed call list.", Toast.LENGTH_LONG).show();
                        break;
                }

                notifyDataSetChanged();
            }
        });

        return listItem;
    }
}
