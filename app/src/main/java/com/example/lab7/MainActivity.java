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
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    ListView incomingCallsListView;
    ListView outgoingCallsListView;
    ListView missedCallsListView;

    ListView searchResultsListView;

    ArrayAdapter<Call> incomingCallsArrayAdapter;
    ArrayAdapter<Call> outgoingCallsArrayAdapter;
    ArrayAdapter<Call> missedCallsArrayAdapter;

    SearchView search;

    TextView searchResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        incomingCallsListView = (ListView) findViewById(R.id.listView_incomingCalls);

        outgoingCallsListView = (ListView) findViewById(R.id.listView_outgoingCalls);

        missedCallsListView = (ListView) findViewById(R.id.listView_missedCalls);

        searchResults = (TextView) findViewById(R.id.textView_results);

        search = findViewById(R.id.searchView);

        int permissions_code = 42;
        String[] permissions = {Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG};

        boolean hasPermissions = PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) &&
                PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG);

        if(!hasPermissions){
            ActivityCompat.requestPermissions(this, permissions, permissions_code);
        } else {
            getCallDetails();
            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    searchForNumber(newText);
                    return true;
                }
            });
        }
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
                            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                @Override
                                public boolean onQueryTextSubmit(String query) {
                                    searchForNumber(query);
                                    return true;
                                }

                                @Override
                                public boolean onQueryTextChange(String newText) {
                                    searchForNumber(newText);
                                    return true;
                                }
                            });
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

    public void searchForNumber(String inputNumber) {
//        final ArrayList<Call> searchResultsList = new ArrayList<>();
        inputNumber = inputNumber.replaceAll("[^0-9]", "");

        Uri callUri = Uri.parse("content://call_log/calls");
        ContentResolver resolver = getContentResolver();
        String selectionQuery = "NUMBER=?";
        Cursor managedCursor = resolver.query(callUri, null, selectionQuery, new String[] {inputNumber}, null);
        StringBuilder results = new StringBuilder();

        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);

        while (managedCursor.moveToNext()) {
            String phoneNumber = managedCursor.getString(number);
            Long callDate = managedCursor.getLong(date);
            String callType = managedCursor.getString(type);
            Long callSeconds = managedCursor.getLong(date);

            Date callDayTime = new Date(callSeconds);
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy @ hh:mm aa");
            String dateString = formatter.format(callDayTime);

            int dirCode = Integer.parseInt(callType);
            switch (dirCode) {
                case CallLog.Calls.INCOMING_TYPE:
//                    searchResultsList.add(new Call(phoneNumber, callDate, callType));
                    results.append("Phone #: " + phoneNumber + " Type: Incoming " + "on " + dateString +" \n");
                    break;
                case CallLog.Calls.OUTGOING_TYPE:
//                    searchResultsList.add(new Call(phoneNumber, callDate, callType));
                    results.append("Phone #: " + phoneNumber + " Type: Outgoing " + "on " + dateString +" \n");
                    break;
                case CallLog.Calls.MISSED_TYPE:
//                    searchResultsList.add(new Call(phoneNumber, callDate, callType));
                    results.append("Phone #: " + phoneNumber + " Type: Missed " + "on " + dateString +" \n");
                    break;
                default:
                    continue;
            }
        }

//        if (searchResultsArrayAdapter == null) {
//            searchResultsArrayAdapter.add(results.toString());
//            searchResultsListView.setAdapter(searchResultsArrayAdapter);
//        } else {
//            searchResultsArrayAdapter.clear();
//            searchResultsArrayAdapter.addAll(results.toString());
//            searchResultsArrayAdapter.notifyDataSetChanged();
//        }

        if (results.length() > 0) {
            Log.i(null, "Query Result: \n" + results.toString());
            results.insert(0,"Results found...\n");
            searchResults.setText(results.toString());
        } else {
            Log.i(null, inputNumber + " not found in call log. ");
            if(inputNumber.length() > 0) {
                searchResults.setText(inputNumber + " not found in call log.");
            } else {
                searchResults.setText("");
            }
        }

        managedCursor.close();
    }
}