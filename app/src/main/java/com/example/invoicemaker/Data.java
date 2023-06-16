package com.example.invoicemaker;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;

public class Data extends RealmObject {
    long recieptNo;
    int receiptCounter;
    String name, smartheName, address, donation;
    String mobileNo;
    String currentDate;
    long createdTime;

    public int getReceiptCounter() {
        return receiptCounter;
    }

    public void setReceiptCounter(int receiptCounter) {
        this.receiptCounter = receiptCounter;
    }

    public long getRecieptNo() {
        return recieptNo;
    }

    public void setRecieptNo(long recieptNo) {
        this.recieptNo = recieptNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSmartheName() {
        return smartheName;
    }

    public void setSmartheName(String smartheName) {
        this.smartheName = smartheName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getDonation() {
        return donation;
    }

    public void setDonation(String donation) {
        this.donation = donation;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }


    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

}
