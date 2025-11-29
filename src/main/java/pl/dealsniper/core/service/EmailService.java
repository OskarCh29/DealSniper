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
import pl.dealsniper.core.dto.response.CarDealResponse;
import pl.dealsniper.core.model.CarDeal;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSenderImpl mailSender;
    private final SpringTemplateEngine templateEngine;

    public void sendOffersToUser(String userEmail, List<CarDeal> offers, String taskName) {
        Map<String, Object> content = Map.of("deals", offers);
        String templateName = "car_deals_email.html";
        String subject = "New offers for filter: " + taskName;
        sendHtmlEmail(userEmail, subject, templateName, content);
        log.info("New offers has been sent");
    }

    public void sendVerificationEmail(String userEmail, String verificationLink) {
        Map<String, Object> content = Map.of("verificationLink", verificationLink);
        String subject = "DealSniper verification";
        String templateName = "verification_email.html";
        sendHtmlEmail(userEmail, subject, templateName, content);
        log.info("Verification requestedEmail has been sent to {}", userEmail);
    }

    public void sendUserFilterOffers(String userEmail, List<CarDealResponse> filteredOffers) {
        Map<String, Object> content = Map.of("deals", filteredOffers);
        String subject = "DealSniper - Your active offers";
        String templateName = "car_deals_offers_email.html";
        sendHtmlEmail(userEmail, subject, templateName, content);
        log.info("User requested offers has been send");
    }

    private void sendHtmlEmail(String sendTo, String subject, String templateName, Map<String, Object> content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(sendTo);
            helper.setSubject(subject);

            Context context = new Context();
            context.setVariables(content);

            String htmlContent = templateEngine.process(templateName, context);
            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new IllegalStateException("Email error occurred while sending email with subject: " + subject);
        }
    }
}
