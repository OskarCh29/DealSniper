/* (C) 2025 */
package pl.dealsniper.core.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import pl.dealsniper.core.model.CarDeal;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSenderImpl mailSender;
    private final SpringTemplateEngine templateEngine;

    public void sendOffersToUser(String userEmail, List<CarDeal> offers, String taskName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(userEmail);
            helper.setSubject("New offers for your" + taskName + "filter");

            Context context = new Context();
            context.setVariable("deals", offers);
            // context.setVariable(sourceId) --> For future unsubscribe feature

            String htmlContent = templateEngine.process("car_deals_email.html", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("New offers has been sent");

        } catch (MessagingException e) {
            throw new IllegalStateException("Error while sending offers requestedEmail");
        }
    }

    public void sendVerificationEmail(String email, String verificationLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("DealSniper verification requestedEmail");

            Context context = new Context();
            context.setVariable("verificationLink", verificationLink);

            String htmlContent = templateEngine.process("verification_email.html", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Verification requestedEmail has been sent to {}", email);

        } catch (MessagingException e) {
            throw new IllegalStateException("Error while sending verification requestedEmail");
        }
    }
}
