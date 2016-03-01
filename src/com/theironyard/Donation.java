package com.theironyard;

/**
 * Created by vajrayogini on 2/25/16.
 */
public class Donation {
    String donorName;
    String region;
    String donationAmount;
    int id;


    public Donation(String donorName, String region, String donationAmount, int id) {
        this.donorName = donorName;
        this.region = region;
        this.donationAmount = donationAmount;
        this.id = id;

    }

}
