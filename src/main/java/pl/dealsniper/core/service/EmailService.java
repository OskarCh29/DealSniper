package pl.dealsniper.core.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import pl.dealsniper.core.model.CarDeal;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSenderImpl mailSender;

    public void sendOffersToUser(String userEmail, List<CarDeal> offers) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(userEmail);
            helper.setSubject("New car offers");

            helper.setText(buildCarDealEmailContent(offers), true);
            mailSender.send(message);
            log.info("New offers has been sent");
        } catch (MessagingException e) {
            throw new IllegalStateException("Error while sending offers email");
        }
    }

    private String buildCarDealEmailContent(List<CarDeal> deals) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        sb.append("<h2>New cars offers according to search filter:</h2>");
        sb.append("<table border='1' cellpadding='5' cellspacing='0'>");
        sb.append("<tr>")
                .append("<th>Title</th>")
                .append("<th>Price</th>")
                .append("<th>Currency</th>")
                .append("<th>Offer link</th>")
                .append("<th>Location</th>")
                .append("<th>Mileage</th>")
                .append("<th>Year</th>")
                .append("</tr>");
        for (CarDeal deal : deals) {
            sb.append("<tr>")
                    .append("<td>").append(deal.getTitle()).append("</td>")
                    .append("<td>").append(deal.getPrice()).append("</td>")
                    .append("<td>").append(deal.getCurrency()).append("</td>")
                    .append("<td><a href='").append(deal.getOfferUrl()).append("'>Link</a></td>")
                    .append("<td>").append(deal.getLocation()).append("</td>")
                    .append("<td>").append(deal.getMileage()).append("</td>")
                    .append("<td>").append(deal.getYear()).append("</td>")
                    .append("</tr>");
        }
        sb.append("</table></body></html>");
        return sb.toString();
    }
}
