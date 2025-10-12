/* (C) 2025 */
package pl.dealsniper.core.dto.request.source;

public enum FuelType implements OtomotoParam {
    DIESEL("diesel"),
    PETROL("petrol"),
    PETROL_CNG("petrol-cng"),
    PETROL_LPG("petrol-lpg"),
    ELECTRIC("electric"),
    HYBRID("hybrid"),
    PLUGIN_HYBRID("plugin-hybrid"),
    HIDROGEN("hidrogen");

    private final String displayName;

    FuelType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }
}
