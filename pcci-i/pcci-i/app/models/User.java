package models;

import com.avaje.ebean.*;
import java.io.UnsupportedEncodingException;
import java.lang.Integer;
import java.lang.String;
import java.lang.System;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.*;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;
import com.avaje.ebean.Expr;

@Entity
public class User extends Model{

    @Id
    @Column(name="id")
    private Long key;

    @Constraints.Required
    @Column(unique=true, name = "user_name")
    public String userName;

    @Constraints.Required
    public String name;


    private long version;
    private Date timeStamp;




    @Version
    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    // columnDefinition could simply be = "TIMESTAMP", as the other settings are the MySQL default
    @Column(name="created_at", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }



    @Column(length = 64, nullable = false)
    private byte[] shaPassword;

    private String authToken;

    public String resetToken;

    public Integer role;

    public Long approver_id;

    public void deleteAuthToken() {
        authToken = null;
        save();
    }

    @Transient
    @Constraints.Required
    @Constraints.MinLength(6)
    @Constraints.MaxLength(256)
    @JsonIgnore
    public String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        shaPassword = getSha512(password);
        System.out.println(shaPassword);
        save();
    }

    public String createToken() {
        authToken = UUID.randomUUID().toString();
        save();
        return authToken;
    }

    @JsonIgnore
    @OneToMany(cascade=CascadeType.ALL, mappedBy="user")
    public List<Purchase_Request> purchase_request;


    public void setResetToken(String token){
        this.resetToken = token;
        System.out.println("Token is");
        System.out.println(token);
        save();
    }

    public String getAuthToken(){
        return authToken;
    }

    public static User findById(Long id){
        return User.find.byId(id);
    }

    public PagingList <Purchase_Request> approved_requests(Integer maxRowsOnPage) {
        Purchase_Request pr = new Purchase_Request();
        if(this.role==0){
            return pr.find.where().eq("status",1).eq("user_id",this.key).orderBy("approved_at desc").findPagingList(maxRowsOnPage);

        }
        else if(role==1){
            return pr.find.where().eq("status",1).eq("id_approver",this.getKey()).orderBy("approved_at desc").findPagingList(maxRowsOnPage);

        }
        return pr.find.where().eq("status",1).orderBy("approved_at desc").findPagingList(maxRowsOnPage);

    }

    public PagingList <Purchase_Request> processed_requests(Integer maxRowsOnPage) {
        Purchase_Request pr = new Purchase_Request();
        if(this.role==0){
            return pr.find.where().eq("status",2).eq("user_id",this.key).orderBy("processed_at desc").findPagingList(maxRowsOnPage);

        }
        else if(role==1){
            return pr.find.where().eq("status",2).eq("id_approver",this.getKey()).orderBy("processed_at desc").findPagingList(maxRowsOnPage);

        }
        return pr.find.where().eq("status",2).orderBy("processed_at desc").findPagingList(maxRowsOnPage);
    }

    public PagingList <Purchase_Request> submitted_requests(Integer maxRowsOnPage) {
        Purchase_Request pr = new Purchase_Request();
        if(this.role==0){
            return pr.find.where().or(Expr.eq("status",0),Expr.or(Expr.eq("status",3),Expr.eq("status",4))).eq("user_id",this.key).orderBy("created_at desc").findPagingList(maxRowsOnPage);

        }
        else if(role==1){
            return  pr.find.where().eq("status",0).or(Expr.eq("id_approver",this.getKey()),Expr.eq("user_id",this.getKey())).orderBy("created_at desc").findPagingList(maxRowsOnPage);

        }
        return pr.find.where().or(Expr.eq("status",0),Expr.or(Expr.eq("status",3),Expr.eq("status",4))).eq("user_id",this.key).orderBy("created_at desc").findPagingList(maxRowsOnPage);
    }



    public static byte[] getSha512(String value) {
        try {
            return MessageDigest.getInstance("SHA-512").digest(value.getBytes("UTF-8"));
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static User findByAuthToken(String authToken) {
        if (authToken == null) {
            return null;
        }

        try  {
            return find.where().eq("authToken", authToken).findUnique();
        }
        catch (Exception e) {
            return null;
        }
    }

    public static User findByUsernameAndPassword(String userName, String password) {

        // todo: verify this query is correct.  Does it need an "and" statement?
        return find.where().eq("user_name", userName.toLowerCase()).eq("shaPassword", getSha512(password)).findUnique();
//        return find.where().eq("user_name",userName.toLowerCase()).findUnique();
    }

    public static Model.Finder<Long,User> find = new Model.Finder<Long,User>(
            Long.class, User.class
    );

    public static User findByUserName(String userName) {
        System.out.println("Username is");
        System.out.println(find.where().eq("user_name", userName));
        return find.where().eq("user_name", userName).findUnique();
    }

    public static User findByResetToken(String resetToken){
        System.out.println("Checking Token");
        if(resetToken!=null)
            return find.where().eq("reset_token",resetToken).findUnique();
        return null;
    }



    public String getUserName() { return userName; }
    public Long getKey() {
        return key;
    }
    public void setKey(Long key) {
        this.key = key;
    }



}
