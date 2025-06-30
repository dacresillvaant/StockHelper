package com.mateusz.springgpt.service;

import com.mailgun.api.v3.MailgunMessagesApi;
import com.mateusz.springgpt.utils.TestListener;
import jakarta.servlet.http.HttpServletRequest;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import static org.mockito.Mockito.*;

@Listeners(TestListener.class)
public class MailgunEmailServiceTest {

    @Mock
    private MailgunMessagesApi mailgunMessagesApi;

    @Mock
    private Environment environment;

    @Mock
    private HttpServletRequest request;

    private MailgunEmailService mailgunEmailService;

    private AutoCloseable closeable;

    @BeforeClass
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        mailgunEmailService = spy(new MailgunEmailService(mailgunMessagesApi, environment));

        ReflectionTestUtils.setField(mailgunEmailService, "mailgunFrom","sender@test.com");
        ReflectionTestUtils.setField(mailgunEmailService, "defaultMailReceiver","receiver@test.com");
    }

    @AfterClass
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testSendErrorAlertEmailWithRequestPresent() {
//      given
        Exception exception = new RuntimeException("Test exception");

        when(environment.getActiveProfiles()).thenReturn(new String[]{"test-profile"});
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getQueryString()).thenReturn("id=123");
        when(request.getHeader("User-Agent")).thenReturn("TestNG-Test-Agent");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

//      when
        mailgunEmailService.sendErrorAlertEmail(exception, request);

//      then - capture the arguments
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

        verify(mailgunEmailService, atMost(1)).sendEmailBasicTemplate(
                anyString(),
                subjectCaptor.capture(),
                bodyCaptor.capture()
        );

        String capturedSubject = subjectCaptor.getValue();
        String capturedBody = bodyCaptor.getValue();

        System.out.println("📧 Captured Subject:\n" + capturedSubject);
        System.out.println("📨 Captured Body:\n" + capturedBody);

//      expect
        SoftAssert softAssert = new SoftAssert();

        softAssert.assertTrue(capturedSubject.contains("[test-profile]"));
        softAssert.assertTrue(capturedBody.contains("Unhandled exception occurred"));
        softAssert.assertTrue(capturedBody.contains("Test exception"));
        softAssert.assertTrue(capturedBody.contains("/api/test"));
        softAssert.assertTrue(capturedBody.contains("id=123"));
        softAssert.assertTrue(capturedBody.contains("127.0.0.1"));

        softAssert.assertAll();
    }
}