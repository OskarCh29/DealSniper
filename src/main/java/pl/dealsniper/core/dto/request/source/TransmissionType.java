/* (C) 2025 */
package pl.dealsniper.core.dto.request.source;

public enum TransmissionType implements OtomotoParam {
    MANUAL("manual"),
    AUTOMATIC("automatic");

    private final String displayName;

    TransmissionType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }
}
