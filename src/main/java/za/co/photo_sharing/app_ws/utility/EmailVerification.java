package za.co.photo_sharing.app_ws.utility;

import jdk.nashorn.internal.objects.annotations.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring5.SpringTemplateEngine;
import za.co.photo_sharing.app_ws.PhotoSharingApplication;
import za.co.photo_sharing.app_ws.entity.UserEntity;
import za.co.photo_sharing.app_ws.shared.dto.UserDto;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@Component
@ComponentScan()
@Slf4j
@Service
public class EmailVerification {

    private static Logger LOGGER = LoggerFactory.getLogger(EmailVerification.class);


    // This address must be verified with Amazon SES.
    final String FROM = "siya.nxuseka@gmail.com";
    // The subject line for the email.
    final String EMAIL_VERIFICATION_SUBJECT = "Complete Registration!";
    final String PASSWORD_RESET_SUBJECT = "Password reset request";
    // The email body for recipients with non-HTML email clients.
    final String TEXTBODY = "Please verify your email address. "
            + "Thank you for registering with our mobile app. To complete registration process and be able to log in,"
            + " open then the following URL in your browser window: "
            + " http://ec2-35-173-238-100.compute-1.amazonaws.com:8080/verification-service/email-verification.html?token=$tokenValue"
            + " Thank you! And we are waiting for you inside!";
    // The email body for recipients with non-HTML email clients.
    final String PASSWORD_RESET_TEXTBODY = "A request to reset your password "
            + "Hi, $firstName! "
            + "Someone has requested to reset your password with our project. If it were not you, please ignore it."
            + " otherwise please open the link below in your browser window to set a new password:"
            + " http://localhost:8080/verification-service/password-reset.html?token=$tokenValue"
            + " Thank you!";
    @Autowired
    private Environment environment;

    // The HTML body for the email.
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private SpringTemplateEngine engine;
    @Autowired
    private JavaMailSender emailSender;

    public void sendVerificationMail(UserDto userDto) throws MessagingException, IOException {

        MimeMessage message = emailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());
        helper.setTo(userDto.getEmail());
        helper.setText("To confirm your account, please click here : "
                + "http://localhost:8080/photo-sharing-app-ws/users/email-verification?token=" + userDto.getEmailVerificationToken());
        helper.setFrom(FROM);
        helper.setSubject(EMAIL_VERIFICATION_SUBJECT);
        helper.setSentDate(new Date());
        emailSender.send(message);
        System.out.println("Successfully sent: {} "
                + message.getSubject() + " " + message.getSentDate() + " " + Arrays.toString(message.getReplyTo()));
    }

    public Function<UserDto, Boolean> verifyEmail = userDto ->{
        boolean returnValue = false;
        MimeMessage message = emailSender.createMimeMessage();

        try {
            Optional.ofNullable(userDto).ifPresent(mimeMessage -> {
                getLog().info("Sending email to {} ", userDto.getEmail());
            });


            MimeMessageHelper helper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());
            helper.setTo(Objects.requireNonNull(userDto).getEmail());
            helper.setText("To confirm your account, please click here : "
                    + "http://localhost:8080/photo-sharing-app-ws/users/email-verification?token=" + userDto.getEmailVerificationToken());
            helper.setFrom(FROM);
            helper.setSubject(EMAIL_VERIFICATION_SUBJECT);
            helper.setSentDate(new Date());
            emailSender.send(message);

            getLog().info("Email sent successfully with the following details {}, {}, and {}",
                    message.getSubject(), message.getSentDate(),
                    message.getAllRecipients());

        }catch (MessagingException e){
            throw new RuntimeException("Error sending email: {} " + e.getMessage());
        }
            if (emailSender !=null){
                returnValue = true;
            }
        return returnValue;
    };

    public BiFunction<UserEntity,String,Boolean> sendPasswordResetReq = ((userEntity, token) -> {

        boolean returnValue = false;
        final String PASSWORD_RESET_HTMLBODY = new StringBuilder().append("Hi, ")
                .append(userEntity.getUsername())
                .append(newLine())
                .append(newLine())
                .append("You has requested to reset your password with our project. If it were not you, please ignore it.")
                .append(" otherwise please click on the link below to set a new password: ")
                .append(newLine())
                .append("<a href='http://localhost:8080/verification-service/password-reset.html?token=")
                .append(token)
                .append("'")
                .append(" Click this link to Reset Password")
                .append(newLine())
                .append(newLine())
                .append("Thank you!")
                .toString();

        try {
            Optional.of(userEntity).ifPresent(mimeMessage -> {
                getLog().info("Sending email to {} ", userEntity.getEmail());
            });
            MimeMessage message = emailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());
            helper.setTo(Objects.requireNonNull(userEntity).getEmail());
            helper.setText(PASSWORD_RESET_HTMLBODY);
            helper.setFrom(FROM);
            helper.setSubject(PASSWORD_RESET_SUBJECT);
            helper.setSentDate(new Date());
            emailSender.send(message);

            if (emailSender != null){
                returnValue = true;
            }

            getLog().info("Email sent successfully with the following details {}, {}, and {}",
                    message.getSubject(), message.getSentDate(),
                    message.getAllRecipients());


        }catch (MessagingException e){
            throw new RuntimeException("Error sending email: {} " + e.getMessage());
        }
        return returnValue;

    });


    public static Logger getLog() {
        return LOGGER;
    }

    private String newLine(){

        String os = determineOperatingSystem();

        String newLineIndicator = "OS not recognized";
        if (os.equalsIgnoreCase("windows")){
            newLineIndicator = "\r\n";
        }
        if (os.equalsIgnoreCase("linux")){
            newLineIndicator = "\n";
        }
        return newLineIndicator;
    }

    private String determineOperatingSystem(){
        String operatingSystem;
        String os;

        operatingSystem = System.getProperty("os.name").toLowerCase();

        if(operatingSystem.contains("win")){
            os = "windows";
        }else if (operatingSystem.contains("mac")){
            os = "mac";
        }else if (operatingSystem.contains("nix") || operatingSystem.contains("nux") || operatingSystem.contains("aix")){
            os = "linux";
        }else if (operatingSystem.contains("sunos")){
            os = "solaris";
        }else {
            throw new IllegalArgumentException("ERROR Finding Operating System");
        }
        return os;
    }

}
