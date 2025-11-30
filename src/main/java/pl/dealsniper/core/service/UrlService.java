/* (C) 2025 */
package pl.dealsniper.core.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import pl.dealsniper.core.dto.request.source.SourceRequest;
import pl.dealsniper.core.exception.url.UriValidationError;
import pl.dealsniper.core.exception.url.UrlConnectException;
import pl.dealsniper.core.scraper.otomoto.OtomotoSelector;
import pl.dealsniper.core.util.ScraperUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {
    ///  Separate to more services
    private static final String OTOMOTO_BASEURL = "https://www.otomoto.pl/osobowe/";
    private final RedisTemplate<String, String> redisTemplate;
    private static final int DAYS_CACHE_DURATION = 1;

    public String generateAndValidateUrl(SourceRequest sourceRequest) {
        String url = createRequestedUrl(sourceRequest);
        pingProvidedUrl(url, sourceRequest);
        return url;
    }

    private String createRequestedUrl(SourceRequest request) {
        String base = OTOMOTO_BASEURL + urlEncode(request.brand().toLowerCase()) + "/";

        if (request.model() != null && !request.model().isBlank()) {
            base += request.model().toLowerCase() + "/";
        }

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

        builder.queryParam("search[filter_enum_damaged]", request.damaged() ? "1" : "0");

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

    private void pingProvidedUrl(String url, SourceRequest request) {
        validate(
                urlCachedAndInvalid(url),
                url,
                new UrlConnectException("No result found for provided url", UriValidationError.INVALID_CACHED));
        try {
            Document document = Jsoup.connect(url)
                    .userAgent(ScraperUtil.getRandomUserAgent())
                    .header("Accept-Language", ScraperUtil.LANGUAGE_HEADER)
                    .header("Referer", ScraperUtil.getRandomReferer())
                    .header("Connection", "keep-alive")
                    .timeout(ScraperUtil.REQUEST_TIMEOUT)
                    .get();

            boolean noResult =
                    document.select("h1").stream().anyMatch(h1 -> h1.text().contains("Brak wyników wyszukiwania"));

            boolean hasOffers = document.select(OtomotoSelector.OFFER_SELECTOR).stream()
                    .map(offer -> offer.select(OtomotoSelector.OFFER_TITLE).text())
                    .anyMatch(title ->
                            title.toLowerCase().contains(request.brand().toLowerCase()));

            validate(
                    noResult,
                    url,
                    new UrlConnectException("No result found for provided url", UriValidationError.NO_SEARCH_RESULT));
            validate(
                    !hasOffers,
                    url,
                    new UrlConnectException(
                            "Provided URL redirected to main page - no results",
                            UriValidationError.NO_MATCHING_OFFERS));

        } catch (IOException e) {
            log.error("Exception encountered while connecting to {} : {}", url, e.getMessage());
            cacheInvalidUrl(url);
            throw new UrlConnectException(
                    "Connection error while connecting to provided URL", UriValidationError.CONNECTION_ERROR);
        }
    }

    private void cacheInvalidUrl(String url) {
        redisTemplate.opsForValue().set(url, "true", Duration.ofDays(DAYS_CACHE_DURATION));
    }

    private boolean urlCachedAndInvalid(String url) {
        return redisTemplate.hasKey(url);
    }

    private String urlEncode(String toEncode) {
        return URLEncoder.encode(toEncode, StandardCharsets.UTF_8);
    }

    private void validate(boolean condition, String url, UrlConnectException exception) {
        if (condition) {
            log.warn("URL validation failed. Reason: {}", exception.getReason());
            cacheInvalidUrl(url);
            throw exception;
        }
    }
}
