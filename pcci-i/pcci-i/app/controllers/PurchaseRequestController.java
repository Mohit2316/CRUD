package controllers;
//import com.sun.media.jfxmedia.logging.Logger;

import com.avaje.ebean.Expr;
import com.ning.http.client.FilePart;
import models.*;
import controllers.*;
import org.jboss.netty.handler.codec.http.multipart.FileUpload;
import play.libs.Json;
import play.Logger;
import play.data.Form;
import play.data.*;
import play.data.validation.Constraints;
import play.i18n.Messages;
import play.libs.Json;
import play.cache.*;
import com.avaje.ebean.*;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
//import views.html.forms;
//import sun.org.mozilla.javascript.internal.json.JsonParser;
import views.html.login;
import views.html.forgotPassword;
import views.html.*;
import javax.imageio.ImageIO;
import play.mvc.Http.Request;
//import javax.xml.transform.Result;

//import javax.xml.transform.Result
import play.cache.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Random;
import java.io.IOException;
import java.lang.*;
import java.lang.Boolean;
import java.lang.Exception;
import java.lang.ExceptionInInitializerError;
import java.lang.Float;
import java.lang.Integer;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.System;
import java.nio.file.FileStore;
import java.nio.file.Path;
import java.sql.Blob;
//import java.sql.Date;
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

import javax.swing.text.html.HTML;
//import javax.xml.transform.Result;

import play.mvc.BodyParser;
import play.libs.Json;
import play.libs.Json.*;
import static play.libs.Json.toJson;


import org.apache.commons.mail.*;
//import javax.mail.*;
//import javax.mail.internet.*;
//import org.apache.commons.mail.HtmlEmail;

//import play.libs.Mail;
import static play.data.Form.form;


public class PurchaseRequestController extends BaseAuthenticatedController{

     public String getRole(){

        User user = super.getUser();
        if(user!=null){
            if(user.role==0){
                return "normal";
            }
            else if(user.role==1){
                return "approver";

            }
            else if(user.role==2){
                return "financer";
            }

        }
        return null;



        }


        public static Result all(){  //read from database;
    //        String username = ctx().session().get("user");
            BaseAuthenticatedController superController = new BaseAuthenticatedController();
            PurchaseRequestController currentControl = new PurchaseRequestController();

            User userr = superController.getUser();
            Long key = userr.getKey();
            String s = request().getQueryString("status");
            String pageNumber = request().getQueryString("page");
            String search = request().getQueryString("search");

            Integer page=1;
            String uuid = session("uuid");
            if (uuid == null) {
                uuid = java.util.UUID.randomUUID().toString();
                session("uuid", uuid);
            }

            if(pageNumber!=null){
                try {
                    page = Integer.parseInt(pageNumber);
                    // Yes!  An integer.
                } catch (NumberFormatException nfe) {
                    // Not an integer
                }
            }

            PagingList<Purchase_Request> pagingLister = null;
            List<Purchase_Request> requests=new ArrayList<Purchase_Request>();
            System.out.println("The string is "+ request().queryString());
            Integer maxRowsOnPage = 6;
            if(currentControl.getRole()=="financer"){
                if(request().getQueryString("status")==null){
                    s="1";
                }
                System.out.println("Query for ");
                System.out.println(request().getQueryString("status")==null);
                Integer status;

                try{
                    status = Integer.parseInt(s);

                }
                catch (Exception e){
                    return ok("wrong query");
                }


                if(status == 2) {
                    pagingLister = userr.processed_requests(maxRowsOnPage);

                }
                else if(status== 1) {
                    pagingLister = userr.approved_requests(maxRowsOnPage);
                }


            }
            else{
                if(request().getQueryString("status")==null || request().getQueryString("status")=="0"){
                    s="0";
                }
                System.out.println("Query for ");
                System.out.println(request().getQueryString("status")==null);
                Integer status;
                try{
                    status = Integer.parseInt(s);

                }
                catch (Exception e){
                    return ok("wrong query");
                }


                if(status == 2) {
//                    pagingLister = (PagingList<Purchase_Request>) Cache.get(uuid + "processedRequests");
                    if(pagingLister==null){
                        pagingLister = userr.processed_requests(maxRowsOnPage);
                    }

                    Cache.set(uuid+"processedRequests",pagingLister);
                }
                else if(status== 1) {
//                    pagingLister = (PagingList<Purchase_Request>) Cache.get(uuid + "approvedRequests");
                    if(pagingLister==null) {
                        pagingLister = userr.approved_requests(maxRowsOnPage);
                    }
                    Cache.set(uuid+"approvedRequests",pagingLister);
                }
                else {
//                    pagingLister = (PagingList<Purchase_Request>) Cache.get(uuid + "submittedRequests");

                    if(pagingLister==null) {
                        pagingLister = userr.submitted_requests(maxRowsOnPage);
                    }else{
                        System.out.println("returned from cache is the list-----------"+pagingLister.getPage(page -1).getList());
                    }
                    Cache.set(uuid+"submittedRequests",pagingLister);
                }

            } //check financer /home?.
            Page<Purchase_Request> currentPager = pagingLister.getPage(page -1);
            Integer totalPageCount = currentPager.getTotalPageCount();
            Page<Purchase_Request>reversePager = pagingLister.getPage(totalPageCount-page);
            List<Purchase_Request> appendList = new ArrayList<Purchase_Request>();
            if(search==null||search.length()==0){
                requests = currentPager.getList();
//                int i=0;
//                while(requests.size()<maxRowsOnPage ){
//                    page=page+1;
//                    i=i+1;
//                   requests.addAll(pagingLister.getPage(totalPageCount-page).getList());
//                }

            }
            else{
                for(Integer i=0;i<totalPageCount;i++){ //make the list of all forms of respective categories.

                    appendList.addAll(pagingLister.getPage(i).getList());

                }
                for(Purchase_Request listElement: appendList){
                    if(search.toLowerCase().trim().equals(listElement.po_number.toLowerCase().trim())){
                        System.out.println("APPENDED LIST final IS -->>"+search.toLowerCase().trim()+listElement.po_number.toLowerCase().trim());

                        requests.add(listElement);
                    }
                    Double u= Math.ceil((double) requests.size()/(double) maxRowsOnPage);
                    totalPageCount = u.intValue();


                }


            }


            System.out.println("PAGES ARE llllllllllll--==--==>"+totalPageCount);
            return ok(user.render(requests,page,totalPageCount, currentControl.getRole(),s)); //every type of user has to access home page so no restrictions on /home route.
    }


    public static Result read(Long id){  //Make json from request;


        PurchaseRequestController currentControl = new PurchaseRequestController();
        Purchase_Request purchaser = Purchase_Request.find.byId(id);

//        try{
//            Json.toJson(Purchase_Request.find.where().eq("Id", id).findList());
//        }
//        catch(Exception e){
//            return ok("Technical error");
//
//        }
        List<Purchase_Request> purchaseFormData = new ArrayList<Purchase_Request>();
        BaseAuthenticatedController superController = new BaseAuthenticatedController();
        purchaseFormData.add(purchaser);
        if(superController.getUser().getKey()==purchaser.user.getKey() || (currentControl.getRole()=="approver" && superController.getUser().getKey()==purchaser.user.approver_id && (purchaser.status==0 || purchaser.status==1 || purchaser.status==2)) || (currentControl.getRole()=="financer" && (purchaser.status==1 || purchaser.status==2))){
            return ok(viewPurchaseForm.render(purchaseFormData,currentControl.getRole())); //access to only forms owned by user-approver or requester.

        }
        else{
            return redirect(routes.PurchaseRequestController.all());
        }

//       return ok(Json.toJson(Purchase_Request.find.where().eq("Id", id).findList()));
    }


    public static class CreateForm{
        public String idInput;

        public Integer ccp_number;

        public String ccp_name;

        public String account_category;

        public String requisition_type;

        public String asset_tag;

        public Float cost_estimate;

        public String delivery_date;

        public String detailed_item;

        public Integer quantity;

        public String vendor_name;

        public String approver;

        public String po_number;

        public String req;

        public String vp_sign;

        public Date vp_sign_date;

        public String vp_typed_name;

        public String name_1;

        public Date date_1;

        public String name_2;

        public Date date_2;

        public Integer status;

        public String notes;

        public String attachment;






        /**
         * Validate the authentication.
         *
         * @return null if validation ok, string with details otherwise
         */
        public String validate() {
            try{
                //Catch if there is any exceptions.
            }catch (Exception e){
                return "Bad Request";
            }


            return null;
        }



    }

    public static Result newRequest() {
        Form<CreateForm> loginForm = form(CreateForm.class).bindFromRequest();
//        System.out.print("THE VALUE IN FIELD IS"+loginForm.get().ccp_number);
        if (loginForm.hasErrors()) {
            System.out.println("the errors are " + loginForm.globalErrors());

            return ok("Error in form data");

        }
        String username = ctx().session().get("user");



        User userer = User.findByUserName(username);
        User user_approver=User.findById(userer.approver_id);
        Purchase_Request purchaseRequest;


        Date date = new Date();

        java.sql.Date sqlDate=null; //Get only date without time.

        Integer checkRejected =0;
        if(!loginForm.get().idInput.isEmpty()){

            Long idRow = Long.valueOf(loginForm.get().idInput);
            System.out.println("id row is"+idRow);
            purchaseRequest = Purchase_Request.find.byId(idRow);
            if(purchaseRequest.status==3 || purchaseRequest.status==4){
                checkRejected = 1;
            }
            purchaseRequest.updated_at=date;





        }
        else{  //Create new
            purchaseRequest=new Purchase_Request();



            purchaseRequest.created_at = date;
            purchaseRequest.updated_at=date;
//            purchaseRequest.status=0;
//            purchaseRequest.cost_Center_Number= loginForm.get().ccp_number;
//            purchaseRequest.cost_Center_Name =loginForm.get().ccp_name;

            System.out.println("THE DATE IS" + date);


        }


        if(loginForm.get().delivery_date!=null){
            Date day = null;
            try {
                day = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).parse(loginForm.get().delivery_date);
            } catch(Exception e) {
                //Catch some error
            }
            sqlDate = new java.sql.Date(day.getTime());

        }
        purchaseRequest.status=0; //set status 0 even when he re-submits or updates for first time.
        purchaseRequest.cost_Center_Number= loginForm.get().ccp_number;
        purchaseRequest.cost_Center_Name =loginForm.get().ccp_name;
        purchaseRequest.requisition_type = loginForm.get().requisition_type;
        purchaseRequest.acc_category = loginForm.get().account_category;
        purchaseRequest.notes=loginForm.get().notes;
        purchaseRequest.asset_tag = loginForm.get().asset_tag;
        purchaseRequest.cost_estimate=loginForm.get().cost_estimate;
        purchaseRequest.requested_delivery_date=sqlDate;
        purchaseRequest.detailed_item = loginForm.get().detailed_item;
        purchaseRequest.quantity = loginForm.get().quantity;
        purchaseRequest.vendor_name = loginForm.get().vendor_name;
        purchaseRequest.reqId = loginForm.get().req;

//        purchaseRequest.po_number=loginForm.get().po_number;

        purchaseRequest.vp_signature=loginForm.get().vp_sign;
        purchaseRequest.date_sig_vp =loginForm.get().vp_sign_date;
        purchaseRequest.vp_typed_name=loginForm.get().vp_typed_name;
        purchaseRequest.software_approver_signature_1=loginForm.get().name_1;
        purchaseRequest.software_approver_signature_2=loginForm.get().name_2;
        purchaseRequest.date_sign_software_1=loginForm.get().date_1;
        purchaseRequest.date_sign_software_2=loginForm.get().date_2;
        purchaseRequest.user=userer;
        purchaseRequest.id_approver=userer.approver_id;


//        purchaseRequest.setPo_number();
//        purchaseRequest.aproover = userer.aproover;


        System.out.println("Show the requested row"+purchaseRequest.cost_Center_Number);
        purchaseRequest.save();

        purchaseRequest.setPo_number(); //set after the form is created

        //File attachment

        String token = Application.randomString(20);    // Creating random name for file name
        token = purchaseRequest.getPo_number() +"-"+ token;
        Http.MultipartFormData body = request().body().asMultipartFormData();
        System.out.println(body.getFile("attachment"));
        Http.MultipartFormData.FilePart attach = body.getFile("attachment");
        if(attach!=null) {
            String extension = attach.getFilename();
            File file = attach.getFile();
            if (extension.contains(".")) { //Getting Extension of the file
                extension = extension.substring(extension.lastIndexOf('.'),extension.length());
                purchaseRequest.fileID = token + extension;
            }
        /*        File destDir = new File("/public/images");
            Path src = file.toPath();
            Path dest = new File(destDir, file.getName()).toPath();*/
            /*File file = new File(image.getFile(), "/public/images" + fileName);*/
            boolean k = file.renameTo(new File("public/uploads/" + token + extension));
            if(k)
                System.out.println(token+extension+" Attachment Uploaded Successfully");
            else
                System.out.println(token+extension+" Failed to Upload the file extension");
        }

        purchaseRequest.save();
        BaseAuthenticatedController superController = new BaseAuthenticatedController();
        String message = Messages.get("mail.header") + " " +purchaseRequest.getPo_number() + " "+Messages.get("mail.addedBy") +" "+ superController.getUser().name;

        message = message + "<br><br><br><table style=\"margin-left: 0px; border-collapse:collapse;\"><tr style=\"border-bottom: solid 1px black; border-top: solid 1px black;\"><th style=\"border-left: solid 1px black;\">PO # (to be completed by Office Manager)</th><td style=\"border-left: solid 1px black; border-right: solid 1px black;\">"+purchaseRequest.getPo_number()+"</td></tr><tr style=\"border-bottom: solid 1px black; border-top: solid 1px black;\"><th style=\"border-left: solid 1px black;\">Request Status</th><td style=\"border-left: solid 1px black; border-right: solid 1px black;\">"+purchaseRequest.getStatus()+"</td></tr><tr style=\"border-bottom: solid 1px black; border-top: solid 1px black;\"><th style=\"border-left: solid 1px black;\">Cost Center or Project #</th><td style=\"border-left: solid 1px black; border-right: solid 1px black;\">"+purchaseRequest.cost_Center_Number+"</td></tr><tr style=\"border-bottom: solid 1px black; border-top: solid 1px black;\"><th style=\"border-left: solid 1px black;\">Cost Center or Project Name</th><td style=\"border-left: solid 1px black; border-right: solid 1px black;\">"+purchaseRequest.cost_Center_Name+"</td></tr><tr style=\"border-bottom: solid 1px black; border-top: solid 1px black;\"><th style=\"border-left: solid 1px black;\">Account Category</th><td style=\"border-left: solid 1px black; border-right: solid 1px black;\">"+purchaseRequest.acc_category+"</td></tr><tr style=\"border-bottom: solid 1px black; border-top: solid 1px black;\"><th style=\"border-left: solid 1px black;\">Requisition Type#</th><td style=\"border-left: solid 1px black; border-right: solid 1px black;\">"+purchaseRequest.requisition_type+"</td></tr><tr style=\"border-bottom: solid 1px black; border-top: solid 1px black;\"><th style=\"border-left: solid 1px black;\">Asset Tag</th><td style=\"border-left: solid 1px black; border-right: solid 1px black;\">"+purchaseRequest.asset_tag+"</td></tr><tr style=\"border-bottom: solid 1px black; border-top: solid 1px black;\"><th style=\"border-left: solid 1px black;\">Cost Estimate and Source of Estimate if available</th><td style=\"border-left: solid 1px black; border-right: solid 1px black;\">"+purchaseRequest.cost_estimate+"</td></tr><tr style=\"border-bottom: solid 1px black; border-top: solid 1px black;\"><th style=\"border-left: solid 1px black;\">File Attachment</th><td style=\"border-left: solid 1px black; border-right: solid 1px black;\">";
        if(purchaseRequest.fileID!=null) {
            String url = routes.Application.index().absoluteURL(request());;
            url = url.substring(0, url.length()-1);
            message = message + "<a href="+url+"/assets/uploads/"+purchaseRequest.fileID+">"+purchaseRequest.getPo_number()+"</a>";
        }
        message = message + "</td></tr><tr style=\"border-bottom: solid 1px black; border-top: solid 1px black;\"><th style=\"border-left: solid 1px black;\">Requested Delivery Date</th><td style=\"border-left: solid 1px black; border-right: solid 1px black;\">"+purchaseRequest.getRequested_date()+"</td></tr><tr style=\"border-bottom: solid 1px black; border-top: solid 1px black;\"><th style=\"border-left: solid 1px black;\">Detailed Item or Service Description</th><td style=\"border-left: solid 1px black; border-right: solid 1px black;\">"+purchaseRequest.detailed_item+"</td></tr><tr style=\"border-bottom: solid 1px black; border-top: solid 1px black;\"><th style=\"border-left: solid 1px black;\">Quantity</th><td style=\"border-left: solid 1px black; border-right: solid 1px black;\">"+purchaseRequest.quantity+"</td></tr><tr style=\"border-bottom: solid 1px black; border-top: solid 1px black;\"><th style=\"border-left: solid 1px black;\">Vendor Name</th><td style=\"border-left: solid 1px black; border-right: solid 1px black;\">"+purchaseRequest.vendor_name+"</td></tr><tr style=\"border-bottom: solid 1px black; border-top: solid 1px black;\"><th style=\"border-left: solid 1px black;\">Approver:</th><td style=\"border-left: solid 1px black; border-right: solid 1px black;\">"+purchaseRequest.getApprover()+"</td></tr><tr style=\"border-bottom: solid 1px black; border-top: solid 1px black;\"><th style=\"border-left: solid 1px black;\">REQ # (to be completed by Office Manager)</th><td style=\"border-left: solid 1px black; border-right: solid 1px black;\">"+purchaseRequest.reqId+"</td><tr style=\"border-bottom: solid 1px black; border-top: solid 1px black;\"><th style=\"border-left: solid 1px black;\">Notes</th><td style=\"border-left: solid 1px black; border-right: solid 1px black;\">"+purchaseRequest.notes+"</td></tr></tbody></table>";
        String url = routes.PurchaseRequestController.read(purchaseRequest.getKey()).absoluteURL(request());
        String statusFlag = "createNewForm";
        if(!loginForm.get().idInput.isEmpty()){
            statusFlag = "updateByUser";
        }
        if(checkRejected==1){
            statusFlag = "resubmitByUser";
        }
        decideMailRecipients(purchaseRequest, purchaseRequest.status, purchaseRequest.status,url, message,statusFlag);
        try{
            //do something
        }
        catch(Exception e){
            //do action
        }


            return redirect(routes.PurchaseRequestController.read(purchaseRequest.key));

    }

    public static Result authenticateUpdate() {
        Form<CreateForm>  updateForm= form(CreateForm.class).bindFromRequest();

        if (updateForm.hasErrors()) {

            return ok("Error in form data");
//            System.out.println("ERRORS in Authemticate");
//            System.out.println(loginForm.globalError().message());
//           return badRequest(login.render(app.title,app.heading,loginForm));
        }





        return redirect(routes.PurchaseRequestController.all());

    }

//    public static Result authenticateUpdate() {
//        Form<CreateForm> finalForm = form(CreateForm.class).bindFromRequest();
//
//        if (finalForm.hasErrors()) {
//
//            return ok("Error in form data");
////            System.out.println("ERRORS in Authemticate");
////            System.out.println(loginForm.globalError().message());
////           return badRequest(login.render(app.title,app.heading,loginForm));
//        }
//
//
//
//
//
//        return redirect(routes.PurchaseRequestController.all());
//
//    }



    public static Result create(){
        List<Purchase_Request> list = new ArrayList<Purchase_Request>();
        PurchaseRequestController currentControl = new PurchaseRequestController();
        if(currentControl.getRole()=="approver" || currentControl.getRole()=="normal"){
            return ok(createPurchaseForm.render(form(CreateForm.class),list,currentControl.getRole()));
        }
        return redirect(routes.PurchaseRequestController.all()); //Financer can't access the create url;



    }

    public static Result update(Long id){  //Make json from request;

        List<Purchase_Request> purchaser = Purchase_Request.find.where().eq("Id", id).findList();
        BaseAuthenticatedController superController = new BaseAuthenticatedController();
        PurchaseRequestController currentControl = new PurchaseRequestController();
        Purchase_Request purchaseForm = Purchase_Request.find.byId(id);
//        try{
//            Json.toJson(Purchase_Request.find.where().eq("Id", id).findList());
//        }
//        catch(Exception e){
//            return ok("Technical error");
//
//        }
        if((currentControl.getRole()=="approver" || currentControl.getRole()=="normal") && superController.getUser().getKey()==purchaseForm.user.getKey()) {
            return ok(createPurchaseForm.render(form(CreateForm.class), purchaser, currentControl.getRole()));
        }
        return redirect(routes.PurchaseRequestController.all()); //Financer can't access the update url;

    }

    public static Result delete(Long id){  //Make json from request;

        BaseAuthenticatedController superController = new BaseAuthenticatedController();
        PurchaseRequestController currentControl = new PurchaseRequestController();
        Purchase_Request purchaser = Purchase_Request.find.byId(id);
        if((currentControl.getRole()=="approver" || currentControl.getRole()=="normal") && superController.getUser().getKey()==purchaser.user.getKey()) {

            purchaser.delete();
        }
        return redirect(routes.PurchaseRequestController.all()); //Financer can't access the delete url;
    }

    public static Result approve(Long id){  //Make json from request;

        Date date = new Date();

        BaseAuthenticatedController superController = new BaseAuthenticatedController();
        PurchaseRequestController currentControl = new PurchaseRequestController();
        Purchase_Request purchaseForm = Purchase_Request.find.byId(id);
        System.out.println("USER ROLE IS"+currentControl.getRole());
        if(currentControl.getRole()=="approver" && superController.getUser().getKey()==purchaseForm.id_approver){  //only approver can access approve page.
            try{
            purchaseForm.status = 1;
            purchaseForm.approved_at=date;
            purchaseForm.save();

                //Optimistic Lock Exception.
            }
            catch (Exception e){
                //Catch the exception.
            }

        }

        return redirect(routes.PurchaseRequestController.all());
    }

    public static Result approverComment() {
        DynamicForm requestData = Form.form().bindFromRequest();
        BaseAuthenticatedController superController = new BaseAuthenticatedController();
        PurchaseRequestController currentControl = new PurchaseRequestController();
        Date date = new Date();
        Comment commentForm = new Comment();

        Purchase_Request purchaseForm = Purchase_Request.find.byId(Long.valueOf(requestData.get("idForm")));

        if(currentControl.getRole()=="approver" && superController.getUser().getKey()==purchaseForm.id_approver) {
            try {
                commentForm.timeStamp = purchaseForm.dateFormat(date);
                commentForm.comment = requestData.get("commentApprover");
                commentForm.purchase_key = purchaseForm.getKey();
                commentForm.createdBy = superController.getUser().name;
                commentForm.save();
                String message = "New comment added by: " + superController.getUser().name;
                String url = routes.PurchaseRequestController.comments(purchaseForm.getKey()).absoluteURL(request());
                String statusFlag = "addCommentApprover";
                decideMailRecipients(purchaseForm, purchaseForm.status, purchaseForm.status,url, message,statusFlag);
               /* Application app = new Application();
                User user = User.find.byId(purchaseForm.getUserId());
                app.sendNotificationEmail(user.getUserName(),purchaseForm.getKey());*/

            } catch (Exception e) {
                //Catch the exception.
            }
        }
        return redirect(routes.PurchaseRequestController.all());
    }

    public static Result Commentfinancer() {
        DynamicForm requestData = Form.form().bindFromRequest();
        BaseAuthenticatedController superController = new BaseAuthenticatedController();
        PurchaseRequestController currentControl = new PurchaseRequestController();
        Date date = new Date();
        Comment commentForm = new Comment();


        Purchase_Request purchaseForm = Purchase_Request.find.byId(Long.valueOf(requestData.get("idForm")));
        if(purchaseForm!=null && currentControl.getRole()=="financer") {
            try {
                commentForm.timeStamp = purchaseForm.dateFormat(date);
                commentForm.comment = requestData.get("financerComment");
                commentForm.purchase_key = purchaseForm.getKey();
                commentForm.createdBy = superController.getUser().name;
                commentForm.save();
                String message = "New comment added by: " + superController.getUser().name;
                String url = routes.PurchaseRequestController.comments(purchaseForm.getKey()).absoluteURL(request());
                String statusFlag = "addCommentFinancer";
                decideMailRecipients(purchaseForm, purchaseForm.status, purchaseForm.status,url,message,statusFlag);
            } catch (Exception e) {
                //Catch the exception.
            }
        }
        return redirect(routes.PurchaseRequestController.all());
    }

    public static Result approverAccept(){  //Make json from request;
        DynamicForm requestData = Form.form().bindFromRequest();

        Date date = new Date();
        BaseAuthenticatedController superController = new BaseAuthenticatedController();

        PurchaseRequestController currentControl = new PurchaseRequestController();
        System.out.println("THE IS ISS"+Long.valueOf(requestData.get("idForm")));

        Purchase_Request purchaseForm = Purchase_Request.find.byId(Long.valueOf(requestData.get("idForm")));
        List<Purchase_Request> purchaseRequest=new ArrayList<Purchase_Request>();
        Integer fromStatus = purchaseForm.status;
        if(currentControl.getRole()=="approver" && superController.getUser().getKey()==purchaseForm.id_approver){  //only approver can access approve page.
            try{
                purchaseForm.status = 1;
                purchaseForm.comment_Approver_accept = requestData.get("commentApproverAccept");
                purchaseForm.comment_Approver_rejct = null;
                purchaseForm.approved_at=date;
                purchaseForm.save();
                String message = "Approver: " + superController.getUser().name +" accepted your Purchase request, PO Number "+purchaseForm.getPo_number();
                String url = routes.PurchaseRequestController.read(purchaseForm.getKey()).absoluteURL(request());
                String statusFlag = "approveByApprover";
                decideMailRecipients(purchaseForm, fromStatus, purchaseForm.status, url, message,statusFlag);
//                purchaseRequest.add(purchaseForm);
//                superController.sendEmail(purchaseRequest);


                //Optimistic Lock Exception.
            }
            catch (Exception e){
                //Catch the exception.
            }

        }

        return redirect(routes.PurchaseRequestController.all());
    }

    public static Result financerAccept(){  //Make json from request;
        DynamicForm requestData = Form.form().bindFromRequest();

        Date date = new Date();
        BaseAuthenticatedController superController = new BaseAuthenticatedController();

        PurchaseRequestController currentControl = new PurchaseRequestController();
        System.out.println("THE IS ISS"+Long.valueOf(requestData.get("idForm")));

        Purchase_Request purchaseForm = Purchase_Request.find.byId(Long.valueOf(requestData.get("idForm")));
        Integer fromStatus = purchaseForm.status;
        if(purchaseForm!=null && currentControl.getRole()=="financer"){
            purchaseForm.payment_method = requestData.get("paymentMethod");
            purchaseForm.comment_Financer_accept = requestData.get("comment");
//            purchaseForm.comment_Financer_rejct=null; when re-processing then do we make it null;
            purchaseForm.processed_at=date;
            purchaseForm.status = 2;
            purchaseForm.save();
            String message = "Financer: " + superController.getUser().name +" processed your Purchase request, PO Number"+purchaseForm.getPo_number();
            String url = routes.PurchaseRequestController.read(purchaseForm.getKey()).absoluteURL(request());
            String statusFlag = "processByFinancer";
            decideMailRecipients(purchaseForm, fromStatus, purchaseForm.status, url, message,statusFlag);
        }



        return redirect(routes.PurchaseRequestController.all());
    }

//    public static Result process(Long id){  //Make json from request;
//
//        Date date = new Date();
//
//        PurchaseRequestController currentControl = new PurchaseRequestController();
//        System.out.println("USER ROLE IS"+currentControl.getRole());
//        if(currentControl.getRole()=="financer"){  //only approver canaccess approve page.
//            Purchase_Request purchaseForm = Purchase_Request.find.byId(id);
//            purchaseForm.processed_at=date;
//            purchaseForm.status = 2;
//            purchaseForm.save();
//            try{
//                //Optimistic Lock Exception.
//            }
//            catch (Exception e){
//                //Catch the exception.
//            }
//
//        }
//
//        return redirect(routes.PurchaseRequestController.all());
//    }


    public static Result rejectByApprover(Long id){  //Make json from request;

        BaseAuthenticatedController superController = new BaseAuthenticatedController();

        PurchaseRequestController currentControl = new PurchaseRequestController();
        Purchase_Request purchaseForm = Purchase_Request.find.byId(id);
        System.out.println("USER ROLE IS"+currentControl.getRole());
        Integer fromStatus = purchaseForm.status;
        if(currentControl.getRole()=="approver" && superController.getUser().getKey()==purchaseForm.id_approver){  //only approver canaccess approve page. + approver should only access for his forms.

            purchaseForm.approved_at=null;
            purchaseForm.status = 3;
            purchaseForm.save();
            String message = "The application was rejected by approver: " + superController.getUser().name;
            String statusFlag = "rejectByApprover";
            String url = routes.PurchaseRequestController.read(purchaseForm.getKey()).absoluteURL(request());
            decideMailRecipients(purchaseForm, fromStatus, purchaseForm.status, url, message,statusFlag);

        }

        return redirect(routes.PurchaseRequestController.all());
    }


    public static Result approverReject(){  //Make json from request;
        DynamicForm requestData = Form.form().bindFromRequest();

        Date date = new Date();
        BaseAuthenticatedController superController = new BaseAuthenticatedController();

        PurchaseRequestController currentControl = new PurchaseRequestController();
        System.out.println("THE IS ISS"+Long.valueOf(requestData.get("idForm")));

        Purchase_Request purchaseForm = Purchase_Request.find.byId(Long.valueOf(requestData.get("idForm")));

        Integer fromStatus = purchaseForm.status;
        if(currentControl.getRole()=="approver" && superController.getUser().getKey()==purchaseForm.id_approver){  //only approver can access approve page.
            try{
                purchaseForm.status = 3;
                purchaseForm.comment_Approver_rejct = requestData.get("commentApproverReject");
                purchaseForm.approved_at=null;
                purchaseForm.save();
                String message = "The purchase request, PO Number "+purchaseForm.getPo_number()+" was rejected by approver: " + superController.getUser().name;
                String statusFlag = "rejectByApprover";

                String url = routes.PurchaseRequestController.read(purchaseForm.getKey()).absoluteURL(request());
                decideMailRecipients(purchaseForm, fromStatus, purchaseForm.status,url, message,statusFlag);
                //Optimistic Lock Exception.
            }
            catch (Exception e){
                //Catch the exception.
            }

        }

        return redirect(routes.PurchaseRequestController.all());
    }

    public static Result financerReject(){  //Make json from request;

        DynamicForm requestData = Form.form().bindFromRequest();


        BaseAuthenticatedController superController = new BaseAuthenticatedController();

        PurchaseRequestController currentControl = new PurchaseRequestController();
        System.out.println("THE IS ISS"+Long.valueOf(requestData.get("idForm")));

        Purchase_Request purchaseForm = Purchase_Request.find.byId(Long.valueOf(requestData.get("idForm")));
        Integer fromStatus = purchaseForm.status;
        if(purchaseForm!=null && currentControl.getRole()=="financer"){

            purchaseForm.comment_Financer_reject = requestData.get("commentReject");
            purchaseForm.comment_Financer_accept = null;
            purchaseForm.payment_method=null;

            purchaseForm.approved_at=null;
            purchaseForm.processed_at=null;
            purchaseForm.status = 4;
            purchaseForm.save();
            String message = "The purchase request, PO Number "+purchaseForm.getPo_number()+" was rejected by financer: " + superController.getUser().name;
            String statusFlag = "rejectByFinancer";
            String url = routes.PurchaseRequestController.read(purchaseForm.getKey()).absoluteURL(request());
            decideMailRecipients(purchaseForm, fromStatus, purchaseForm.status, url, message,statusFlag);
        }



        return redirect(routes.PurchaseRequestController.all());
    }

    public static Result comments(Long id) {
        BaseAuthenticatedController superController = new BaseAuthenticatedController();
        User userr = superController.getUser();
        PurchaseRequestController currentControl = new PurchaseRequestController();


        Purchase_Request poNumber = Purchase_Request.find.byId(id);
        List<Comment> comments = Comment.submitted_comments(id);
        System.out.println(comments+"-----------------------");
        if(poNumber == null) {
            return ok(comment.render(comments,currentControl.getRole(),"0","3"));
        }
        return ok(comment.render(comments,currentControl.getRole(),poNumber.getPo_number(),"3"));

    }

    public static void decideMailRecipients(Purchase_Request purchaseForm, Integer fromStatus, Integer toStatus, String url, String message,String statusFlag) {
        List<SendingMails> mailing = SendingMails.findNotifications(fromStatus, toStatus); // Getting the list of whom to mail
        Application app = new Application();
        User user = new User();
//        String subject = Messages.get("mail.subject") + purchaseForm.getPo_number();
        String subject = null;


        switch (statusFlag){
            case "createNewForm":
                subject = Messages.get("mail.subject.createnewform");
                break;
            case "updateByUser":
                subject = Messages.get("mail.subject.updateform");
                break;
            case "resubmitByUser":
                subject = Messages.get("mail.subject.reupdateform");
                break;
            case "approveByApprover":
                subject = Messages.get("mail.subject.approver.approveform");
                break;
            case "rejectByApprover":
                subject = Messages.get("mail.subject.approver.rejectform");
                break;
            case "processByFinancer":
                subject = Messages.get("mail.subject.financer.processform");
                break;
            case "rejectByFinancer":
                subject = Messages.get("mail.subject.financer.rejectform");
                break;
            case "addCommentFinancer":
                subject = Messages.get("mail.subject.financer.comment");
                break;
            case "addCommentApprover":
                subject = Messages.get("mail.subject.approver.comment");
                break;
            default:
                subject="N/A";

        }
        subject+= purchaseForm.getPo_number();

        for (SendingMails temp : mailing) {
            if(temp.role==0 ) {

                user = User.find.byId(purchaseForm.getUserId());



                app.sendNotificationEmail(user.getUserName(), purchaseForm.getKey(), url, message, subject);
            }
            else if(temp.role == 1 && statusFlag!="addCommentApprover") {
                user = User.find.byId(purchaseForm.id_approver);
                app.sendNotificationEmail(user.getUserName(), purchaseForm.getKey(), url, message, subject);
            } else {
                //If role is finance then put the required action here.
                // To be filled.
            }
        }
    }
//    public static Result rejectByFinancer(Long id){  //Make json from request;
//
//
//
//        PurchaseRequestController currentControl = new PurchaseRequestController();
//        System.out.println("USER ROLE IS"+currentControl.getRole());
//        if(currentControl.getRole()=="financer"){  //only approver canaccess approve page.
//            Purchase_Request purchaseForm = Purchase_Request.find.byId(id);
//            purchaseForm.approved_at=null;
//            purchaseForm.processed_at=null;
//            purchaseForm.status = 4;
//            purchaseForm.save();
//
//        }
//
//        return redirect(routes.PurchaseRequestController.all());
//    }



}

