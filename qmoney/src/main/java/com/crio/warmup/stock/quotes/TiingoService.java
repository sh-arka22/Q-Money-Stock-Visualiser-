
package com.crio.warmup.stock.quotes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.web.client.RestTemplate;

public class TiingoService implements StockQuotesService {
  private RestTemplate restTemplate = new RestTemplate();

  protected TiingoService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }
//***************** Added a method *********************************************/
  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }
// ******************************** extra method added *******************************
  @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException, StockQuoteServiceException {
        try{
          ObjectMapper om = getObjectMapper();
          // System.out.println(restTemplate == null ? "null" : "arka");
          String result = restTemplate.getForObject(buildUri(symbol, from, to), String.class);
          // System.out.println(result);
          // List<TiingoCandle> collection = om.readValue(result, new TypeReference<ArrayList<TiingoCandle>>() {});
          List<TiingoCandle> collection = om.readValue(result, new TypeReference<ArrayList<TiingoCandle>>() {});
          return new ArrayList<Candle>(collection);
        }
        catch(Exception e){
          e.printStackTrace();
          System.out.println("Error");
        }
    return Collections.emptyList();
  }
  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    String uriTemplate = "https://api.tiingo.com/tiingo/daily/" + symbol + "/prices?" + "startDate="
        + startDate.toString() + "&endDate=" + endDate.toString() + "&token="
        + "19998507daee4c4c47beeea76e8e8b929f692130";
    return uriTemplate;
  }

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement getStockQuote method below that was also declared in the interface.

  // Note:
  // 1. You can move the code from PortfolioManagerImpl#getStockQuote inside newly created method.
  // 2. Run the tests using command below and make sure it passes.
  //    ./gradlew test --tests TiingoServiceTest


  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Write a method to create appropriate url to call the Tiingo API.

}
