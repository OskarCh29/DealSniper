/* (C) 2025 */
package pl.dealsniper.core.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class CarDeal extends BaseDeal {

    private String location;

    private Integer mileage;

    private Integer year;
}
