package pl.dealniper.core.scraper.otomoto;

import lombok.Getter;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Getter
public class OtomotoUrl {

    private final String BASE_URL = "https://www.otomoto.pl/osobowe/";

    private final BodyType bodyType;

    private final String brand;

    private final String model;

    private final int productionYear;

    private final int maxMilage;

    private final BigDecimal maxPrice;

    private final String fuelType;

    private OtomotoUrl(urlBuilder builder) {
        this.bodyType = builder.bodyType;
        this.brand = builder.brand;
        this.model = builder.model;
        this.productionYear = builder.productionYear;
        this.maxMilage = builder.mileage;
        this.maxPrice = builder.maxPrice;
        this.fuelType = builder.fuelType;
    }

    public String getSearchUrl() {
        StringBuilder stringBuilder = new StringBuilder(BASE_URL);

        if (brand != null) {
            stringBuilder.append(brand.toLowerCase()).append("/");
        }
        if (model != null) {
            stringBuilder.append(model.replaceAll(" ", "-").toLowerCase()).append("/");
        }
        if (bodyType != null) {
            stringBuilder.append("seg-").append(bodyType.name().toLowerCase()).append("/");
        }
        if (productionYear > 0) {
            stringBuilder.append("od-").append(productionYear);
        }

        List<String> params = new ArrayList<>();

        if (fuelType != null) {
            params.add(encode("search[filter_enum_fuel_type]") + "=" + encode(fuelType));
        }

        if (maxPrice != null) {
            params.add(encode("search[filter_float_price:to]") + "=" + encode(maxPrice.toString()));
        }
        if (maxMilage > 0) {
            params.add(encode("search[filter_float_mileage:to]") + "=" + encode(String.valueOf(maxMilage)));
        }

        if (!params.isEmpty()) {
            stringBuilder.append("?").append(String.join("&", params));
        }

        return stringBuilder.toString();

    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }


    ///  The following builder was made only for training purposes and could be reduced to @Builder annotation
    public static class urlBuilder {
        private BodyType bodyType;
        private String brand;
        private String model;
        private int productionYear;
        private int mileage;
        private BigDecimal maxPrice;
        private String fuelType;

        public urlBuilder bodyType(BodyType bodyType) {
            this.bodyType = bodyType;
            return this;
        }

        public urlBuilder brand(String brand) {
            this.brand = brand;
            return this;
        }

        public urlBuilder model(String model) {
            this.model = model;
            return this;
        }

        public urlBuilder productionYear(int productionYear) {
            this.productionYear = productionYear;
            return this;
        }

        public urlBuilder mileage(int mileage) {
            this.mileage = mileage;
            return this;
        }


        public urlBuilder maxPrice(BigDecimal maxPrice) {
            this.maxPrice = maxPrice;
            return this;
        }

        public urlBuilder fuelType(String fuelType) {
            this.fuelType = fuelType;
            return this;
        }

        public OtomotoUrl build() {
            return new OtomotoUrl(this);
        }

    }

}
