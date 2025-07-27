package pl.dealsniper.core.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public abstract class BaseDeal {

    private Long id;
    private String title;
    private BigDecimal price;
    private String offerUrl;
}
