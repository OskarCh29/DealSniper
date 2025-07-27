package pl.dealsniper.core.persister;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CarDealPersister {

    private final DSLContext dsl;

}
