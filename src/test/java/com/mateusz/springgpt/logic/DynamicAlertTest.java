package com.mateusz.springgpt.logic;

import com.mateusz.springgpt.controller.dto.QuoteExternalDto;
import com.mateusz.springgpt.controller.dto.model.FiftyTwoWeek;
import com.mateusz.springgpt.service.MailgunEmailService;
import com.mateusz.springgpt.service.TwelveDataService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;

public class DynamicAlertTest {

    @Mock
    private TwelveDataService twelveDataService;
    @Mock
    private MailgunEmailService mailgunEmailService;
    @InjectMocks
    private DynamicAlert dynamicAlert;

    private AutoCloseable closeable;
    private final String symbol = "V";
    private final int percentChange = 15;

    @BeforeClass
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(dynamicAlert, "mailReceiver", "receiver@test.com");
    }

    @AfterClass
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test(testName = "Should send email notification when latest close price fallen below threshold price")
    public void testPriceChangeAlertTriggered() {
//      given
        QuoteExternalDto quote = new QuoteExternalDto();
        quote.setClose("310"); // below threshold

        FiftyTwoWeek fiftyTwoWeek = new FiftyTwoWeek();
        fiftyTwoWeek.setHigh("366.5400");
        quote.setFiftyTwoWeek(fiftyTwoWeek);
        quote.setName("Visa Inc.");

//      when
        when(twelveDataService.getQuote(symbol))
                .thenReturn(Mono.just(ResponseEntity.ok(quote)));

        dynamicAlert.priceChangeAlert(symbol, percentChange);

//      then
        verify(mailgunEmailService).sendEmail(anyString(), anyString(), anyString());
    }

    @Test(testName = "Should not send email when latest close price was above threshold price")
    public void testPriceChangeAlertOmitted() {
//      given
        QuoteExternalDto quote = new QuoteExternalDto();
        quote.setClose("365"); // above threshold

        FiftyTwoWeek fiftyTwoWeek = new FiftyTwoWeek();
        fiftyTwoWeek.setHigh("366.5400");
        quote.setFiftyTwoWeek(fiftyTwoWeek);
        quote.setName("Visa Inc.");

//      when
        when(twelveDataService.getQuote(symbol))
                .thenReturn(Mono.just(ResponseEntity.ok(quote)));

        dynamicAlert.priceChangeAlert(symbol, percentChange);

//      then
        verify(mailgunEmailService, never()).sendEmail(anyString(), anyString(), anyString());
    }
}