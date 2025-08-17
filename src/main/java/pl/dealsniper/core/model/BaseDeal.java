package pl.dealsniper.core.model;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public abstract class BaseDeal {

    private Long id;

    private String title;

    private BigDecimal price;

    private String currency;

    private String offerUrl;

    private Long sourceId;
}
