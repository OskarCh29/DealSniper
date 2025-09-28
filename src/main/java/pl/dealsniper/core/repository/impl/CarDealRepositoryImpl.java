/* (C) 2025 */
package pl.dealsniper.core.repository.impl;

import static com.dealsniper.jooq.tables.CarDeals.CAR_DEALS;
import static com.dealsniper.jooq.tables.CarDealsTmp.CAR_DEALS_TMP;
import static com.dealsniper.jooq.tables.ScheduledTasks.SCHEDULED_TASKS;
import static com.dealsniper.jooq.tables.Sources.SOURCES;

import com.dealsniper.jooq.tables.records.CarDealsRecord;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import pl.dealsniper.core.dto.response.PageResponse;
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
    public boolean existsByUserIdAndSourceId(UUID userId, Long sourceId) {
        return dsl.fetchExists(dsl.selectFrom(CAR_DEALS)
                .asTable()
                .join(SOURCES)
                .on(CAR_DEALS.SOURCE_ID.eq(SOURCES.ID))
                .where(SOURCES.USER_ID.eq(userId).and(CAR_DEALS.SOURCE_ID.eq(sourceId))));
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
                .set(CAR_DEALS.TITLE, DSL.excluded(CAR_DEALS_TMP.TITLE))
                .set(CAR_DEALS.PRICE, DSL.excluded(CAR_DEALS_TMP.PRICE))
                .set(CAR_DEALS.CURRENCY, DSL.excluded(CAR_DEALS_TMP.CURRENCY))
                .set(CAR_DEALS.MILEAGE, DSL.excluded(CAR_DEALS_TMP.MILEAGE))
                .set(CAR_DEALS.LOCATION, DSL.excluded(CAR_DEALS_TMP.LOCATION))
                .set(CAR_DEALS.YEAR, DSL.excluded(CAR_DEALS_TMP.YEAR))
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
    public PageResponse<CarDeal> findAllByUserId(UUID userId, int page, int size) {
        int offset = page * size;

        List<CarDeal> offers = dsl
                .select(
                        CAR_DEALS.TITLE,
                        CAR_DEALS.PRICE,
                        CAR_DEALS.CURRENCY,
                        CAR_DEALS.LOCATION,
                        CAR_DEALS.MILEAGE,
                        CAR_DEALS.YEAR,
                        CAR_DEALS.OFFER_URL)
                .from(CAR_DEALS)
                .join(SOURCES)
                .on(CAR_DEALS.SOURCE_ID.eq(SOURCES.ID))
                .where(SOURCES.USER_ID.eq(userId))
                .and(CAR_DEALS.ACTIVE.isTrue())
                .limit(size)
                .offset(offset)
                .fetchInto(CarDealsRecord.class)
                .stream()
                .map(mapper::toDomainCarDeal)
                .toList();
        return PageResponse.<CarDeal>builder()
                .content(offers)
                .page(page)
                .size(size)
                .build();
    }

    @Override
    public PageResponse<CarDeal> findAllByUserIdAndTaskName(UUID userId, String taskName, int page, int size) {
        int offset = page * size;

        List<CarDeal> offers = dsl
                .select(
                        CAR_DEALS.TITLE,
                        CAR_DEALS.PRICE,
                        CAR_DEALS.CURRENCY,
                        CAR_DEALS.LOCATION,
                        CAR_DEALS.MILEAGE,
                        CAR_DEALS.YEAR,
                        CAR_DEALS.OFFER_URL)
                .from(SCHEDULED_TASKS)
                .join(SOURCES)
                .on(SCHEDULED_TASKS.SOURCE_ID.eq(SOURCES.ID))
                .and(SCHEDULED_TASKS.USER_ID.eq(SOURCES.USER_ID))
                .join(CAR_DEALS)
                .on(CAR_DEALS.SOURCE_ID.eq(SOURCES.ID))
                .where(SCHEDULED_TASKS.USER_ID.eq(userId))
                .and(SCHEDULED_TASKS.TASK_NAME.eq(taskName))
                .and(CAR_DEALS.ACTIVE.isTrue())
                .limit(size)
                .offset(offset)
                .fetchInto(CarDealsRecord.class)
                .stream()
                .map(mapper::toDomainCarDeal)
                .toList();
        return PageResponse.<CarDeal>builder()
                .content(offers)
                .page(page)
                .size(size)
                .build();
    }
}
