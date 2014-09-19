package utils;

import au.com.bytecode.opencsv.CSVReader;
import models.Comment;
import models.SendingMails;
import models.User;

import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

/**
 * Created by nitish on 11/8/14.
 */
public class DemoData {
    public static void loadDemoData() throws IOException, ParseException, DataException {
        Date date = new Date();


        CSVReader reader = new CSVReader(new FileReader("data/users.csv"));
        String[] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            if (nextLine[0].startsWith("#")) {
                continue;
            }
            User user = new User();
            user.setKey(Long.parseLong(nextLine[0]));
            user.userName = nextLine[1];
            user.name = nextLine[2];
            user.getVersion();
            user.setTimeStamp(date);
            user.setPassword(nextLine[5]);
            user.createToken();
            user.resetToken = nextLine[7];
            user.role = Integer.parseInt(nextLine[8]);
            System.out.println(nextLine[9]);
            if(nextLine[9]!=null)
                user.approver_id = Long.parseLong(nextLine[9]);
            user.save();
        }


        reader = new CSVReader(new FileReader("data/comments.csv"));
        while ((nextLine = reader.readNext()) != null) {
            if (nextLine[0].startsWith("#")) {
                continue;
            }
            Comment comment = new Comment();
            comment.key = Long.parseLong(nextLine[0]);
            comment.purchase_key = Long.parseLong(nextLine[1]);
            comment.comment = nextLine[2];
            comment.ifDelete = Boolean.parseBoolean(nextLine[3]);
            comment.userId = Long.parseLong(nextLine[4]);
            System.out.println("Comment Loaded");
            comment.save();
        }

        reader = new CSVReader(new FileReader("data/sendingMails.csv"));
        while ((nextLine = reader.readNext()) != null) {
            if (nextLine[0].startsWith("#")) {
                continue;
            }
            SendingMails send = new SendingMails();
            send.key = Long.parseLong(nextLine[0]);
            send.fromStatus = Integer.parseInt(nextLine[1]);
            send.toStatus = Integer.parseInt(nextLine[2]);
            send.role = Integer.parseInt(nextLine[3]);
            System.out.println("Mailing List loaded");
            send.save();
        }

    }
}
