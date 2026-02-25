package com.mateusz.stockassistant.logic;

import com.mateusz.stockassistant.controller.twelvedata.dto.QuoteExternalDto;
import com.mateusz.stockassistant.controller.twelvedata.dto.model.FiftyTwoWeek;
import com.mateusz.stockassistant.entity.AlertConfigEntity;
import com.mateusz.stockassistant.service.AlertConfigService;
import com.mateusz.stockassistant.service.MailgunEmailService;
import com.mateusz.stockassistant.service.TwelveDataService;
import com.mateusz.stockassistant.tools.mail.MailTemplate;
import com.mateusz.stockassistant.utils.TestListener;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;

import static com.mateusz.stockassistant.controller.alertconfig.AlertType.LOW_PRICE_ALERT;
import static org.mockito.Mockito.*;

@Listeners(TestListener.class)
public class DynamicAlertTest {

    @Mock
    private TwelveDataService twelveDataService;
    @Mock
    private MailgunEmailService mailgunEmailService;
    @Mock
    private AlertConfigService alertConfigService;

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
    public void testLowPriceAlertTriggered() {
//      given
        AlertConfigEntity alertConfig = new AlertConfigEntity();
        alertConfig.setTicker(symbol);
        alertConfig.setPercentChangeThreshold(percentChange);
        List<AlertConfigEntity> alertConfigs = List.of(alertConfig);

        QuoteExternalDto quote = new QuoteExternalDto();
        quote.setClose("310"); // below threshold

        FiftyTwoWeek fiftyTwoWeek = new FiftyTwoWeek();
        fiftyTwoWeek.setHigh("366.5400");
        quote.setFiftyTwoWeek(fiftyTwoWeek);
        quote.setName("Visa Inc.");

//      when
        when(alertConfigService.getAlertConfigurationsByAlertType(LOW_PRICE_ALERT)).thenReturn(alertConfigs);
        when(twelveDataService.getQuote(symbol)).thenReturn((ResponseEntity.ok(quote)));

        dynamicAlert.lowPriceAlert();

//      then
        verify(mailgunEmailService).sendEmail(anyString(), any(MailTemplate.class));
    }

    @Test(testName = "Should not send email when latest close price was above threshold price")
    public void testLowPriceAlertOmitted() {
//      given
        AlertConfigEntity alertConfig = new AlertConfigEntity();
        alertConfig.setTicker(symbol);
        alertConfig.setPercentChangeThreshold(percentChange);
        List<AlertConfigEntity> alertConfigs = List.of(alertConfig);

        QuoteExternalDto quote = new QuoteExternalDto();
        quote.setClose("365"); // above threshold

        FiftyTwoWeek fiftyTwoWeek = new FiftyTwoWeek();
        fiftyTwoWeek.setHigh("366.5400");
        quote.setFiftyTwoWeek(fiftyTwoWeek);
        quote.setName("Visa Inc.");

//      when
        when(alertConfigService.getAlertConfigurationsByAlertType(LOW_PRICE_ALERT)).thenReturn(alertConfigs);
        when(twelveDataService.getQuote(symbol)).thenReturn((ResponseEntity.ok(quote)));

        dynamicAlert.lowPriceAlert();

//      then
        verify(mailgunEmailService, never()).sendEmail(anyString(), anyString(), anyString());
        verify(mailgunEmailService, never()).sendEmail(anyString(), any(MailTemplate.class));
    }
}