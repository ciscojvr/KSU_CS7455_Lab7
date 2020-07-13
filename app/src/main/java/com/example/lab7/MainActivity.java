/*

Name: Francisco Ozuna Diaz
Assignment: CS 7455 Lab 7
Lab Date: July 12, 2020 at 11:59 PM
 */


package com.example.lab7;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;

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

//        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)) {
//            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_CALL_LOG }, 1);
//            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG)) {
//                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.WRITE_CALL_LOG }, 1);
//            }
//        } else {
//            getCallDetails();
//        }

        ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_CALL_LOG }, 1);
        ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.WRITE_CALL_LOG }, 1);
        getCallDetails();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "Permission granted!", Toast.LENGTH_LONG).show();
                            getCallDetails();
                        }
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

        while (managedCursor.moveToNext()) {
            String phoneNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            Long callSeconds = managedCursor.getLong(date);
            int dirCode = Integer.parseInt(callType);
            switch (dirCode) {
                case CallLog.Calls.INCOMING_TYPE:
                    Call currentIncomingCall = new Call(phoneNumber, callSeconds, callType);
                    incomingCallsList.add(currentIncomingCall);
                    break;
                case CallLog.Calls.OUTGOING_TYPE:
                    Call currentOutgoingCall = new Call(phoneNumber, callSeconds, callType);
                    outgoingCallsList.add(currentOutgoingCall);
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    Call currentMissedCall = new Call(phoneNumber, callSeconds, callType);
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
            boolean foundInIncoming = false;
            boolean foundInOutgoing = false;
            boolean foundInMissed = false;
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onQueryTextSubmit(String query) {
                getCallDetails();
                for(Call item: incomingCallsList) {
                    if (item.getCallPhoneNumber().equals(query)) {
                        foundInIncoming = true;
                    }
                }

                for(Call item: outgoingCallsList) {
                    if (item.getCallPhoneNumber().equals(query)) {
                        foundInOutgoing = true;
                    }
                }

                for(Call item: missedCallsList) {
                    if (item.getCallPhoneNumber().equals(query)) {
                        foundInMissed = true;
                    }
                }

                StringBuilder foundIn = new StringBuilder();

                if (foundInIncoming) {
                    foundIn.append("incoming ");
                }

                if (foundInOutgoing) {
                    foundIn.append("outgoing ");
                }

                if (foundInMissed) {
                    foundIn.append("missed ");
                }

                if (foundIn.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Number not found.", Toast.LENGTH_LONG).show();
                    return false;
                } else if (foundIn.length() == 1) {
                    Toast.makeText(getApplicationContext(), "Found the number in: " + foundIn.toString() + " call list.", Toast.LENGTH_LONG).show();
                    return true;
                } else {
                    StringBuilder newFoundIn = new StringBuilder();
                    String [] foundInArr = foundIn.toString().split(" ");
                    for (int i = 0; i < foundInArr.length; i++) {
                        if (i == foundInArr.length-1) {
                            newFoundIn.append(foundInArr[i]);
                        } else {
                            newFoundIn.append(foundInArr[i]);
                            newFoundIn.append(" and ");
                        }
                    }
                    Toast.makeText(getApplicationContext(), "Found the number in: " + newFoundIn.toString() + " call list.", Toast.LENGTH_LONG).show();
                    return true;
                }
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
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG)) {
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.WRITE_CALL_LOG }, 1);
            }
        } else {
            getCallDetails();
            Toast.makeText(this, "Call log has been updated.", Toast.LENGTH_SHORT).show();
        }
    }
}

