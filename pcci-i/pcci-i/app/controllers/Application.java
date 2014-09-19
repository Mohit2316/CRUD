package controllers;

import com.typesafe.plugin.MailerAPI;
import com.typesafe.plugin.MailerPlugin;
import com.avaje.ebean.*;
import models.*;
import play.api.libs.concurrent.Promise;
import play.libs.F;
import play.mvc.*;
import play.Logger;
import play.data.Form;
import play.data.validation.Constraints;
import play.i18n.Messages;
import play.libs.Json;
import com.typesafe.plugin.*;
import play.cache.*;
import play.mvc.Controller;
import play.mvc.Result;

import views.html.login;
import views.html.forgotPassword;
import views.html.*;
import javax.imageio.ImageIO;

import play.libs.F.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.System;
import java.nio.file.FileStore;
import java.sql.Blob;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.commons.mail.*;
import javax.servlet.*;
import javax.servlet.http.*;

import static play.data.Form.form;

public class Application extends Controller {
    String title = Messages.get("title");
    String heading = Messages.get("heading");
    String headingForgot = Messages.get("headingForgot");
    String headingReset = Messages.get("headingReset");
    String requestText = Messages.get("passwordResetText");


    public static String randomString(int len){

        String stringChars =  "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random ran = new Random();
        StringBuilder sb = new StringBuilder(len);
        for(int i=0;i<len;i++){
            sb.append(stringChars.charAt(ran.nextInt(stringChars.length())));

        }
        return sb.toString();
    }

    private static boolean checkLoggedIn(){
        String username = ctx().session().get("user");


        System.out.println("Checking for existing session");
        System.out.println(username);
        if (username != null) {
            User user = User.findByUserName(username);
            if (user != null ) {
                return true;
            } else {
                Logger.debug("Clearing invalid session credentials");
                session().clear();
            }
        }
        return false;

    }
//    public static Result defaultPage(){
//       return redirect(routes.Application.index());
//
//    }



    public static Result index() {                     //Launching first page of app.
        Application app=new Application();


        if(checkLoggedIn()){



            return redirect(
                    routes.PurchaseRequestController.all()
            );
        }
        System.out.println("Request is "+request());
        return  ok(login.render(app.title,app.heading,form(Login.class)));   //render login.scala.html with arguement.

    }

    public static Result forgotPassword(){
        Application app=new Application();
        return ok(forgotPassword.render(app.title,app.headingForgot,app.requestText,form(Forgot.class)));
    }

    public static Result resetPasswordForm(){
        Application app=new Application();
        String token = request().getQueryString("token");
        System.out.println("token-----------------------------");
        System.out.println(token);
        User user= null;
        user = User.findByResetToken(token);
        if(user==null){
            flash("success", Messages.get("token.invalid"));
            return redirect(routes.Application.index());

        }

        return ok(resetPassword.render(app.title,app.headingReset,form(Resetpass.class)));
    }
    public static Result logout() {
        session().clear();
//        response().setHeader(CACHE_CONTROL,"no-cache, no-store, must-revalidate");
//        response().setHeader(PRAGMA,"no-cache");

        flash("success", Messages.get("youve.been.logged.out"));
        return redirect(
                routes.Application.index()
        );
    }


    /**
     * Login class used by Login CForm.
     */
    public static class Login {

        @Constraints.Required
        public String username;

        @Constraints.Required
        public String password;

        /**
         * Validate the authentication.
         *
         * @return null if validation ok, string with details otherwise
         */
        public String validate() {
            System.out.println("check for validation");
            User user= null;

            try {
                user = User.findByUsernameAndPassword(username,password);
            } catch (Exception e) {
               return Messages.get("error.technical");
            }
            if (user == null) {
                return Messages.get("invalid.user.or.password");
            }
            return null;
        }

        public User getUser() {
            System.out.println("RUN THIS TOO");
           return User.findByUsernameAndPassword(username, password);
        }

    }

    public static class Forgot {

        @Constraints.Required
        public String username;



        /**
         * Validate the authentication.
         *
         * @return null if validation ok, string with details otherwise
         */
        public String validate() {
            System.out.println("check for validation");
            User user= null;

            try {
                System.out.println("check for validation with"+username);
                user = User.findByUserName(username);
                System.out.println("returned"+user);





            } catch (Exception e) {
              return Messages.get("error.technical");
            }
            if (user == null) {
                return Messages.get("youre.not.registered");
            }
            System.out.println("USER IS");
            System.out.println(user.userName);




            String token = randomString(32);
            user.setResetToken(token);
            // Recipient's email ID needs to be mentioned.
            String to = user.userName;//change accordingly

            // Sender's email ID needs to be mentioned
            String from = "y.mohit2316@gmail.com";//change accordingly
            final String username = "y.mohit2316@gmail.com";//change accordingly
            final String password = "webmail12ABCD";//change accordingly

            // Assuming you are sending email through relay.jangosmtp.net
            String host = "smtp.gmail.com";

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", "587");
            // Get the Session object.
            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });

            try {
                // Create a default MimeMessage object.
                Message message = new MimeMessage(session);

                // Set From: header field of the header.
                message.setFrom(new InternetAddress(from));

                // Set To: header field of the header.
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(to));

                // Set Subject: header field
                message.setSubject("Testing Subject");

                // Now set the actual message
//                message.setText("Hello, the password reset link is" +routes.Application.index().absoluteURL(request())+"resetPassword?token="+token);
                String url = routes.Application.index().absoluteURL(request())+"resetPassword?token="+token;
               message.setContent("<h1>Hi "+user.name+",</h1><br/><div>Your requested password reset link is <a href="+url+">"+url+"</a></div>","text/html");
                // Send message
                Transport.send(message);

                System.out.println("Sent message successfully....");

            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
            System.out.println(routes.Application.index().absoluteURL(request())+"resetPassword?token="+token);


            return null;
        }

        public User getUser() {
            System.out.println("RUN THIS TOO");
            return User.findByUserName(username);
        }

    }

    public static class Resetpass{


//        public String token;
        public String formToken;

        @Constraints.Required
        public String password;

        /**
         * Validate the authentication.
         *
         * @return null if validation ok, string with details otherwise
         */
        public String validate() {           //validate on basis of token;
            System.out.println("check for token validation");
            System.out.println("token is");

            System.out.println(formToken);
            User user= null;

          try {
               user = User.findByResetToken(formToken);


            } catch (Exception e) {
               return Messages.get("error.technical");
           }
            if (user == null) {
               return Messages.get("token.invalid");
            }


            user.setPassword(password);
            user.setResetToken(null);
            return null;
        }



    }



    public static Result authenticate() {
        Form<Login> loginForm = form(Login.class).bindFromRequest();
        Application app=new Application();


        if (loginForm.hasErrors()) {
            flash("success", loginForm.globalError().message());
            return redirect(
                    routes.Application.index()
            );

        }



        session("user", loginForm.get().username);


            return redirect (
                    routes.PurchaseRequestController.all()
            );

            //return ok(main.render(app.title,"Welcome to this page"));

    }

    public static Result authenticateEmail() {
        Form<Forgot> forgotPassForm = form(Forgot.class).bindFromRequest();
        Application app=new Application();
        System.out.println("Control in Authemticate-forgot");

        if (forgotPassForm.hasErrors()) {
            flash("success", forgotPassForm.globalError().message());
            return redirect(
                    routes.Application.index()
            );
//            return badRequest(forgotPassword.render(app.title,app.headingForgot,app.requestText,forgotPassForm));
        } else {


            return ok("<h1>Your Password Has been sent to "+forgotPassForm.get().username+"!</h1>").as("text/html");
        }
    }


    /***************************
     *Authenticate the resetpassword for.
     */

    public static Result authenticatePass() {
        Form<Resetpass> resetForm = form(Resetpass.class).bindFromRequest();
        Application app=new Application();
        System.out.println("Control in Authemticate-forgot");

        if (resetForm.hasErrors()) {
            flash("success", resetForm.globalError().message());
            return redirect(
                    routes.Application.index()
            );

        } else {


            return redirect(routes.Application.index());
        }
    }


    public static class SendNotify {

        @Constraints.Required
        public String username;

        @Constraints.Required
        public String url;

        @Constraints.Required
        public String body;

        @Constraints.Required
        public String subject;


        /**
         * Validate the authentication.
         *
         * @return null if validation ok, string with details otherwise
         */

        F.Promise<Result> promiseOfInt = F.Promise.promise(
                new Function0<Result>() {
                    public Result apply() {
                        MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
                        mail.setSubject(subject);
                        mail.setRecipient(username);
                        mail.setFrom("y.mohit2316@gmail.com");

                        mail.sendHtml("<h3>Hi " + ",</h3><br/><div>" + body + "<br> Link: <a href=" + url + ">" + url + "</a></div>");
                        System.out.println("Helo--------------------------------");
                        return ok("Success");
                    }
                }
        );
        /*public String validate(Long key) {
            System.out.println("check for validation");
            User user = null;

            try {
                System.out.println("check for validation with" + username);
                user = User.findByUserName(username);
                System.out.println("returned" + user);


            } catch (Exception e) {
                return Messages.get("error.technical");
            }
            if (user == null) {
                return Messages.get("youre.not.registered");
            }
            System.out.println("USER IS");
            System.out.println(user.userName);

            // Recipient's email ID needs to be mentioned.
            String to = user.userName;//change accordingly

            // Sender's email ID needs to be mentioned
            *//*String from = "y.mohit2316@gmail.com";//change accordingly
            final String username = "y.mohit2316@gmail.com";//change accordingly
            final String password = "webmail12ABCD";//change accordingly

            // Assuming you are sending email through relay.jangosmtp.net
            String host = "smtp.gmail.com";

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", "587");
            // Get the Session object.
            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });

            try {
                // Create a default MimeMessage object.
                Message message = new MimeMessage(session);

                // Set From: header field of the header.
                message.setFrom(new InternetAddress(from));

                // Set To: header field of the header.
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(to));

                // Set Subject: header field
                message.setSubject(subject);

                // Now set the actual message
//                message.setText("Hello, the password reset link is" +routes.Application.index().absoluteURL(request())+"resetPassword?token="+token);
                String url = routes.Application.index().absoluteURL(request())+"comment/"+key;
                message.setContent("<h3>Hi " + user.name + ",</h3><br/><div>" + body + "<br> Link: <a href=" + url + ">" + url  + "</a></div>", "text/html");
                // Send message
                Transport.send(message);

                System.out.println("Sent message successfully....");

            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
*//*

            MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
            mail.setSubject(subject);
            mail.setRecipient(username);
            mail.setFrom("y.mohit2316@gmail.com");
            mail.sendHtml("<h3>Hi " + user.name + ",</h3><br/><div>" + body + "<br> Link: <a href=" + url + ">" + url + "</a></div>");
            return null;
        }*/
    }

    public static void sendNotificationEmail(String username, Long key, String url, String body, String subject) {
        SendNotify notify = new SendNotify();
        notify.username = username;
        notify.url = url;
        notify.subject = subject;
        notify.body = body;
        System.out.println("Helo*****************************");
        try {
            notify.promiseOfInt.get(1);
        } catch(Exception e)  {
            //catch exception

        }
        System.out.println("Helo++++++++++++++++++++++");
    }









}
