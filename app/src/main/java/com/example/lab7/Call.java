/*

Name: Francisco Ozuna Diaz
Assignment: CS 7455 Lab 7
Lab Date: July 12, 2020 at 11:59 PM
 */

package com.example.lab7;

public class Call {
    private String callPhoneNumber;
    private Long callDate;
    private String callType;

    public Call(String callPhoneNumber, Long callDate, String callType) {
        this.callPhoneNumber = callPhoneNumber;
        this.callDate = callDate;
        this.callType = callType;
    }

    public String getCallPhoneNumber() {
        return callPhoneNumber;
    }

    public void setCallPhoneNumber(String callPhoneNumber) {
        this.callPhoneNumber = callPhoneNumber;
    }

    public Long getCallDate() {
        return callDate;
    }

    public void setCallDate(Long callDate) {
        this.callDate = callDate;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }
}
