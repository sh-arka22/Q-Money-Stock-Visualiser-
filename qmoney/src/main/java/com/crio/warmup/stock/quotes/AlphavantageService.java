package com.crio.warmup.stock.quotes;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.crio.warmup.stock.dto.AlphavantageCandle;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

import com.crio.warmup.stock.dto.AlphavantageDailyResponse;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.web.client.RestTemplate;
public class AlphavantageService implements StockQuotesService {
  public static final String token = "GD8DA62DO4E843Q0";
  private RestTemplate restTemplate;
  // @Override
  // public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
  //     throws JsonProcessingException {
  //   // TODO Auto-generated method stub
  //   return null;
  // }
  public AlphavantageService(RestTemplate restTemplate){
    this.restTemplate = restTemplate;
  }
  //********************************* added this method ****************************************************************************
  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }
  //********************************* added this method **************************************************************************/
  @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException, StockQuoteServiceException {
        ObjectMapper om = getObjectMapper();
          // System.out.println(restTemplate == null ? "null" : "arka");
        String result = restTemplate.getForObject(buildUri(symbol), String.class);
        try{
          // if(result == null || result.length() == 0) return Collections.emptyList();
          AlphavantageDailyResponse alphavantageDailyResponse = om.readValue(result, AlphavantageDailyResponse.class);
          Map<LocalDate, AlphavantageCandle> candles = alphavantageDailyResponse.getCandles();
          Map<LocalDate, AlphavantageCandle> filteredCandles = candles.entrySet() 
          .stream()
          .filter(x -> x.getKey().compareTo(from) >= 0 && x.getKey().compareTo(to) <= 0) // x - start time>=0          endTime-x >=0   x-endTinme<=0
          .sorted((a, b) ->  a.getKey().compareTo(b.getKey())) // assending ordere
          .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
          filteredCandles.forEach((k, v) -> v.setDate(k));
          List<Candle> answer = new ArrayList<Candle>(filteredCandles.values());
          // Collections.reverse(answer); //decending order
          return answer;
        }
        catch(Exception e){
          e.printStackTrace();
          // System.out.println("Error");
        }
    return Collections.emptyList();
  }
  protected String buildUri(String symbol) {
    String uriTemplate = "https://www.alphavantage.co/query?function="
        + "TIME_SERIES_DAILY&symbol=" + symbol + "&outputsize=full&apikey=" + token;
    return uriTemplate;
  }
  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement the StockQuoteService interface as per the contracts. Call Alphavantage service
  //  to fetch daily adjusted data for last 20 years.
  //  Refer to documentation here: https://www.alphavantage.co/documentation/
  //  --
  //  The implementation of this functions will be doing following tasks:
  //    1. Build the appropriate url to communicate with third-party.
  //       The url should consider startDate and endDate if it is supported by the provider.
  //    2. Perform third-party communication with the url prepared in step#1
  //    3. Map the response and convert the same to List<Candle>
  //    4. If the provider does not support startDate and endDate, then the implementation
  //       should also filter the dates based on startDate and endDate. Make sure that
  //       result contains the records for for startDate and endDate after filtering.
  //    5. Return a sorted List<Candle> sorted ascending based on Candle#getDate
  //  IMP: Do remember to write readable and maintainable code, There will be few functions like
  //    Checking if given date falls within provided date range, etc.
  //    Make sure that you write Unit tests for all such functions.
  //  Note:
  //  1. Make sure you use {RestTemplate#getForObject(URI, String)} else the test will fail.
  //  2. Run the tests using command below and make sure it passes:
  //    ./gradlew test --tests AlphavantageServiceTest
  //CHECKSTYLE:OFF
    //CHECKSTYLE:ON
  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  1. Write a method to create appropriate url to call Alphavantage service. The method should
  //     be using configurations provided in the {@link @application.properties}.
  //  2. Use this method in #getStockQuote.
}