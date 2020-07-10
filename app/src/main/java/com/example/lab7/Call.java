/*

Name: Francisco Ozuna Diaz
Assignment: CS 7455 Lab 7
Lab Date: July 12, 2020 at 11:59 PM
 */

package com.example.lab7;

public class Call {
    private String callPhoneNumber;
    private String callDate;

    public Call(String callPhoneNumber, String callDate) {
        this.callPhoneNumber = callPhoneNumber;
        this.callDate = callDate;
    }

    public String getCallPhoneNumber() {
        return callPhoneNumber;
    }

    public void setCallPhoneNumber(String callPhoneNumber) {
        this.callPhoneNumber = callPhoneNumber;
    }

    public String getCallDate() {
        return callDate;
    }

    public void setCallDate(String callDate) {
        this.callDate = callDate;
    }
}
