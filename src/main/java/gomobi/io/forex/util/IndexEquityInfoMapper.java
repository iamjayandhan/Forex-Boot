package gomobi.io.forex.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gomobi.io.forex.model.IndexEquityInfo;

public class IndexEquityInfoMapper {

    public static Map<String, Object> enrich(IndexEquityInfo e) {
    Map<String, Object> enriched = new HashMap<>();

    // Basic fields
    enriched.put("symbol", e.getSymbol());
    enriched.put("identifier", e.getIdentifier());
    enriched.put("series", e.getSeries());
    enriched.put("open", e.getOpen());
    enriched.put("dayHigh", e.getDayHigh());
    enriched.put("dayLow", e.getDayLow());
    enriched.put("lastPrice", e.getLastPrice());
    enriched.put("previousClose", e.getPreviousClose());
    enriched.put("change", e.getChange());
    enriched.put("pChange", e.getPChange());
    enriched.put("pchange", e.getPChange());

    // Convert large numbers
    enriched.put("totalTradedVolume", e.getTotalTradedVolume());
    enriched.put("totalTradedValue", String.format("%.12E", e.getTotalTradedValue()));
    enriched.put("ffmc", String.format("%.12E", e.getFfmc()));
    enriched.put("lastUpdateTime", null);
    enriched.put("stockIndClosePrice", 0.0);

    // Charts and historical
    enriched.put("chartTodayPath", "https://nsearchives.nseindia.com/today/" + e.getIdentifier() + ".svg");
    enriched.put("chart30dPath", "https://nsearchives.nseindia.com/30d/" + e.getSymbol() + "-EQ.svg");
    enriched.put("chart365dPath", "https://nsearchives.nseindia.com/365d/" + e.getSymbol() + "-EQ.svg");
    enriched.put("date30dAgo", e.getDate30dAgo());
    enriched.put("perChange30d", e.getPerChange30d());
    enriched.put("date365dAgo", e.getDate365dAgo());
    enriched.put("perChange365d", e.getPerChange365d());

    enriched.put("yearHigh", e.getYearHigh());
    enriched.put("yearLow", e.getYearLow());
    enriched.put("nearWKH", e.getNearWKH());
    enriched.put("nearWKL", e.getNearWKL());
    enriched.put("priority", e.getPriority());

    // Null-safe meta
    Map<String, Object> meta = new HashMap<>();
    IndexEquityInfo.Meta m = e.getMeta();
    if (m != null) {
        meta.put("symbol", m.getSymbol());
        meta.put("companyName", m.getCompanyName());
        meta.put("industry", m.getIndustry());
        meta.put("activeSeries", m.getActiveSeries());
        meta.put("debtSeries", m.getDebtSeries());
        meta.put("tempSuspendedSeries", m.getTempSuspendedSeries());
        meta.put("isin", m.getIsin());
        meta.put("delisted", m.isDelisted());
        meta.put("suspended", m.isSuspended());
        meta.put("debtSec", m.isDebtSec());
        meta.put("casec", m.isCASec());
        meta.put("fnosec", m.isFNOSec());
        meta.put("etfsec", m.isETFSec());
        meta.put("slbsec", m.isSLBSec());
    } else {
        // Default meta to avoid null
        meta.put("symbol", e.getSymbol());
        meta.put("companyName", "N/A");
        meta.put("industry", "N/A");
        meta.put("activeSeries", List.of("EQ"));
        meta.put("debtSeries", List.of());
        meta.put("tempSuspendedSeries", List.of());
        meta.put("isin", "NA0000");
        meta.put("delisted", false);
        meta.put("suspended", false);
        meta.put("debtSec", false);
        meta.put("casec", false);
        meta.put("fnosec", false);
        meta.put("etfsec", false);
        meta.put("slbsec", false);
    }

    enriched.put("meta", meta);
    return enriched;
}
}
