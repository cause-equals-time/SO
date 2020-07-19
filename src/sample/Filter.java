package sample;

import com.jbetfairng.entities.MarketFilter;
import com.jbetfairng.entities.TimeRange;
import com.jbetfairng.enums.MarketType;
import org.joda.time.DateTime;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Filter {

    public static MarketFilter getOUFilter(Set<String> exchangeIds){

        MarketFilter filter = new MarketFilter();

        filter.setEventTypeIds(Collections.singleton("1"));
        //filter.setEventTypeIds(new HashSet<>(Collections.singleton("1")));

        filter.setEventIds(exchangeIds);
        //filter.setMarketTypeCodes (new HashSet<>(Set.of(MarketType.OVER_UNDER_25.toString())));


        filter.setMarketTypeCodes (new HashSet<>(Set.of(MarketType.FIRST_HALF_GOALS_05.toString(), MarketType.FIRST_HALF_GOALS_15.toString(), MarketType.FIRST_HALF_GOALS_25.toString(), MarketType.OVER_UNDER_05.toString(), MarketType.OVER_UNDER_15.toString(), MarketType.OVER_UNDER_25.toString(), MarketType.OVER_UNDER_35.toString(), MarketType.OVER_UNDER_45.toString(), MarketType.OVER_UNDER_55.toString(), MarketType.OVER_UNDER_65.toString(), MarketType.OVER_UNDER_75.toString(), MarketType.OVER_UNDER_85.toString())));

        /*TimeRange localTimeRange = new TimeRange();
        localTimeRange.setFrom(DateTime.now().toDate());
        localTimeRange.setTo(DateTime.now().plusDays(days).toDate());
        filter.setMarketStartTime(localTimeRange);*/

        return filter;

    }

    public static MarketFilter getSoccerFilter(){
        MarketFilter filter = new MarketFilter();

        filter.setEventTypeIds(Collections.singleton("1"));

        TimeRange localTimeRange = new TimeRange();
        localTimeRange.setFrom(DateTime.now().minusHours(4).toDate());
        localTimeRange.setTo(DateTime.now().plusDays(1).toDate());
        filter.setMarketStartTime(localTimeRange);

        return filter;
    }

}
