package controllers;

import models.*;
import play.mvc.Controller;
import play.mvc.Security;
import com.typesafe.plugin.*;

import java.util.*;
import java.lang.Exception;
import java.lang.System;


@Security.Authenticated(Secured.class)
public class BaseAuthenticatedController extends Controller {

    public static User getUser() {

        User user = (User) ctx().args.get("user_data");
        return user;
    }


    public void sendEmail(List<Purchase_Request> requests){
        try {
            System.out.println("About to send email"+requests);
            MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();


            mail.setSubject("mailer");
            mail.setRecipient("Mohit Yadav <mohit@zemosolabs.com>");
            mail.setFrom("<noreply@email.com>");
//sends html


            mail.sendHtml("<html>html</html>");
//sends text/text
        }
        catch (Exception e){
            System.out.println("Email not sent..");
        }



    }





}