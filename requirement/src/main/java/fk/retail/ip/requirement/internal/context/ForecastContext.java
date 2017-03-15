package fk.retail.ip.requirement.internal.context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import fk.retail.ip.requirement.internal.Constants;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ForecastContext {

    private final ObjectMapper objectMapper;
    private Table<String, String, List<Double>> fsnWarehouseForecastTable = HashBasedTable.create();

    public ForecastContext(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<Double> addForecast(String fsn, String warehouse, String forecastString) {
        List<Double> forecastValues = Lists.newArrayList();
        TypeReference<List<Double>> typeReference = new TypeReference<List<Double>>(){};
        try {
            forecastValues = objectMapper.readValue(forecastString, typeReference);
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        }
        return fsnWarehouseForecastTable.put(fsn, warehouse, forecastValues);
    }

    public Set<String> getFsns() {
        return fsnWarehouseForecastTable.rowKeySet();
    }


    public List<Double> getForecast(String fsn, String warehouse) {
        List<Double> value = fsnWarehouseForecastTable.get(fsn, warehouse);
        if (value == null) {
            value = Stream.generate(() -> Constants.DEFAULT_FORECAST).limit(Constants.WEEKS_OF_FORECAST).collect(Collectors.toList());
        }
        return value;
    }

    public List<Double> getForecast(String fsn) {
        List<Double> allIndiaForecast = Stream.generate(() -> Constants.DEFAULT_FORECAST).limit(Constants.WEEKS_OF_FORECAST).collect(Collectors.toList());
        Map<String, List<Double>> forecasts = fsnWarehouseForecastTable.row(fsn);
        forecasts.keySet().forEach(warehouse -> {
            List<Double> forecast = forecasts.get(warehouse);
            for (int i = 0; i < Constants.WEEKS_OF_FORECAST; i++) {
                double sum = allIndiaForecast.get(i) + forecast.get(i);
                allIndiaForecast.set(i, sum);
            }
        });
        return allIndiaForecast;
    }

    public Set<String> getWarehouses(String fsn) {
        Set<String> value = fsnWarehouseForecastTable.row(fsn).keySet();
        return value;
    }

    public String getForecastAsString(String fsn, String warehouse) {
        try {
            return objectMapper.writeValueAsString(fsnWarehouseForecastTable.get(fsn, warehouse));
        } catch (JsonProcessingException e) {
            log.warn(e.getMessage(), e);
        }
        return null;
    }

}
