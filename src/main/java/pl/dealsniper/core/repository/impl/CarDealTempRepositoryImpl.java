package pl.dealsniper.core.repository.impl;

import static com.dealsniper.jooq.Tables.CAR_DEALS;
import static com.dealsniper.jooq.tables.CarDealsTmp.CAR_DEALS_TMP;

import java.util.List;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Query;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import pl.dealsniper.core.mapper.CarDealMapper;
import pl.dealsniper.core.model.CarDeal;
import pl.dealsniper.core.repository.CarDealTempRepository;

@Repository
@RequiredArgsConstructor
public class CarDealTempRepositoryImpl implements CarDealTempRepository<CarDeal> {

    private final DSLContext dsl;
    private final CarDealMapper mapper;

    @Override
    public void clear() {
        dsl.truncate(CAR_DEALS_TMP).execute();
    }

    @Override
    public void insertBatch(List<CarDeal> deals) {
        if (deals == null || deals.isEmpty()) {
            return;
        }

        List<Query> inserts = deals.stream()
                .map(deal -> dsl.insertInto(CAR_DEALS_TMP)
                        .set(mapper.toJooqCarDealRecord(deal))
                        .onDuplicateKeyIgnore())
                .collect(Collectors.toList());

        dsl.batch(inserts).execute();
    }

    @Override
    public List<CarDeal> getNewDeals() {
        return dsl.selectFrom(CAR_DEALS_TMP)
                .whereNotExists(dsl.selectOne()
                        .from(CAR_DEALS)
                        .where(CAR_DEALS.OFFER_URL.eq(CAR_DEALS_TMP.OFFER_URL))
                        .and(CAR_DEALS.SOURCE_ID.eq(CAR_DEALS_TMP.SOURCE_ID)))
                .fetch()
                .map(mapper::toDomainCarDeal);
    }
}
