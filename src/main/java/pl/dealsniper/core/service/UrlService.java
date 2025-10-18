/* (C) 2025 */
package pl.dealsniper.core.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import pl.dealsniper.core.dto.request.source.SourceRequest;
import pl.dealsniper.core.exception.UrlConnectException;
import pl.dealsniper.core.scraper.Selector;
import pl.dealsniper.core.scraper.otomoto.OtomotoSelector;

@Service
public class UrlService {

    private static final String OTOMOTO_BASEURL = "https://www.otomoto.pl/osobowe/";

    public String generateAndValidateUrl(SourceRequest sourceRequest) {
        String url = createRequestedUrl(sourceRequest);
        pingProvidedUrl(url);
        return url;
    }

    private String createRequestedUrl(SourceRequest request) {
        String base = OTOMOTO_BASEURL
                + urlEncode(request.brand().toLowerCase()) + "/"
                + urlEncode(request.model().toLowerCase()) + "/";

        if (request.location() != null && !request.location().isBlank()) {
            base += request.location().toLowerCase() + "/";
        }
        if (request.bodyType() != null) {
            base += request.bodyType().getDisplayName() + "/";
        }
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(base)
                .queryParamIfPresent("search[filter_float_price:from]", Optional.ofNullable(request.minPrice()))
                .queryParamIfPresent("search[filter_float_price:to]", Optional.ofNullable(request.maxPrice()))
                .queryParamIfPresent("search[filter_float_year:from]", Optional.ofNullable(request.minYear()))
                .queryParamIfPresent("search[filter_float_year:to]", Optional.ofNullable(request.maxYear()))
                .queryParamIfPresent("search[filter_float_mileage:from]", Optional.ofNullable(request.minMileage()))
                .queryParamIfPresent("search[filter_float_mileage:to]", Optional.ofNullable(request.maxMileage()));
        if (request.damaged()) {
            builder.queryParam("search[filter_enum_damaged]", "1");
        }
        if (request.transmissionType() != null) {
            builder.queryParam(
                    "search[filter_enum_gearbox]", request.transmissionType().getDisplayName());
        }
        if (request.fuelType() != null) {
            builder.queryParam(
                    "search[filter_enum_fuel_type][0]", request.fuelType().getDisplayName());
        }

        builder.queryParam("search[order]", "relevance_web");

        return builder.toUriString();
    }

    private String urlEncode(String toEncode) {
        return URLEncoder.encode(toEncode, StandardCharsets.UTF_8);
    }

    private void pingProvidedUrl(String url) {
        try {
            Document document = Jsoup.connect(url)
                    .userAgent(Selector.getRandomUserAgent())
                    .header("Accept-Language", Selector.LANGUAGE_HEADER)
                    .header("Referer", Selector.getRandomReferer())
                    .header("Connection", "keep-alive")
                    .timeout(Selector.REQUEST_TIMEOUT)
                    .get();

            boolean offerListEmpty =
                    document.select(OtomotoSelector.OFFER_SELECTOR).isEmpty();

            boolean noResult =
                    document.select("h1").stream().anyMatch(h1 -> h1.text().contains("Brak wyników wyszukiwania"));

            if (offerListEmpty || noResult) {
                throw new UrlConnectException("Generated url seems invalid or returns no results");
            }

        } catch (Exception e) {
            throw new UrlConnectException("Connection error while connecting to provided URL");
        }
    }
}
