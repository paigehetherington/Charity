package com.theironyard;

import org.junit.Test;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by vajrayogini on 3/1/16.
 */
public class MainTest {
    public Connection startConnection() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:./test");
        Main.createTables(conn);
        return conn;
    }

    public void endConnection(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("DROP TABLE users");
        stmt.execute("DROP TABLE donations");
        conn.close();
    }
    @Test
    public void testUser() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Paige", "");
        User user = Main.selectUser(conn, "Paige");
        endConnection(conn);
        assertTrue(user != null);

    }
    @Test
    public void testDonation() throws SQLException {
        Connection conn= startConnection();
        Main.insertUser(conn, "Paige", "");
        User user = Main.selectUser(conn, "Paige");
        Main.insertDonation(conn, user.id, "Bill", "Iowa", "4");
        Donation donation = Main.selectDonation(conn, 1);
        endConnection(conn);
        assertTrue(donation != null);

    }

    @Test
    public void testDonationsArray() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Paige", "");
        Main.insertUser(conn, "Bill", "");
        User paige = Main.selectUser(conn, "Paige");
        User bill = Main.selectUser(conn, "Bill");
        Main.insertDonation(conn, paige.id, "John", "California", "50");
        Main.insertDonation(conn, bill.id, "Betty", "Nepal", "100");
        Main.insertDonation(conn, bill.id, "Tom", "Hawaii", "100");
        ArrayList<Donation> donations = Main.selectDonations(conn);
        endConnection(conn);
        assertTrue(donations.size() == 3);
    }

    @Test
    public void testUpdate() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Paige", "");
        User paige = Main.selectUser(conn, "Paige");
        Main.insertDonation(conn, paige.id, "John", "California", "100");
        Donation donation = Main.selectDonation(conn, 1);
        donation.region = "Hawaii";
        Main.updateDonation(conn, donation);
        Donation donationUpdated = Main.selectDonation(conn, 1);
        endConnection(conn);
        assertTrue(donationUpdated.region.equals("Hawaii"));
    }

    @Test
    public void testDelete() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Paige", "");
        User paige = Main.selectUser(conn, "Paige");
        Main.insertDonation(conn, paige.id, "John", "California", "100");
        Donation donation = Main.selectDonation(conn, 1);
        Main.deleteDonation(conn, paige.id);
        Donation deletedDonation = Main.selectDonation(conn, 1);
        endConnection(conn);
        assertTrue(deletedDonation == null);

    }




}