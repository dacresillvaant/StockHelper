package com.mateusz.springgpt.service;

import com.mailgun.api.v3.MailgunMessagesApi;
import com.mateusz.springgpt.utils.TestListener;
import jakarta.servlet.http.HttpServletRequest;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
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

    @BeforeMethod
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        mailgunEmailService = spy(new MailgunEmailService(mailgunMessagesApi, environment));

        ReflectionTestUtils.setField(mailgunEmailService, "mailgunFrom","sender@test.com");
        ReflectionTestUtils.setField(mailgunEmailService, "defaultMailReceiver","receiver@test.com");
    }

    @AfterMethod
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

        softAssert.assertTrue(capturedSubject.contains("[test-profile] Unhandled exception alert"));
        softAssert.assertTrue(capturedBody.contains("Unhandled exception occurred in Stock Assistant app:"));

        softAssert.assertTrue(capturedBody.contains("Timestamp:"));
        softAssert.assertTrue(capturedBody.contains("Path: /api/test"));
        softAssert.assertTrue(capturedBody.contains("Query: id=123"));
        softAssert.assertTrue(capturedBody.contains("User-Agent: TestNG-Test-Agent"));
        softAssert.assertTrue(capturedBody.contains("Remote IP:"));

        softAssert.assertTrue(capturedBody.contains("Exception: java.lang.RuntimeException"));
        softAssert.assertTrue(capturedBody.contains("Message: Test exception"));
        softAssert.assertTrue(capturedBody.contains("Stack trace:"));

        softAssert.assertAll();
    }

    @Test
    public void testSendErrorAlertEmailWithoutRequestPresent() {
//      given
        Exception exception = new RuntimeException("Test exception");

        when(environment.getActiveProfiles()).thenReturn(new String[]{"test-profile"});

//      when
        mailgunEmailService.sendErrorAlertEmail(exception, null);

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

        softAssert.assertTrue(capturedSubject.contains("[test-profile] Unhandled exception alert"));
        softAssert.assertTrue(capturedBody.contains("Unhandled exception occurred in Stock Assistant app:"));

        softAssert.assertTrue(capturedBody.contains("Timestamp:"));
        softAssert.assertTrue(capturedBody.contains("Exception: java.lang.RuntimeException"));
        softAssert.assertTrue(capturedBody.contains("Message: Test exception"));
        softAssert.assertTrue(capturedBody.contains("Stack trace:"));

        softAssert.assertAll();
    }
}