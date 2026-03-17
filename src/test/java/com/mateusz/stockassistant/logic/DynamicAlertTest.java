package com.mateusz.stockassistant.logic;

import com.mateusz.stockassistant.controller.yahoofinance.dto.YahooTruncatedChartResponseDto;
import com.mateusz.stockassistant.entity.AlertConfigEntity;
import com.mateusz.stockassistant.service.AlertConfigService;
import com.mateusz.stockassistant.service.MailgunEmailService;
import com.mateusz.stockassistant.service.YahooFinanceService;
import com.mateusz.stockassistant.tools.mail.MailTemplate;
import com.mateusz.stockassistant.utils.TestListener;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.List;

import static com.mateusz.stockassistant.controller.alertconfig.AlertType.LOW_PRICE_ALERT;
import static org.mockito.Mockito.*;

@Listeners(TestListener.class)
public class DynamicAlertTest {

    @Mock
    private YahooFinanceService yahooFinanceService;
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

        YahooTruncatedChartResponseDto.Meta meta = new YahooTruncatedChartResponseDto.Meta();
        meta.setLastPrice(BigDecimal.valueOf(310));
        meta.setFiftyTwoWeekHigh(BigDecimal.valueOf(366.5400));
        meta.setLongName("Visa Inc.");

        YahooTruncatedChartResponseDto dto = YahooTruncatedChartResponseDto.builder()
                .chart(new YahooTruncatedChartResponseDto.Chart())
                .build();

        dto.getChart().setResult(List.of(new YahooTruncatedChartResponseDto.Result()));
        dto.getChart().getResult().get(0).setMeta(meta);

//      when
        when(alertConfigService.getAlertConfigurationsByAlertType(LOW_PRICE_ALERT)).thenReturn(alertConfigs);
        when(yahooFinanceService.getSimplifiedData(symbol)).thenReturn(dto);

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

        YahooTruncatedChartResponseDto.Meta meta = new YahooTruncatedChartResponseDto.Meta();
        meta.setLastPrice(BigDecimal.valueOf(365));
        meta.setFiftyTwoWeekHigh(BigDecimal.valueOf(366.5400));
        meta.setLongName("Visa Inc.");

        YahooTruncatedChartResponseDto dto = YahooTruncatedChartResponseDto.builder()
                .chart(new YahooTruncatedChartResponseDto.Chart())
                .build();

        dto.getChart().setResult(List.of(new YahooTruncatedChartResponseDto.Result()));
        dto.getChart().getResult().get(0).setMeta(meta);


//      when
        when(alertConfigService.getAlertConfigurationsByAlertType(LOW_PRICE_ALERT)).thenReturn(alertConfigs);
        when(yahooFinanceService.getSimplifiedData(symbol)).thenReturn(dto);

        dynamicAlert.lowPriceAlert();

//      then
        verify(mailgunEmailService, never()).sendEmail(anyString(), anyString(), anyString());
        verify(mailgunEmailService, never()).sendEmail(anyString(), any(MailTemplate.class));
    }
}