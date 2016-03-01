package com.theironyard;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by vajrayogini on 2/25/16.
 */
public class User {
    int id;
    String name;
    String password;
    ArrayList<Donation> donations = new ArrayList<>();

    public User(int id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }
}
