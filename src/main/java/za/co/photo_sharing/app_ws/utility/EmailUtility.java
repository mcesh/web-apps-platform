package za.co.photo_sharing.app_ws.utility;

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
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import za.co.photo_sharing.app_ws.entity.UserProfile;
import za.co.photo_sharing.app_ws.exceptions.UserServiceException;
import za.co.photo_sharing.app_ws.model.response.ErrorMessages;
import za.co.photo_sharing.app_ws.shared.dto.UserAppRequestDTO;
import za.co.photo_sharing.app_ws.shared.dto.UserDto;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

@Component
@ComponentScan()
@Slf4j
@Service
public class EmailUtility {

    private static String savePath = "C:/Token";
    private static Logger LOGGER = LoggerFactory.getLogger(EmailUtility.class);
    // This address must be verified with Amazon SES.
    final String FROM = "siya.nxuseka@gmail.com";
    // The subject line for the email.
    final String EMAIL_VERIFICATION_SUBJECT = "Complete Registration!";
    final String APP_REQUEST_CONFIRMATION = "APPLICATION IN PROGRESS!";
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





    public void sendAppReqVerificationMail(UserAppRequestDTO appRequestDTO, String userAgent, String webUrl) {

        try {
            MimeMessage message = emailSender.createMimeMessage();
            String emailVerificationToken = appRequestDTO.getEmailVerificationToken();

            if (userAgent.contains("Apache-HttpClient")) {
                if (determineOperatingSystem().equalsIgnoreCase("linux")){
                    savePath = "/home/Token";
                }
                utils.generateFilePath.accept(savePath);
                utils.generateFile.accept(savePath + "/appRequest.txt", appRequestDTO.getEmailVerificationToken());

            }
            MimeMessageHelper helper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());
            helper.setTo(appRequestDTO.getEmail());
            helper.setText("To confirm your account, please click here : "
                    + webUrl +"web-apps-platform/users_app_request/request-app-email-verify?token=" + appRequestDTO.getEmailVerificationToken());
            helper.setFrom(FROM);
            helper.setSubject(EMAIL_VERIFICATION_SUBJECT);
            helper.setSentDate(new Date());
            emailSender.send(message);
            getLog().info("Email sent successfully with the following details {}, {}, and {}",
                    message.getSubject(), message.getSentDate(),
                    message.getAllRecipients());
        }catch (Exception e){
            getLog().info("Email Unsuccessfully sent {}", e.getMessage());
            throw new UserServiceException(ErrorMessages.ERROR_SENDING_EMAIL.getErrorMessage());
        }
    }

    public void sendAppToken(String tokenKey, String firstName, String email) {

        MimeMessage message = null;
        try {

            message = emailSender.createMimeMessage();


            Context context = new Context();
            context.setVariable("tokenKey",tokenKey);
            context.setVariable("firstName",firstName);
            String html = engine.process("appTokenTemplate", context);

            MimeMessageHelper helper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());
            helper.setTo(email);
            helper.setText(html,true);
            helper.setFrom(FROM);
            helper.setSubject(APP_REQUEST_CONFIRMATION);
            helper.setSentDate(new Date());
            emailSender.send(message);

            getLog().info("Email sent successfully with the following details {}, {}, and {}",
                    message.getSubject(), message.getSentDate(),
                    message.getAllRecipients());
        }catch (MessagingException e){
            getLog().info("Email Unsuccessfully sent {}", e.getMessage());
            throw new UserServiceException(ErrorMessages.ERROR_SENDING_EMAIL.getErrorMessage());
        }

    }


    public Function<UserDto, Boolean> verifyEmail = userDto -> {
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
                    + "http://157.230.240.44:8080/web-apps-platform/users/email-verification?token=" + userDto.getEmailVerificationToken());
            helper.setFrom(FROM);
            helper.setSubject(EMAIL_VERIFICATION_SUBJECT);
            helper.setSentDate(new Date());
            emailSender.send(message);

            getLog().info("Email sent successfully with the following details {}, {}, and {}",
                    message.getSubject(), message.getSentDate(),
                    message.getAllRecipients());

        } catch (MessagingException e) {
            throw new RuntimeException("Error sending email: {} " + e.getMessage());
        }
        if (emailSender != null) {
            returnValue = true;
        }
        return returnValue;
    };
    public BiFunction<UserProfile, String, Boolean> sendPasswordResetReq = ((userEntity, token) -> {

        boolean returnValue = false;
        final String PASSWORD_RESET_HTMLBODY = new StringBuilder().append("Hi, ")
                .append(userEntity.getUsername())
                .append(newLine())
                .append(newLine())
                .append("You has requested to reset your password with our project. If it were not you, please ignore it.")
                .append(" otherwise please click on the link below to set a new password: ")
                .append(newLine())
                .append("<a href='http://178.128.244.72:8080/verification-service/password-reset.html?token=")
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

            if (emailSender != null) {
                returnValue = true;
            }

            getLog().info("Email sent successfully with the following details {}, {}, and {}",
                    message.getSubject(), message.getSentDate(),
                    message.getAllRecipients());


        } catch (MessagingException e) {
            throw new RuntimeException("Error sending email: {} " + e.getMessage());
        }
        return returnValue;

    });
    @Autowired
    private Utils utils;

    public static Logger getLog() {
        return LOGGER;
    }

    public void sendVerificationMail(UserDto userDto, String userAgent, String webUrl) {

        try {

            MimeMessage message = emailSender.createMimeMessage();
            String emailVerificationToken = userDto.getEmailVerificationToken();

            if (userAgent.contains("Apache-HttpClient")) {
                if (determineOperatingSystem().equalsIgnoreCase("linux")){
                    savePath = "/home/Token";
                }
                utils.generateFilePath.accept(savePath);
                utils.generateFile.accept(savePath + "/token.txt", userDto.getEmailVerificationToken());

            }
            MimeMessageHelper helper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());
            helper.setTo(userDto.getEmail());
            helper.setText("To confirm your account, please click here : "
                    + webUrl +"web-apps-platform/users/email-verification?token=" + userDto.getEmailVerificationToken());
            helper.setFrom(FROM);
            helper.setSubject(EMAIL_VERIFICATION_SUBJECT);
            helper.setSentDate(new Date());
            emailSender.send(message);
            getLog().info("Email sent successfully with the following details {}, {}, and {}",
                    message.getSubject(), message.getSentDate(),
                    message.getAllRecipients());

        }catch (MessagingException e){
            getLog().info("Email Unsuccessfully sent {}", e.getMessage());
            throw new UserServiceException(ErrorMessages.ERROR_SENDING_EMAIL.getErrorMessage());
        }
    }

    private String newLine() {

        String os = determineOperatingSystem();

        String newLineIndicator = "OS not recognized";
        if (os.equalsIgnoreCase("windows")) {
            newLineIndicator = "\r\n";
        }
        if (os.equalsIgnoreCase("linux")) {
            newLineIndicator = "\n";
        }
        return newLineIndicator;
    }

    public String determineOperatingSystem() {
        String operatingSystem;
        String os;

        operatingSystem = System.getProperty("os.name").toLowerCase();

        if (operatingSystem.contains("win")) {
            os = "windows";
        } else if (operatingSystem.contains("mac")) {
            os = "mac";
        } else if (operatingSystem.contains("nix") || operatingSystem.contains("nux") || operatingSystem.contains("aix")) {
            os = "linux";
        } else if (operatingSystem.contains("sunos")) {
            os = "solaris";
        } else {
            throw new IllegalArgumentException("ERROR Finding Operating System");
        }
        return os;
    }

}
