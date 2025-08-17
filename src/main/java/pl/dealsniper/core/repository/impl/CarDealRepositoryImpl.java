package pl.dealsniper.core.repository.impl;

import static com.dealsniper.jooq.tables.CarDeals.CAR_DEALS;
import static com.dealsniper.jooq.tables.CarDealsTmp.CAR_DEALS_TMP;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import pl.dealsniper.core.mapper.CarDealMapper;
import pl.dealsniper.core.model.CarDeal;
import pl.dealsniper.core.repository.CarDealRepository;

@Repository
@RequiredArgsConstructor
public class CarDealRepositoryImpl implements CarDealRepository<CarDeal> {

    private final DSLContext dsl;
    private final CarDealMapper mapper;

    @Override
    public void save(CarDeal deal) {
        dsl.insertInto(CAR_DEALS)
                .set(CAR_DEALS.TITLE, deal.getTitle())
                .set(CAR_DEALS.PRICE, deal.getPrice())
                .set(CAR_DEALS.CURRENCY, deal.getCurrency())
                .set(CAR_DEALS.OFFER_URL, deal.getOfferUrl())
                .set(CAR_DEALS.MILEAGE, deal.getMileage())
                .set(CAR_DEALS.LOCATION, deal.getLocation())
                .set(CAR_DEALS.YEAR, deal.getYear())
                .onDuplicateKeyIgnore()
                .execute();
    }

    @Override
    public boolean existsByUrl(String offerUrl) {
        return dsl.fetchExists(dsl.selectFrom(CAR_DEALS).where(CAR_DEALS.OFFER_URL.eq(offerUrl)));
    }

    @Override
    public void mergeFromTempTable() {
        dsl.insertInto(CAR_DEALS)
                .columns(
                        CAR_DEALS.TITLE,
                        CAR_DEALS.PRICE,
                        CAR_DEALS.CURRENCY,
                        CAR_DEALS.OFFER_URL,
                        CAR_DEALS.LOCATION,
                        CAR_DEALS.MILEAGE,
                        CAR_DEALS.YEAR,
                        CAR_DEALS.ACTIVE,
                        CAR_DEALS.SOURCE_ID)
                .select(dsl.select(
                                CAR_DEALS_TMP.TITLE,
                                CAR_DEALS_TMP.PRICE,
                                CAR_DEALS_TMP.CURRENCY,
                                CAR_DEALS_TMP.OFFER_URL,
                                CAR_DEALS_TMP.LOCATION,
                                CAR_DEALS_TMP.MILEAGE,
                                CAR_DEALS_TMP.YEAR,
                                DSL.inline(true),
                                CAR_DEALS_TMP.SOURCE_ID)
                        .from(CAR_DEALS_TMP))
                .onConflict(CAR_DEALS.OFFER_URL, CAR_DEALS.SOURCE_ID)
                .doUpdate()
                .set(CAR_DEALS.TITLE, CAR_DEALS_TMP.TITLE)
                .set(CAR_DEALS.PRICE, CAR_DEALS_TMP.PRICE)
                .set(CAR_DEALS.CURRENCY, CAR_DEALS_TMP.CURRENCY)
                .set(CAR_DEALS.MILEAGE, CAR_DEALS_TMP.MILEAGE)
                .set(CAR_DEALS.LOCATION, CAR_DEALS_TMP.LOCATION)
                .set(CAR_DEALS.YEAR, CAR_DEALS_TMP.YEAR)
                .set(CAR_DEALS.ACTIVE, DSL.inline(true))
                .execute();
    }

    @Override
    public void deleteInactiveOffers() {
        dsl.update(CAR_DEALS)
                .set(CAR_DEALS.ACTIVE, false)
                .where(CAR_DEALS.ACTIVE.isTrue())
                .andNotExists(dsl.selectOne()
                        .from(CAR_DEALS_TMP)
                        .where(CAR_DEALS_TMP.OFFER_URL.eq(CAR_DEALS.OFFER_URL))
                        .and(CAR_DEALS_TMP.SOURCE_ID.eq(CAR_DEALS.SOURCE_ID)))
                .execute();
    }

    @Override
    public boolean existsByOfferUrlAndSourceId(String offerUrl, Long sourceId) {
        return dsl.fetchExists(dsl.selectFrom(CAR_DEALS)
                .where(CAR_DEALS.OFFER_URL.eq(offerUrl))
                .and(CAR_DEALS.SOURCE_ID.eq(sourceId)));
    }

    @Override
    public List<CarDeal> findAll() {
        return dsl.selectFrom(CAR_DEALS).fetch().map(mapper::toDomainCarDeal);
    }
}
