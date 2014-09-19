package models;

import com.avaje.ebean.Expr;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;
import java.util.List;

/**
 * Created by nitish on 11/8/14.
 */
@Entity
public class Comment extends Model {
    @Id
    @Column(name="id")
    public Long key;

    @Constraints.Required
    public Long purchase_key;

    @Constraints.Required
    public String comment;

    @Constraints.Required
    public Long userId;

    @Constraints.Required
    public String createdBy;

    @Constraints.Required
    public String timeStamp;

    public boolean ifDelete;

    public static Model.Finder<Long,Comment> find = new Model.Finder<Long,Comment>(
            Long.class, Comment.class
    );

    public static List<Comment> submitted_comments(Long id) {
        Comment pr = new Comment();
        return pr.find.where().eq("purchase_key",id).findList();
                        //Expr.eq("normal_user", key)).or().eq("approver_user", key).or().eq("finance_user", key).findList();
    }
}
