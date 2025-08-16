package pl.dealsniper.core.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@Setter
@SuperBuilder
public abstract class BaseDeal {

    private Long id;

    private String title;

    private BigDecimal price;

    private String currency;

    private String offerUrl;
}
