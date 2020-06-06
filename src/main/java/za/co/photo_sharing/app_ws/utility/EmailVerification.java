package za.co.photo_sharing.app_ws.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import za.co.photo_sharing.app_ws.shared.dto.UserDto;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Objects;

@Component
public class EmailVerification {

    @Autowired
    private Environment environment;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private SpringTemplateEngine engine;

    @Autowired
    private JavaMailSender emailSender;
    // This address must be verified with Amazon SES.
    final String FROM = "siya.nxuseka@gmail.com";

    // The subject line for the email.
    final String EMAIL_VERIFICATION_SUBJECT = "Complete Registration!";

    final String PASSWORD_RESET_SUBJECT = "Password reset request";

    // The HTML body for the email.


    // The email body for recipients with non-HTML email clients.
    final String TEXTBODY = "Please verify your email address. "
            + "Thank you for registering with our mobile app. To complete registration process and be able to log in,"
            + " open then the following URL in your browser window: "
            + " http://ec2-35-173-238-100.compute-1.amazonaws.com:8080/verification-service/email-verification.html?token=$tokenValue"
            + " Thank you! And we are waiting for you inside!";


    final String PASSWORD_RESET_HTMLBODY = "<h1>A request to reset your password</h1>"
            + "<p>Hi, $firstName!</p> "
            + "<p>Someone has requested to reset your password with our project. If it were not you, please ignore it."
            + " otherwise please click on the link below to set a new password: "
            + "<a href='http://localhost:8080/verification-service/password-reset.html?token=$tokenValue'>"
            + " Click this link to Reset Password"
            + "</a><br/><br/>"
            + "Thank you!";

    // The email body for recipients with non-HTML email clients.
    final String PASSWORD_RESET_TEXTBODY = "A request to reset your password "
            + "Hi, $firstName! "
            + "Someone has requested to reset your password with our project. If it were not you, please ignore it."
            + " otherwise please open the link below in your browser window to set a new password:"
            + " http://localhost:8080/verification-service/password-reset.html?token=$tokenValue"
            + " Thank you!";

    public void sendVerificationMail(UserDto userDto) throws MessagingException, IOException {

        MimeMessage message = emailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());
        helper.setTo(userDto.getEmail());
        helper.setText("To confirm your account, please click here : "
                +"http://localhost:8080/photo-sharing-app-ws/users/email-verification?token="+userDto.getEmailVerificationToken());
        helper.setFrom(FROM);
        helper.setSubject(EMAIL_VERIFICATION_SUBJECT);
        helper.setSentDate(new Date());
        emailSender.send(message);
        System.out.println("Successfully sent: {} "
                + message.getSubject() +" " + message.getSender() + " " + message.getSentDate());
    }
}
