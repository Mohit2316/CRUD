import models.User;
import play.*;
import play.mvc.*;
import play.mvc.Http.*;

import play.libs.F.*;
import controllers.*;
import utils.DataException;
import utils.DemoData;
import static play.mvc.Results.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.Override;
import java.lang.System;
import java.text.ParseException;
import views.html.*;


import static play.mvc.Results.*;
import play.mvc.Results.*;

public class Global extends GlobalSettings {
//    @Override
//    PurchaseRequestController currentControl = new PurchaseRequestController();
/*    public Promise onError(RequestHeader request, Throwable t) {
        return Promise.pure(internalServerError(
                views.html.errorPage.render(t)
        ));
    }*/

    public void onStart(play.Application app) {
        Logger.info("Application has started");
        if (Play.isDev() && (User.find.all().size() == 0)) {
            try {
                DemoData.loadDemoData();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (DataException e) {
                e.printStackTrace();
            }
            System.out.println("Loaded Demo Data");
        }
        else {
            System.out.println("did not load");
        }
        super.onStart(app);
    }

//    public Promise<Result> onError(RequestHeader request, Throwable t) {
//        BaseAuthenticatedController req = new BaseAuthenticatedController();
//
//        System.out.println("Base control is "+ req.getUser());
//
//        return Promise.<Result>pure(internalServerError(
//                views.html.errorPage.render(t)
//        ));
//    }
//
//    public Promise<Result> onHandlerNotFound(RequestHeader request) {
//        return Promise.<Result>pure(notFound(
//                views.html.notFoundPage.render(request.uri())
//        ));
//    }

}