package models;

import com.avaje.ebean.Expr;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

/**
 * Created by nitish on 13/8/14.
 */
@Entity
public class SendingMails extends Model {
    @Id
    @Column(name="id")
    public Long key;

    @Constraints.Required
    public Integer fromStatus;

    @Constraints.Required
    public Integer toStatus;

    @Constraints.Required
    public Integer role;

    public static Model.Finder<Long,SendingMails> find = new Model.Finder<Long,SendingMails>(
            Long.class, SendingMails.class
    );

    public static List<SendingMails> findNotifications(Integer from, Integer to) {
        SendingMails pr = new SendingMails();
        return pr.find.where().and(Expr.eq("from_status", from), Expr.eq("to_status", to)).findList();
    }
}
