package com.theironyard;

import com.sun.org.apache.xpath.internal.operations.Mod;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.HashMap;

public class Main {

    static HashMap<String, User> donors = new HashMap<>();




    public static void main(String[] args) {

        Spark.init();

        Spark.get(
                "/",
                ((request, response) ->  {
                    Session session = request.session();
                    String userName = session.attribute("userName");
                    User user = donors.get(userName);

                    HashMap m = new HashMap();
                    m.put("userName", userName);
                    return new ModelAndView(m, "home.html");



                }),
                new MustacheTemplateEngine()

        );

        Spark.post(
                "/login",
                ((request, response) ->  {
                    String name = request.queryParams("loginName");
                    if (name == null) {
                        throw new Exception("Please enter login name.");
                    }

                    User user = donors.get(name);
                    if (user == null) {
                        user = new User(name);
                        donors.put(name, user);
                    }
                    Session session = request.session();
                    //String userName = session.attribute("userName");
                    session.attribute("userName", name);
                    response.redirect("/");
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
}
