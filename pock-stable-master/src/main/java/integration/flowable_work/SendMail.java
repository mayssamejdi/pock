package integration.flowable_work;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;


@Log
@Service
public class SendMail implements JavaDelegate {

    @SneakyThrows
    public void execute(DelegateExecution execution) {

        String email = (String) execution.getVariable("email");
        System.out.println("Prepare an email to " + email);
        Properties properties = new Properties();

        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        String EmailAccount = "personnoone863@gmail.com";   //the gmail account
        String AccountPassword = "123456789!ABC";     //the password

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EmailAccount, AccountPassword);
            }
        });

        Message messageAccept = new MimeMessage(session);
        messageAccept.setFrom(new InternetAddress(EmailAccount));
        messageAccept.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
        messageAccept.setSubject("Application Control");
        messageAccept.setText("Your request has been Accepted ,resume your work ...!");

        Message messageReject = new MimeMessage(session);
        messageReject.setFrom(new InternetAddress(EmailAccount));
        messageReject.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
        messageReject.setSubject("Application Control");
        messageReject.setText("Your request has been rejected ...!");

        boolean adminChoice=(boolean) execution.getVariable("approved");

        if(adminChoice) {
            Transport.send(messageAccept);
            System.out.println("Confirmation Message sent successfully");
        }else{
            Transport.send(messageReject);
            System.out.println("Reject message sent successfully");
        }
    }
}