/* (C) 2025 */
package pl.dealsniper.core.dto.request.source;

public enum BodyType implements OtomotoParam {
    SEDAN("sedan"),
    SUV("suv"),
    COUPE("coupe"),
    MINIVAN("minivan"),
    COMPACT("compact"),
    COMBI("combi"),
    CABRIO("cabrio"),
    CITY_CAR("city-car"),
    MINI("mini");

    private final String displayName;

    BodyType(String displayName) {
        String apiPrefix = "seg-";
        this.displayName = apiPrefix + displayName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }
}
