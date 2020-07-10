package com.example.lab7;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    ListView incomingCallsListView;
    ListView outgoingCallsListView;
    ListView missedCallsListView;

    ArrayAdapter<Call> incomingCallsArrayAdapter;
    ArrayAdapter<Call> outgoingCallsArrayAdapter;
    ArrayAdapter<Call> missedCallsArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        incomingCallsListView = (ListView) findViewById(R.id.listView_incomingCalls);

        outgoingCallsListView = (ListView) findViewById(R.id.listView_outgoingCalls);

        missedCallsListView = (ListView) findViewById(R.id.listView_missedCalls);

        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_CALL_LOG }, 1);
        } else {
            getCallDetails();
        }

        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG)) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.WRITE_CALL_LOG }, 1);
        } else {
            getCallDetails();

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
                        getCallDetails();
                    }
                } else {
                    Toast.makeText(this, "No permission granted!", Toast.LENGTH_SHORT).show();
                }

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
                        getCallDetails();
                    }
                } else {
                    Toast.makeText(this, "No permission granted!", Toast.LENGTH_SHORT).show();
                }

                return;
        }
    }

    private void getCallDetails() {
        final ArrayList<Call> incomingCallsList = new ArrayList<>();
        final ArrayList<Call> outgoingCallsList = new ArrayList<>();
        final ArrayList<Call> missedCallsList = new ArrayList<>();

        Uri callUri = Uri.parse("content://call_log/calls");
        ContentResolver resolver = getContentResolver();
        Cursor managedCursor = resolver.query(callUri, null, null, null, CallLog.Calls.DATE + " DESC");

        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

        while (managedCursor.moveToNext()) {
            String phoneNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);
            Date callDayTime = new Date(Long.valueOf(callDate));
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy @ hh:mm aa");
            String dateString = formatter.format(callDayTime);
//            String callDuration = managedCursor.getString(duration);
//            String dir;
            int dirCode = Integer.parseInt(callType);
            switch (dirCode) {
                case CallLog.Calls.INCOMING_TYPE:
//                    dir = "Incoming";
                    Call currentIncomingCall = new Call(phoneNumber, dateString);
                    incomingCallsList.add(currentIncomingCall);
                    break;
                case CallLog.Calls.OUTGOING_TYPE:
//                    dir = "Outgoing";
                    Call currentOutgoingCall = new Call(phoneNumber, dateString);
                    outgoingCallsList.add(currentOutgoingCall);
                    break;
                case CallLog.Calls.MISSED_TYPE:
//                    dir = "Missed";
                    Call currentMissedCall = new Call(phoneNumber, dateString);
                    missedCallsList.add(currentMissedCall);
                    break;
                default:
                    continue;
            }
        }

        if (incomingCallsArrayAdapter == null) {
            incomingCallsArrayAdapter = new CallAdapter(this, incomingCallsList);
            incomingCallsListView.setAdapter(incomingCallsArrayAdapter);
        } else {
            incomingCallsArrayAdapter.clear();
            incomingCallsArrayAdapter.addAll(incomingCallsList);
            incomingCallsArrayAdapter.notifyDataSetChanged();
        }

        if (outgoingCallsArrayAdapter == null) {
            outgoingCallsArrayAdapter = new CallAdapter(this, outgoingCallsList);
            outgoingCallsListView.setAdapter(outgoingCallsArrayAdapter);
        } else {
            outgoingCallsArrayAdapter.clear();
            outgoingCallsArrayAdapter.addAll(outgoingCallsList);
            outgoingCallsArrayAdapter.notifyDataSetChanged();
        }

        if (missedCallsArrayAdapter == null) {
            missedCallsArrayAdapter = new CallAdapter(this, missedCallsList);
            missedCallsListView.setAdapter(missedCallsArrayAdapter);
        } else {
            missedCallsArrayAdapter.clear();
            missedCallsArrayAdapter.addAll(missedCallsList);
            missedCallsArrayAdapter.notifyDataSetChanged();
        }

        managedCursor.close();

        SearchView search = findViewById(R.id.searchView);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                for(Call item: incomingCallsList) {
                    if (item.getCallPhoneNumber().equals(query)) {
                        Toast.makeText(getApplicationContext(), "Found the number: " + query + " in incoming call list.", Toast.LENGTH_LONG).show();
                        return true;
                    }
                }

                for(Call item: outgoingCallsList) {
                    if (item.getCallPhoneNumber().equals(query)) {
                        Toast.makeText(getApplicationContext(), "Found the number: " + query + " in outgoing call list.", Toast.LENGTH_LONG).show();
                        return true;
                    }
                }

                for(Call item: missedCallsList) {
                    if (item.getCallPhoneNumber().equals(query)) {
                        Toast.makeText(getApplicationContext(), "Found the number: " + query + " in missed call list.", Toast.LENGTH_LONG).show();
                        return true;
                    }
                }

                Toast.makeText(getApplicationContext(), "Number not found.", Toast.LENGTH_LONG).show();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    public void getNewData(View view) {
        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_CALL_LOG }, 1);
        } else {
            getCallDetails();
            Toast.makeText(this, "Call log has been updated.", Toast.LENGTH_SHORT).show();
        }

        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG)) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.WRITE_CALL_LOG }, 1);
        } else {
            getCallDetails();
            Toast.makeText(this, "Call log has been updated.", Toast.LENGTH_SHORT).show();
        }
    }
}

