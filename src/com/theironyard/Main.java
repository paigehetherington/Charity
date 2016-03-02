package com.theironyard;

import com.sun.org.apache.xpath.internal.operations.Mod;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

//    static HashMap<String, User> donors = new HashMap<>();
//    static ArrayList<Donation> allDonations = new ArrayList<>();  //AL of all donations by all users

    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS users(id IDENTITY, name VARCHAR, password VARCHAR)");
        stmt.execute("CREATE TABLE IF NOT EXISTS donations(id IDENTITY, user_id INT, donor_name VARCHAR, region VARCHAR, donation_amount VARCHAR)");

    }


public static void insertUser(Connection conn, String name, String password) throws SQLException {
    PreparedStatement stmt = conn.prepareStatement("INSERT INTO users VALUES(NULL, ?, ?)");
    stmt.setString(1, name);
    stmt.setString(2, password);
    stmt.execute();
}

    public static User selectUser(Connection conn, String name) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE name = ?");
        stmt.setString(1, name);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            int id = results.getInt("id");
            String password = results.getString("password");
            return new User(id, name, password);

        }
        return null;

    }
    public static void insertDonation(Connection conn, int userId, String donorName, String region, String donationAmount) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO donations VALUES(NULL, ?, ?, ?, ?)");
        stmt.setInt(1, userId);
        stmt.setString(2, donorName);
        stmt.setString(3, region);
        stmt.setString(4, donationAmount);
        stmt.execute();
    }

    public static Donation selectDonation(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM donations INNER JOIN users ON donations.user_id = users.id WHERE donations.id = ?");
        stmt.setInt(1, id);
        ResultSet results = stmt.executeQuery();
         if (results.next()) {
             String donorName = results.getString("donations.donor_name");
             String region = results.getString("donations.region");
             String donationAmount = results.getString("donations.donation_amount");
             String name = results.getString("users.name");
             return new Donation(donorName, region, donationAmount, id);
         }
        return null;
    }

    public static ArrayList<Donation> selectDonations(Connection conn) throws SQLException {
        ArrayList<Donation> donations = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM donations INNER JOIN users ON donations.user_id = users.id");
        ResultSet results = stmt.executeQuery();
        while (results.next()) {
            int id = results.getInt("donations.id");
            String donorName = results.getString("donations.donor_name");
            String region = results.getString("donations.region");
            String donationAmount = results.getString("donations.donation_amount");
            String name = results.getString("users.name");
            Donation donation = new Donation(donorName, region, donationAmount, id);
            donations.add(donation);

        }
        return donations;

    }
    public static void updateDonation(Connection conn, Donation donation) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("UPDATE donations SET donor_name = ?, region = ?, donation_amount = ? WHERE id = ?");
        stmt.setString(1, donation.donorName);
        stmt.setString(2, donation.region);
        stmt.setString(3, donation.donationAmount);
        stmt.setInt(4, donation.id);
        stmt.execute();

    }

    public static void deleteDonation(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM donations WHERE id =?");
        stmt.setInt(1, id);
        stmt.execute();

    }





    public static void main(String[] args) throws SQLException {

        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        createTables(conn);

        Spark.externalStaticFileLocation("public");

        Spark.init();

        Spark.get(
                "/",
                ((request, response) ->  {
                    Session session = request.session();
                    String userName = session.attribute("userName");
                    User user = selectUser(conn,userName);



                    HashMap m = new HashMap();
                    m.put("userName", userName);
                    ArrayList<Donation> allDonations = selectDonations(conn);
                    m.put("allDonations", allDonations); // add AL of all users' donations to HM

                    if (user != null) {
                        m.put("donations", user.donations);
                    }

                    return new ModelAndView(m, "home.html");

                }),
                new MustacheTemplateEngine()

        );

        Spark.post(
                "/login",
                ((request, response) ->  {
                    String name = request.queryParams("loginName");
                    String password = request.queryParams("password");
                    if (name == null) {
                        throw new Exception("Login name not found.");
                    }

                    User user = selectUser(conn, name);
                    if (user == null) {
                        insertUser(conn, name, password);
                    }
//                    if (!donors.containsKey(name)) {
////                        User donor = new User(name, password);
////
////                        donors.put(name, donor);
//                        response.redirect("/");
//                    } else
//                    if (password.equals(donors.get(name).password)) {
//                        response.redirect("/");
//                    } else {
//                        Spark.halt(403);
//                    }
//                    if (name == null) {
//                        throw new Exception("Please enter login name.");
//                    }
//
//                    User user = donors.get(name);
//                    if (user == null) {
//                        user = new User(name);
//                        donors.put(name, user);

                    Session session = request.session();
                    //String userName = session.attribute("userName");
                    session.attribute("userName", name);
                    response.redirect("/");
                    return "";

                })
        );


        Spark.post(
                "/create-donation",
                ((request, response) ->  {
                    User user = getUserFromSession(conn, request.session());
                    if (user == null) {
                        Spark.halt(403);
                    }

                    String donorName = request.queryParams("donorName");
                    String region = request.queryParams("region");
                    String  amount = request.queryParams("donationAmount");
                    int id = user.donations.size();
                    //Double donationAmount = Double.parseDouble(amount);
                    if (donorName == null || region == null) { //donation amount null?
                        throw new Exception("Didn't receive all query parameters");
                    }
                    //String formattedDonation = String.format("%.2f", donationAmount);

                    Donation donation = new Donation(donorName, region, amount, id);
                    user.donations.add(donation); //adds to user's array list
                    insertDonation(conn, user.id, donorName, region, amount);
                    //allDonations.add(donation); //adds to main arraylist
                    response.redirect("/");
                    return "";



                })
        );
        Spark.get(
                "/edit",
                ((request3, response3) ->  {
                    HashMap m = new HashMap();

                    String editId = request3.queryParams("editId");
                    int id = Integer.valueOf(editId);
                    User user = getUserFromSession(conn, request3.session());

                    Donation donation = selectDonation(conn, id);           //user.donations.get(id);
                    m.put("editDonation", donation);
                    return new ModelAndView(m, "edit.html");
                }),
                new  MustacheTemplateEngine()
        );
        Spark.post(
                "/edit-donation",
                ((request2, response2) ->  {
                    User user = getUserFromSession(conn, request2.session());
                    int donationId = Integer.valueOf(request2.queryParams("id"));
                    Donation edit = selectDonation(conn, donationId);                      //user.donations.get(donationId);
                    String editDonationAmount = request2.queryParams("donationAmount");
                    edit.donationAmount = editDonationAmount;
                    String editDonorName = request2.queryParams("donorName");
                    edit.donorName = editDonorName;
                    String editRegion = request2.queryParams("region");
                    edit.region = editRegion;
                    Donation donation = new Donation(editDonorName, editRegion, editDonationAmount, donationId);
                    updateDonation(conn, donation);
                    //user.donations.add(donationId, edit);
                    response2.redirect("/");
                    return "";
                })
        );



        Spark.get(
                "/delete-donation",
                ((request1, response1) ->  {
                    Session session = request1.session();
                    String name = session.attribute("userName");// one argument for getting attribute
                    //User user = selectUser(conn, name);   //donors.get(name);
                    String deleteId = request1.queryParams("id");
                    int id = Integer.valueOf(deleteId);
                    deleteDonation(conn, id);
                    //user.donations.remove(id);
                    //allDonations.remove(id);
                    response1.redirect("/");
                    return "";
                })
        );

        Spark.post(
                "/logout",
                ((request, response) ->  {
                    Session session = request.session();
                    session.invalidate();
                    response.redirect("/");
                    return "";
                })
        );



    }
     static User getUserFromSession(Connection conn, Session session) throws SQLException {
         String name = session.attribute("userName");
         //return donors.get(name);
        return selectUser(conn, name);
     }
}
