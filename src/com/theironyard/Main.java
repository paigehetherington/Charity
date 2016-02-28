package com.theironyard;

import com.sun.org.apache.xpath.internal.operations.Mod;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    static HashMap<String, User> donors = new HashMap<>();
    static ArrayList<Donation> allDonations = new ArrayList<>();  //AL of all donations by all users




    public static void main(String[] args) {

        Spark.externalStaticFileLocation("public");

        Spark.init();

        Spark.get(
                "/",
                ((request, response) ->  {
                    Session session = request.session();
                    String userName = session.attribute("userName");
                    User user = donors.get(userName);



                    HashMap m = new HashMap();
                    m.put("userName", userName);
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
                    if (!donors.containsKey(name)) {
                        User donor = new User(name, password);

                        donors.put(name, donor);
                        response.redirect("/");
                    } else
                    if (password.equals(donors.get(name).password)) {
                        response.redirect("/");
                    } else {
                        Spark.halt(403);
                    }
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
                    User user = getUserFromSession(request.session());
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
                    allDonations.add(donation); //adds to main arraylist
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
                    User user = getUserFromSession(request3.session());
                    Donation donation = user.donations.get(id);
                    m.put("editDonation", donation);
                    return new ModelAndView(m, "edit.html");
                }),
                new  MustacheTemplateEngine()
        );
        Spark.post(
                "/edit-donation",
                ((request2, response2) ->  {
                    User user = getUserFromSession(request2.session());
                    int donationId = Integer.valueOf(request2.queryParams("id"));
                    Donation edit = user.donations.get(donationId);
                    String editDonationAmount = request2.queryParams("donationAmount");
                    edit.donationAmount = editDonationAmount;
                    String editDonorName = request2.queryParams("donorName");
                    edit.donorName = editDonorName;
                    String editRegion = request2.queryParams("region");
                    edit.region = editRegion;
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
                    User user = donors.get(name);
                    String deleteId = request1.queryParams("id");
                    int id = Integer.valueOf(deleteId);
                    user.donations.remove(id);
                    allDonations.remove(id);
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
     static User getUserFromSession(Session session) {
         String name = session.attribute("userName");
         return donors.get(name);

     }
}
