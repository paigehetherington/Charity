package com.theironyard;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by vajrayogini on 2/25/16.
 */
public class User {
    String name;
    ArrayList<Donation> donations = new ArrayList<>();

    public User(String name) {
        this.name = name;
    }
}
