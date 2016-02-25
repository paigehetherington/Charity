package com.theironyard;

/**
 * Created by vajrayogini on 2/25/16.
 */
public class Donation {
    String donorName;
    String region;
    Double donationAmount;


    public Donation(String donorName, String region, Double donationAmount) {
        this.donorName = donorName;
        this.region = region;
        this.donationAmount = donationAmount;
    }
}
