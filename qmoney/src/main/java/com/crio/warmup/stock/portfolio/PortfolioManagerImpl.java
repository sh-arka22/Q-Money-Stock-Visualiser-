package com.crio.warmup.stock.portfolio;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.crio.warmup.stock.quotes.StockQuotesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.crio.warmup.stock.quotes.StockQuotesService;

public class PortfolioManagerImpl implements PortfolioManager {
  private RestTemplate restTemplate;
  private StockQuotesService stockQuotesService;
  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }
  protected PortfolioManagerImpl(StockQuotesService stockQuotesService) {
    this.stockQuotesService = stockQuotesService;
  }
  // protected PortfolioManagerImpl(RestTemplate restTemplate, StockQuotesService stockQuotesService) {
  //   this.restTemplate = restTemplate;
  //   this.stockQuotesService = stockQuotesService;
  // }


  //TODO: CRIO_TASK_MODULE_REFACTOR
  // 1. Now we want to convert our code into a module, so we will not call it from main anymore.
  //    Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  //    into #calculateAnnualizedReturn function here and ensure it follows the method signature.
  // 2. Logic to read Json file and convert them into Objects will not be required further as our
  //    clients will take care of it, going forward.

  // Note:
  // Make sure to exercise the tests inside PortfolioManagerTest using command below:
  // ./gradlew test --tests PortfolioManagerTest

  //CHECKSTYLE:OFF

  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades, LocalDate endDate) 
  throws JsonProcessingException, StockQuoteServiceException {
    //  ObjectMapper om = getObjectMapper();
  //  RestTemplate restTemplate = new RestTemplate();
    List<AnnualizedReturn> anreturns = new ArrayList<AnnualizedReturn>();
    for (PortfolioTrade symbol : portfolioTrades) {
      // String result =
      // this.restTemplate.getForObject("https://api.tiingo.com/tiingo/daily/"
      // + symbol.getSymbol() + "/prices?startDate=" + symbol.getPurchaseDate()
      // + "&endDate=" + endDate.toString() + "&token="
      // + "56ced5d79d2bfeedeb35aa03a11d6d68a33074f7", String.class);
      List<Candle> collection = getStockQuote(symbol.getSymbol(), symbol.getPurchaseDate(), endDate);
      // collection = om.readValue(result,
      // om.getTypeFactory().constructCollectionType(List.class, TiingoCandle.class));
      AnnualizedReturn x = calculateAnnualizedReturns(endDate, symbol, collection.get(0).getOpen(),
          collection.get(collection.size() - 1).getClose());
      anreturns.add(x);
      Collections.sort(anreturns, getComparator());
    }
    return anreturns;
    // tickerWithCloseValues.put(collection.get(collection.size() - 1).getClose(),
    // symbol.getSymbol());
    // }
  }
  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }
  
  // public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate, PortfolioTrade trade, Double buyPrice,
  //     Double sellPrice) {
  //   Double totalReturn = (sellPrice - buyPrice) / buyPrice;
  //   long daysBetween = ChronoUnit.DAYS.between(trade.getPurchaseDate(), endDate);
  //   double years = (double) daysBetween / 365;
  //   Double annualret = Math.pow(1 + totalReturn, 1 / years) - 1;
  //   return new AnnualizedReturn(trade.getSymbol(), annualret, totalReturn);
  // }
  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate, PortfolioTrade trade, Double buyPrice, Double sellPrice) {
    Double totalReturns = (sellPrice - buyPrice) / buyPrice;
    LocalDate purchase = trade.getPurchaseDate();
    Double noYears = purchase.until(endDate, ChronoUnit.DAYS) / 365.24;
    // System.out.println("hello");
    Double annualized_returns = Math.pow(1 + totalReturns, (1 / noYears)) - 1;
    return new AnnualizedReturn(trade.getSymbol(), annualized_returns, totalReturns);
  }

  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo third-party APIs to a separate function.
  //  Remember to fill out the buildUri function and use that.


  // public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to) throws JsonProcessingException {
  //   ObjectMapper om = new ObjectMapper();
  //   // System.out.println(restTemplate == null ? "null" : "arka");
  //   String result = restTemplate.getForObject(buildUri(symbol, from, to), String.class);
  //   List<TiingoCandle> collection = om.readValue(result, new TypeReference<ArrayList<TiingoCandle>>() {});
  //   return new ArrayList<Candle>(collection);
  // }
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to) throws  StockQuoteServiceException, JsonProcessingException {
    // try {
    //   try {
    //     return stockQuotesService.getStockQuote(symbol, from, to);
    //   } catch (JsonProcessingException e) {
    //     // TODO Auto-generated catch block
    //     e.printStackTrace();
    //   }
    // } 
    // catch (StockQuoteServiceException e) {
    //   // TODO Auto-generated catch block
    //   e.printStackTrace();
    // }
    return stockQuotesService.getStockQuote(symbol, from, to);
  }

  // protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
  //      String uriTemplate = "https:api.tiingo.com/tiingo/daily/$SYMBOL/prices?"
  //           + "startDate=$STARTDATE&endDate=$ENDDATE&token=$APIKEY";
  //     return uriTemplate;
  // }
  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    String uriTemplate = "https://api.tiingo.com/tiingo/daily/" + symbol + "/prices?" + "startDate="
        + startDate.toString() + "&endDate=" + endDate.toString() + "&token="
        + "19998507daee4c4c47beeea76e8e8b929f692130";
    return uriTemplate;
  }
  // public List<AnnualizedReturn> calculateAnnualizedReturnParallel( List<PortfolioTrade> portfolioTrades, LocalDate endDate, int numThreads)
  //     throws InterruptedException, StockQuoteServiceException {
    //   // TODO Auto-generated method stub
    //   return null;
    // }
    @Override
    public List<AnnualizedReturn> calculateAnnualizedReturnParallel(List<PortfolioTrade> portfolioTrades,
        LocalDate endDate, int numThreads) throws InterruptedException, StockQuoteServiceException {
      ExecutorService executor = Executors.newFixedThreadPool(numThreads);
      List<AnnualizedReturn> anreturns = new ArrayList<AnnualizedReturn>();
      List<Future<AnnualizedReturn>> list = new ArrayList<Future<AnnualizedReturn>>();
  
      for (PortfolioTrade symbol : portfolioTrades) {
        Callable<AnnualizedReturn> callable = new PortfolioCallable(symbol,endDate, this.stockQuotesService);
        Future<AnnualizedReturn> future = executor.submit(callable);
        list.add(future);
      }
  
      for (Future<AnnualizedReturn> fut : list) {
        try {
          anreturns.add(fut.get());
        } catch (ExecutionException e) {
          throw new StockQuoteServiceException("Error when calling the API");
        }
      }
      Collections.sort(anreturns, getComparator());
      executor.shutdown();
      return anreturns;
      //return null;
    }
  }




  // private Comparator<AnnualizedReturn> getComparator() {
  //   return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  // }



  // ¶TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Modify the function #getStockQuote and start delegating to calls to
  //  stockQuoteService provided via newly added constructor of the class.
  //  You also have a liberty to completely get rid of that function itself, however, make sure
  //  that you do not delete the #getStockQuote function.
  // private Comparator<AnnualizedReturn> getComparator() {
  //   return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  // }

