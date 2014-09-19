package controllers;

import models.*;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import java.lang.System;

public class Secured extends Security.Authenticator {

    @Override
    public String getUsername(Http.Context ctx) {
        System.out.println("user check");
        System.out.println("CONTEXT IS ---------->"+ctx.session());
        String userName = ctx.session().get("user");
        if(userName!=null && userName.length() > 0) {
            ctx.current().args.put("user_data", User.findByUserName(userName));
        }
        return userName;
    }

    @Override
    public Result onUnauthorized(Http.Context ctx) {
        return redirect(routes.Application.index());
    }
}