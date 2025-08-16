package pl.dealsniper.core.persister;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import pl.dealsniper.core.model.CarDeal;

import java.util.List;

import static com.dealsniper.jooq.tables.CarDeals.CAR_DEALS;

@Repository
@RequiredArgsConstructor
public class CarDealPersister implements DealPersister<CarDeal> {

    private final DSLContext dsl;

    @Override
    public void save(CarDeal deal) {
        dsl.insertInto(CAR_DEALS)
                .set(CAR_DEALS.TITLE, deal.getTitle())
                .set(CAR_DEALS.PRICE, deal.getPrice())
                .set(CAR_DEALS.OFFER_URL, deal.getOfferUrl())
                .set(CAR_DEALS.MILEAGE, deal.getMileage())
                .set(CAR_DEALS.LOCATION, deal.getLocation())
                .set(CAR_DEALS.YEAR, deal.getYear())
                .onDuplicateKeyIgnore()
                .execute();

    }

    @Override
    public void saveAll(List<CarDeal> deals) {

    }

    @Override
    public boolean existsByUrl(String offerUrl) {
        return dsl.fetchExists(
                dsl.selectFrom(CAR_DEALS)
                        .where(CAR_DEALS.OFFER_URL.eq(offerUrl))
        );
    }
}
