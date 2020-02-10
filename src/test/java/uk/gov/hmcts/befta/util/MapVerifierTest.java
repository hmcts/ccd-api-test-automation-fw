package uk.gov.hmcts.befta.util;

import static uk.gov.hmcts.befta.util.ExpectedValuePlaceholder.ANYTHING_PRESENT;
import static uk.gov.hmcts.befta.util.ExpectedValuePlaceholder.ANY_DATE_NOT_NULLABLE;
import static uk.gov.hmcts.befta.util.ExpectedValuePlaceholder.ANY_DATE_NULLABLE;
import static uk.gov.hmcts.befta.util.ExpectedValuePlaceholder.ANY_FLOATING_NOT_NULLABLE;
import static uk.gov.hmcts.befta.util.ExpectedValuePlaceholder.ANY_FLOATING_NULLABLE;
import static uk.gov.hmcts.befta.util.ExpectedValuePlaceholder.ANY_INTEGER_NOT_NULLABLE;
import static uk.gov.hmcts.befta.util.ExpectedValuePlaceholder.ANY_INTEGER_NULLABLE;
import static uk.gov.hmcts.befta.util.ExpectedValuePlaceholder.ANY_NOT_NULLABLE;
import static uk.gov.hmcts.befta.util.ExpectedValuePlaceholder.ANY_NULLABLE;
import static uk.gov.hmcts.befta.util.ExpectedValuePlaceholder.ANY_NUMBER_NOT_NULLABLE;
import static uk.gov.hmcts.befta.util.ExpectedValuePlaceholder.ANY_NUMBER_NULLABLE;
import static uk.gov.hmcts.befta.util.ExpectedValuePlaceholder.ANY_OBJECT_NOT_NULLABLE;
import static uk.gov.hmcts.befta.util.ExpectedValuePlaceholder.ANY_OBJECT_NULLABLE;
import static uk.gov.hmcts.befta.util.ExpectedValuePlaceholder.ANY_STRING_NOT_NULLABLE;
import static uk.gov.hmcts.befta.util.ExpectedValuePlaceholder.ANY_STRING_NULLABLE;
import static uk.gov.hmcts.befta.util.ExpectedValuePlaceholder.ANY_TIMESTAMP_NOT_NULLABLE;
import static uk.gov.hmcts.befta.util.ExpectedValuePlaceholder.ANY_TIMESTAMP_NULLABLE;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import uk.gov.hmcts.befta.data.HttpTestDataSource;
import uk.gov.hmcts.befta.data.JsonStoreHttpTestDataSource;


public class MapVerifierTest {

    private static final String[] TEST_DATA_RESOURCE_PACKAGES = { "framework-test-data" };
    private static final HttpTestDataSource TEST_DATA_RESOURCE = new JsonStoreHttpTestDataSource(
            TEST_DATA_RESOURCE_PACKAGES);

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForNegativeMaxDepth() {
        new MapVerifier("", -1).verifyMap(null, null);
    }

    @Test
    public void shouldVerifyNullVsNullCase() {
        MapVerificationResult result = new MapVerifier("").verifyMap(null, null);
        Assert.assertTrue(result.isVerified());
        result = new MapVerifier("", 1).verifyMap(null, null);
        Assert.assertTrue(result.isVerified());
        result = new MapVerifier("", 2).verifyMap(null, null);
        Assert.assertTrue(result.isVerified());
        result = new MapVerifier("", 1000).verifyMap(null, null);
        Assert.assertTrue(result.isVerified());
    }

    @Test
    public void shouldNotVerifyNullVsNonNullCase() {
        MapVerificationResult result = new MapVerifier("", 999).verifyMap(new HashMap<String, Object>(), null);
        Assert.assertArrayEquals(new Object[] { "Map is expected to be non-null, but is actually null." },
                result.getAllIssues().toArray());
    }

    @Test
    public void shouldNotVerifyNonNullVsNullCase() {
        MapVerificationResult result = new MapVerifier("", 999).verifyMap(null, new HashMap<String, Object>());
        Assert.assertArrayEquals(new Object[] { "Map is expected to be null, but is actually not." },
                result.getAllIssues().toArray());
    }

    @Test
    public void shouldVerifyEmptyVsEmptyCase() {
        MapVerificationResult result = new MapVerifier("", 0).verifyMap(new HashMap<>(), new HashMap<>());
        Assert.assertEquals(0, result.getAllIssues().size());
        result = new MapVerifier("", 0).verifyMap(new ConcurrentHashMap<>(), new LinkedHashMap<>());
        Assert.assertTrue(result.isVerified());
    }

    @Test
    public void shouldVerifySimpleMapObjectWithItself() {
        Map<String, Object> expected = new HashMap<>();
        expected.put("key", "value");
        MapVerificationResult result = new MapVerifier("", 0).verifyMap(expected, expected);
        Assert.assertTrue(result.isVerified());
    }

    @Test
    public void shouldVerifySimpleMapsOfSameContentWithoutWildcards() {
        Map<String, Object> expected = new HashMap<>();
        Map<String, Object> actual = new ConcurrentHashMap<>();

        expected.put("key", "value");
        actual.put("key", "value");
        MapVerificationResult result = new MapVerifier("", 0).verifyMap(expected, actual);
        Assert.assertEquals(0, result.getAllIssues().size());

        expected.put("key2", "value2");
        actual.put("key2", "value2");
        result = new MapVerifier("", 0).verifyMap(expected, actual);
        Assert.assertEquals(0, result.getAllIssues().size());

        expected.put("key3", 333.333);
        actual.put("key3", 333.333);
        result = new MapVerifier("", 0).verifyMap(expected, actual);
        Assert.assertTrue(result.isVerified());
    }

    @Test
    public void shouldVerifySimpleMapOfAcceptableContentWithWildcards() {
        Map<String, Object> expected = new HashMap<>();
        Map<String, Object> expectedBody = new HashMap<>();

        expected.put("responseCode", 400);
        expected.put("body", expectedBody);
        expectedBody.put("exception", "uk.gov.hmcts.ccd.endpoint.exceptions.BadRequestException");
        expectedBody.put("timestamp", ANY_TIMESTAMP_NOT_NULLABLE.getValue());
        expectedBody.put("timestamp2", ANY_TIMESTAMP_NULLABLE.getValue());
        expectedBody.put("status", 400);
        expectedBody.put("error", "Bad Request");
        expectedBody.put("message", "Unknown sort direction: someInvalidSortDirection");
        expectedBody.put("path", ANYTHING_PRESENT.getValue());
        expectedBody.put("details", null);
        expectedBody.put("callbackErrors", null);
        expectedBody.put("callbackWarnings", null);

        Map<String, Object> actual = new ConcurrentHashMap<>();
        Map<String, Object> actualBody = new HashMap<>();

        actual.put("responseCode", 400);
        actual.put("body", actualBody);
        actualBody.put("exception", "uk.gov.hmcts.ccd.endpoint.exceptions.BadRequestException");
        actualBody.put("timestamp", "2019-11-13T14:02:43.431");
        actualBody.put("timestamp2", null);
        actualBody.put("status", 400);
        actualBody.put("error", "Bad Request");
        actualBody.put("message", "Unknown sort direction: someInvalidSortDirection");
        actualBody.put("path", "/caseworkers/bfb6eeaa-cbcd-466d-aafa-07fe99e7462b/jurisdictions/AUTOTEST1"
            + "/case-types/AAT/cases/pagination_metadata");
        actualBody.put("details", null);
        actualBody.put("callbackErrors", null);
        actualBody.put("callbackWarnings", null);

        MapVerificationResult result = new MapVerifier("actualResponse", 0).verifyMap(expected, actual);
        Assert.assertEquals(0, result.getAllIssues().size());
        Assert.assertTrue(result.isVerified());
    }

    @Test
    public void shouldVerifyContentWithWildcards() {
        Map<String, Object> expected = new HashMap<>();
        Map<String, Object> expectedBody = new HashMap<>();

        expected.put("responseCode", ANY_INTEGER_NOT_NULLABLE.getValue());
        expected.put("body", expectedBody);
        expectedBody.put("exception", "uk.gov.hmcts.ccd.endpoint.exceptions.BadRequestException");
        expectedBody.put("timestamp", ANY_TIMESTAMP_NOT_NULLABLE.getValue());
        expectedBody.put("status", ANY_INTEGER_NOT_NULLABLE.getValue());
        expectedBody.put("error", "Bad Request");
        expectedBody.put("message", ANY_STRING_NOT_NULLABLE.getValue());
        expectedBody.put("message2", ANY_STRING_NULLABLE.getValue());
        expectedBody.put("path", ANYTHING_PRESENT.getValue());
        expectedBody.put("details", ANY_OBJECT_NULLABLE.getValue());
        expectedBody.put("details2", ANY_OBJECT_NOT_NULLABLE.getValue());
        expectedBody.put("callbackErrors", ANY_OBJECT_NULLABLE.getValue());
        expectedBody.put("callbackWarnings", null);

        Map<String, Object> actual = new ConcurrentHashMap<>();
        Map<String, Object> actualBody = new HashMap<>();

        actual.put("responseCode", 400);
        actual.put("body", actualBody);
        actualBody.put("exception", "uk.gov.hmcts.ccd.endpoint.exceptions.BadRequestException");
        actualBody.put("timestamp", "2019-11-13T14:02:43.431");
        actualBody.put("status", 400);
        actualBody.put("error", "Bad Request");
        actualBody.put("message", "Unknown sort direction: someInvalidSortDirection");
        actualBody.put("message2", null);

        actualBody.put("path", "/caseworkers/bfb6eeaa-cbcd-466d-aafa-07fe99e7462b/jurisdictions/AUTOTEST1"
                + "/case-types/AAT/cases/pagination_metadata");
        actualBody.put("details", null);
        actualBody.put("details2", "2019-11-13");
        actualBody.put("callbackErrors", null);
        actualBody.put("callbackWarnings", null);

        MapVerificationResult result = new MapVerifier("actualResponse", 0).verifyMap(expected, actual);
        Assert.assertEquals(0, result.getAllIssues().size());
        Assert.assertTrue(result.isVerified());
    }

    @Test
    public void shouldVerifyContentWithWildcardAnythingIfExists() {
        Map<String, Object> expected = new HashMap<>();
        Map<String, Object> expectedBody = new HashMap<>();

        expected.put("responseCode", ANY_INTEGER_NOT_NULLABLE.getValue());
        expected.put("body", expectedBody);
        expectedBody.put("exception", "uk.gov.hmcts.ccd.endpoint.exceptions.BadRequestException");
        expectedBody.put("timestamp", ANY_TIMESTAMP_NOT_NULLABLE.getValue());
        expectedBody.put("status", ANY_INTEGER_NOT_NULLABLE.getValue());
        expectedBody.put("error", "Bad Request");
        expectedBody.put("message", ANY_STRING_NOT_NULLABLE.getValue());
        expectedBody.put("path", ANYTHING_PRESENT.getValue());
        expectedBody.put("details", ANY_OBJECT_NULLABLE.getValue());
        expectedBody.put("callbackErrors", ANY_NULLABLE.getValue());
        expectedBody.put("callbackWarnings", ANY_NULLABLE.getValue());

        Map<String, Object> actual = new ConcurrentHashMap<>();
        Map<String, Object> actualBody = new HashMap<>();

        actual.put("responseCode", 400);
        actual.put("body", actualBody);
        actualBody.put("exception", "uk.gov.hmcts.ccd.endpoint.exceptions.BadRequestException");
        actualBody.put("timestamp", "2019-11-13T14:02:43.431");
        actualBody.put("status", 400);
        actualBody.put("error", "Bad Request");
        actualBody.put("message", "Unknown sort direction: someInvalidSortDirection");
        actualBody.put("path", "/caseworkers/bfb6eeaa-cbcd-466d-aafa-07fe99e7462b/jurisdictions/AUTOTEST1"
                + "/case-types/AAT/cases/pagination_metadata");
        actualBody.put("details", null);
        actualBody.put("callbackErrors", null);
        actualBody.put("callbackWarnings", "Test Warnings");

        MapVerificationResult result = new MapVerifier("actualResponse", 0).verifyMap(expected, actual);
        Assert.assertEquals(0, result.getAllIssues().size());
        Assert.assertTrue(result.isVerified());
    }

    @Test
    public void shouldVerifyContentWithWildcardAnythingNullable() {
        Map<String, Object> expected = new HashMap<>();
        Map<String, Object> expectedBody = new HashMap<>();

        expected.put("responseCode", ANY_INTEGER_NOT_NULLABLE.getValue());
        expected.put("body", expectedBody);
        expectedBody.put("exception", "uk.gov.hmcts.ccd.endpoint.exceptions.BadRequestException");
        expectedBody.put("timestamp", ANY_TIMESTAMP_NOT_NULLABLE.getValue());
        expectedBody.put("status", ANY_INTEGER_NOT_NULLABLE.getValue());
        expectedBody.put("error", "Bad Request");
        expectedBody.put("message", ANY_STRING_NOT_NULLABLE.getValue());
        expectedBody.put("path", ANYTHING_PRESENT.getValue());
        expectedBody.put("details", ANY_OBJECT_NULLABLE.getValue());
        expectedBody.put("callbackErrors", ANY_NULLABLE.getValue());
        expectedBody.put("callbackErrors2", ANY_NOT_NULLABLE.getValue());
        expectedBody.put("callbackWarnings", ANY_NULLABLE.getValue());
        expectedBody.put("serialNumber", ANY_INTEGER_NULLABLE.getValue());
        expectedBody.put("serialNumber2", ANY_NUMBER_NOT_NULLABLE.getValue());
        expectedBody.put("serialNumber3", ANY_NUMBER_NULLABLE.getValue());

        Map<String, Object> actual = new ConcurrentHashMap<>();
        Map<String, Object> actualBody = new HashMap<>();

        actual.put("responseCode", 400);
        actual.put("body", actualBody);
        actualBody.put("exception", "uk.gov.hmcts.ccd.endpoint.exceptions.BadRequestException");
        actualBody.put("timestamp", "2019-11-13T14:02:43.431");
        actualBody.put("status", 400);
        actualBody.put("error", "Bad Request");
        actualBody.put("message", "Unknown sort direction: someInvalidSortDirection");
        actualBody.put("path", "/caseworkers/bfb6eeaa-cbcd-466d-aafa-07fe99e7462b/jurisdictions/AUTOTEST1"
                + "/case-types/AAT/cases/pagination_metadata");
        actualBody.put("details", null);
        actualBody.put("callbackErrors", null);
        actualBody.put("callbackErrors2", new Object());
        actualBody.put("callbackWarnings", "Test Warnings");
        actualBody.put("serialNumber", null);
        actualBody.put("serialNumber2", 600);
        actualBody.put("serialNumber3", null);

        MapVerificationResult result = new MapVerifier("actualResponse", 0).verifyMap(expected, actual);
        Assert.assertEquals(0, result.getAllIssues().size());
        Assert.assertTrue(result.isVerified());
    }

    @Test
    public void shouldVerifyContentWithWildcardAnyInteger() {
        Map<String, Object> expected = new HashMap<>();

        expected.put("responseCode", ANY_INTEGER_NOT_NULLABLE.getValue());
        expected.put("serialNumber", ANY_INTEGER_NULLABLE.getValue());

        Map<String, Object> actual = new HashMap<>();

        actual.put("responseCode", 400);
        actual.put("serialNumber", null);

        MapVerificationResult result = new MapVerifier("actualResponse", 0).verifyMap(expected, actual);
        Assert.assertEquals(0, result.getAllIssues().size());
        Assert.assertTrue(result.isVerified());
    }

    @Test
    public void shouldFailContentWithWildcardAnyIntegerNotNull() {
        Map<String, Object> expected = new HashMap<>();

        expected.put("responseCode", ANY_INTEGER_NOT_NULLABLE.getValue());
        expected.put("serialNumber", ANY_INTEGER_NULLABLE.getValue());
        expected.put("serialNumber2", ANY_INTEGER_NULLABLE.getValue());

        Map<String, Object> actual = new HashMap<>();

        actual.put("responseCode", null);
        actual.put("serialNumber", null);
        actual.put("serialNumber2", 400);

        MapVerificationResult result = new MapVerifier("actualResponse", 0).verifyMap(expected, actual);
        Assert.assertEquals(1, result.getAllIssues().size());
        Assert.assertFalse(result.isVerified());
    }


    @Test
    public void shouldVerifyContentWithWildcardAnyTimestamp() {
        Map<String, Object> expected = new HashMap<>();

        expected.put("timestamp", ANY_TIMESTAMP_NOT_NULLABLE.getValue());
        expected.put("timestamp2", ANY_TIMESTAMP_NULLABLE.getValue());

        Map<String, Object> actual = new HashMap<>();

        actual.put("timestamp", "2019-11-13T14:02:43.431");
        actual.put("timestamp2", "2019-11-13T14:02:43.431");

        MapVerificationResult result = new MapVerifier("actualResponse", 0).verifyMap(expected, actual);
        Assert.assertEquals(0, result.getAllIssues().size());
        Assert.assertTrue(result.isVerified());
    }

    @Test
    public void shouldFailContentWithWildcardAnyTimestamp() {
        Map<String, Object> expected = new HashMap<>();

        expected.put("timestamp", ANY_TIMESTAMP_NOT_NULLABLE.getValue());
        expected.put("timestamp2", ANY_TIMESTAMP_NULLABLE.getValue());
        expected.put("timestamp3", ANY_TIMESTAMP_NULLABLE.getValue());

        Map<String, Object> actual = new HashMap<>();

        actual.put("timestamp", null);
        actual.put("timestamp2", "2019-11-13T14:02:43.431");
        actual.put("timestamp3", null);

        MapVerificationResult result = new MapVerifier("actualResponse", 0).verifyMap(expected, actual);
        Assert.assertEquals(1, result.getAllIssues().size());
        Assert.assertFalse(result.isVerified());
    }


    @Test
    public void shouldVerifyContentWithWildcardAnyString() {
        Map<String, Object> expected = new HashMap<>();

        expected.put("message", ANY_STRING_NOT_NULLABLE.getValue());
        expected.put("message2", ANY_STRING_NULLABLE.getValue());
        expected.put("message3", ANY_STRING_NULLABLE.getValue());

        Map<String, Object> actual = new HashMap<>();

        actual.put("message", "Unknown sort direction: someInvalidSortDirection");
        actual.put("message2", null);
        actual.put("message3", "Unknown sort direction: someInvalidSortDirection");

        MapVerificationResult result = new MapVerifier("actualResponse", 0).verifyMap(expected, actual);
        Assert.assertEquals(0, result.getAllIssues().size());
        Assert.assertTrue(result.isVerified());
    }

    @Test
    public void shouldFailContentWithWildcardAnyString() {
        Map<String, Object> expected = new HashMap<>();

        expected.put("message", ANY_STRING_NOT_NULLABLE.getValue());
        expected.put("message2", ANY_STRING_NULLABLE.getValue());
        expected.put("message3", ANY_STRING_NULLABLE.getValue());

        Map<String, Object> actual = new HashMap<>();

        actual.put("message", null);
        actual.put("message2", null);
        actual.put("message3", "Unknown sort direction: someInvalidSortDirection");

        MapVerificationResult result = new MapVerifier("actualResponse", 0).verifyMap(expected, actual);
        Assert.assertEquals(1, result.getAllIssues().size());
        Assert.assertFalse(result.isVerified());
    }

    @Test
    public void shouldVerifyContentWithWildcardAnyObject() {
        Map<String, Object> expected = new HashMap<>();
        expected.put("details", ANY_OBJECT_NULLABLE.getValue());
        expected.put("details1", ANY_OBJECT_NULLABLE.getValue());
        expected.put("details2", ANY_OBJECT_NOT_NULLABLE.getValue());

        Map<String, Object> actual = new HashMap<>();
        actual.put("details", null);
        actual.put("details1", 400);
        actual.put("details2", 400);

        MapVerificationResult result = new MapVerifier("actualResponse", 0).verifyMap(expected, actual);
        Assert.assertEquals(0, result.getAllIssues().size());
        Assert.assertTrue(result.isVerified());
    }

    @Test
    public void shouldFailContentWithWildcardAnyObject() {
        Map<String, Object> expected = new HashMap<>();
        expected.put("details", ANY_OBJECT_NULLABLE.getValue());
        expected.put("details1", ANY_OBJECT_NULLABLE.getValue());
        expected.put("details2", ANY_OBJECT_NOT_NULLABLE.getValue());

        Map<String, Object> actual = new HashMap<>();
        actual.put("details", null);
        actual.put("details1", 400);
        actual.put("details2", null);

        MapVerificationResult result = new MapVerifier("actualResponse", 0).verifyMap(expected, actual);
        Assert.assertEquals(1, result.getAllIssues().size());
        Assert.assertFalse(result.isVerified());
    }

    @Test
    public void shouldVerifyContentWithWildcardAnyNumber() {
        Map<String, Object> expected = new HashMap<>();

        expected.put("serialNumber2", ANY_NUMBER_NOT_NULLABLE.getValue());
        expected.put("serialNumber3", ANY_NUMBER_NULLABLE.getValue());
        expected.put("serialNumber4", ANY_NUMBER_NULLABLE.getValue());

        Map<String, Object> actual = new HashMap<>();
        actual.put("serialNumber2", 600);
        actual.put("serialNumber3", null);
        actual.put("serialNumber4", 700.50);

        MapVerificationResult result = new MapVerifier("actualResponse", 0).verifyMap(expected, actual);
        Assert.assertEquals(0, result.getAllIssues().size());
        Assert.assertTrue(result.isVerified());
    }

    @Test
    public void shouldFailContentWithWildcardAnyNumber() {
        Map<String, Object> expected = new HashMap<>();

        expected.put("serialNumber2", ANY_NUMBER_NOT_NULLABLE.getValue());
        expected.put("serialNumber3", ANY_NUMBER_NULLABLE.getValue());
        expected.put("serialNumber4", ANY_NUMBER_NULLABLE.getValue());

        Map<String, Object> actual = new HashMap<>();
        actual.put("serialNumber2", null);
        actual.put("serialNumber3", null);
        actual.put("serialNumber4", 700.50);

        MapVerificationResult result = new MapVerifier("actualResponse", 0).verifyMap(expected, actual);
        Assert.assertEquals(1, result.getAllIssues().size());
        Assert.assertFalse(result.isVerified());
    }

    @Test
    public void shouldVerifyContentWithWildcardAnyDate() {
        Map<String, Object> expected = new HashMap<>();

        expected.put("date", ANY_DATE_NOT_NULLABLE.getValue());
        expected.put("date1", ANY_DATE_NULLABLE.getValue());
        expected.put("date2", ANY_DATE_NULLABLE.getValue());

        Map<String, Object> actual = new HashMap<>();
        actual.put("date", "2019-11-23");
        actual.put("date1", null);
        actual.put("date2", "2019-11-24");

        MapVerificationResult result = new MapVerifier("actualResponse", 0).verifyMap(expected, actual);
        Assert.assertEquals(0, result.getAllIssues().size());
        Assert.assertTrue(result.isVerified());
    }

    @Test
    public void shouldFailContentWithWildcardAnyDate() {
        Map<String, Object> expected = new HashMap<>();

        expected.put("date", ANY_DATE_NOT_NULLABLE.getValue());
        expected.put("date1", ANY_DATE_NULLABLE.getValue());
        expected.put("date2", ANY_DATE_NULLABLE.getValue());

        Map<String, Object> actual = new HashMap<>();
        actual.put("date", null);
        actual.put("date1", null);
        actual.put("date2", "2019-11-24");

        MapVerificationResult result = new MapVerifier("actualResponse", 0).verifyMap(expected, actual);
        Assert.assertEquals(1, result.getAllIssues().size());
        Assert.assertFalse(result.isVerified());
    }

    @Test
    public void shouldFailContentWithWildcardAnyNullale() {
        Map<String, Object> expected = new HashMap<>();

        expected.put("date", ANY_NOT_NULLABLE.getValue());
        expected.put("date1", ANY_NULLABLE.getValue());
        expected.put("date2", ANY_NULLABLE.getValue());

        Map<String, Object> actual = new HashMap<>();
        actual.put("date", null);
        actual.put("date1", null);
        actual.put("date2", "2019-11-24");

        MapVerificationResult result = new MapVerifier("actualResponse", 0).verifyMap(expected, actual);
        Assert.assertEquals(1, result.getAllIssues().size());
        Assert.assertFalse(result.isVerified());
    }

    @Test
    public void shouldVerifyContentWithWildcardAnyNullale() {
        Map<String, Object> expected = new HashMap<>();

        expected.put("date", ANY_NOT_NULLABLE.getValue());
        expected.put("date1", ANY_NULLABLE.getValue());
        expected.put("date2", ANY_NULLABLE.getValue());

        Map<String, Object> actual = new HashMap<>();
        actual.put("date", new Object());
        actual.put("date1", null);
        actual.put("date2", "2019-11-24");

        MapVerificationResult result = new MapVerifier("actualResponse", 0).verifyMap(expected, actual);
        Assert.assertEquals(0, result.getAllIssues().size());
        Assert.assertTrue(result.isVerified());
    }


    @Test
    public void shouldVerifyContentWithWildcardAnyFloating() {
        Map<String, Object> expected = new HashMap<>();
        Map<String, Object> expectedBody = new HashMap<>();

        expected.put("responseCode", ANY_INTEGER_NOT_NULLABLE.getValue());
        expected.put("body", expectedBody);
        expectedBody.put("exception", "uk.gov.hmcts.ccd.endpoint.exceptions.BadRequestException");
        expectedBody.put("solicitorFee", ANY_FLOATING_NOT_NULLABLE.getValue());
        expectedBody.put("fine", ANY_FLOATING_NULLABLE.getValue());

        Map<String, Object> actual = new ConcurrentHashMap<>();
        Map<String, Object> actualBody = new HashMap<>();

        actual.put("responseCode", 400);
        actual.put("body", actualBody);
        actualBody.put("exception", "uk.gov.hmcts.ccd.endpoint.exceptions.BadRequestException");
        actualBody.put("solicitorFee", 2000.45f);
        actualBody.put("fine", null);

        MapVerificationResult result = new MapVerifier("actualResponse", 0).verifyMap(expected, actual);
        Assert.assertEquals(0, result.getAllIssues().size());
        Assert.assertTrue(result.isVerified());
    }




    @Test
    public void shouldVerifySimpleMapOfAcceptableContentCaseInsensitively() {
        Map<String, Object> expected = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        expected.put("Vary", "accept-encoding");

        Map<String, Object> actual = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        actual.put("vary", "AccEpt-EncoDing");

        MapVerificationResult result = new MapVerifier("actualResponse.headers", 1).verifyMap(expected, actual);
        Assert.assertEquals(1, result.getAllIssues().size());
        Assert.assertTrue(!result.isVerified());

        result = new MapVerifier("actualResponse.headers", 1, false).verifyMap(expected, actual);
        Assert.assertEquals(0, result.getAllIssues().size());
        Assert.assertTrue(result.isVerified());
    }

    @Test
    public void shouldNotVerifySimpleMapsWithUnexpectedFields() {
        Map<String, Object> expected = new HashMap<>();
        Map<String, Object> actual = new ConcurrentHashMap<>();

        actual.put("key1", "value1");
        MapVerificationResult result = new MapVerifier("actualResponse.body", 0).verifyMap(expected, actual);
        Assert.assertArrayEquals(new Object[] {
            "actualResponse.body has unexpected field(s): [key1]"
        }, result.getAllIssues().toArray());

        actual.put("number", 15);
        result = new MapVerifier("actualResponse.body", 0).verifyMap(expected, actual);
        Assert.assertArrayEquals(new Object[] {
            "actualResponse.body has unexpected field(s): [key1, number]"
        }, result.getAllIssues().toArray());
    }

    @Test
    public void shouldNotVerifySimpleMapsWithUnavailableFields() {
        Map<String, Object> expected = new HashMap<>();
        Map<String, Object> actual = new ConcurrentHashMap<>();

        expected.put("key1", "value1");
        MapVerificationResult result = new MapVerifier("actualResponse.body", 0).verifyMap(expected, actual);
        Assert.assertArrayEquals(new Object[] {
            "actualResponse.body lacks [key1] field(s) that was/were actually expected to be there."
        }, result.getAllIssues().toArray());

        expected.put("number", 15);
        result = new MapVerifier("actualResponse.body", 0).verifyMap(expected, actual);
        Assert.assertArrayEquals(new Object[] {
            "actualResponse.body lacks [key1, number] field(s) that was/were actually expected to be there."
        }, result.getAllIssues().toArray());
    }

    @Test
    public void shouldNotVerifySimpleMapsWithUnexpectedAndUnavailableFields() {
        Map<String, Object> expected = new HashMap<>();
        Map<String, Object> actual = new ConcurrentHashMap<>();

        expected.put("key1", "value1");
        actual.put("key1", "value1");
        expected.put("key20", "samevalue");
        actual.put("key21", "samevalue");
        MapVerificationResult result = new MapVerifier("actualResponse.body", 0).verifyMap(expected, actual);
        Assert.assertArrayEquals(new Object[] {
            "actualResponse.body has unexpected field(s): [key21]",
            "actualResponse.body lacks [key20] field(s) that was/were actually expected to be there."
        }, result.getAllIssues().toArray());

        expected.put("key30", "samevalue");
        actual.put("key31", "samevalue");
        result = new MapVerifier("actualResponse.body", 0).verifyMap(expected, actual);
        Assert.assertArrayEquals(new Object[] {
            "actualResponse.body has unexpected field(s): [key21, key31]",
            "actualResponse.body lacks [key20, key30] field(s) that was/were actually expected to be there."
        }, result.getAllIssues().toArray());
    }

    @Test
    public void shouldNotVerifySimpleMapsWithBadValues() {
        Map<String, Object> expected = new HashMap<String, Object>();
        Map<String, Object> actual = new ConcurrentHashMap<String, Object>();

        expected.put("key1", "value1");
        actual.put("key1", "value1");
        expected.put("key2", "value2");
        actual.put("key2", "value2_bad");
        MapVerificationResult result = new MapVerifier("actualResponse.body", 0).verifyMap(expected, actual);
        Assert.assertFalse(result.isVerified());
        Assert.assertArrayEquals(new Object[] {
            "actualResponse.body contains 1 bad value(s): [key2: expected 'value2' but got 'value2_bad']"
        }, result.getAllIssues().toArray());

        expected.put("key3", "value3");
        actual.put("key3", "value3_bad");
        result = new MapVerifier("actualResponse.body", 0).verifyMap(expected, actual);
        Assert.assertArrayEquals(new Object[] {
            "actualResponse.body contains 2 bad value(s): [key2: expected 'value2' but got 'value2_bad', "
                + "key3: expected 'value3' but got 'value3_bad']"
        }, result.getAllIssues().toArray());

        expected.put("key30", "samevalue");
        actual.put("key31", "samevalue");
        result = new MapVerifier("actualResponse.body", 0).verifyMap(expected, actual);
        Assert.assertArrayEquals(new Object[] {
            "actualResponse.body has unexpected field(s): [key31]",
            "actualResponse.body lacks [key30] field(s) that was/were actually expected to be there.",
            "actualResponse.body contains 2 bad value(s): [key2: expected 'value2' but got 'value2_bad', "
                + "key3: expected 'value3' but got 'value3_bad']"
        }, result.getAllIssues().toArray());
    }

    @Test
    public void shouldVerifyCascadedMapsWithSameValuesWithoutWildcards() {
        Map<String, Object> expected = new HashMap<>();
        Map<String, Object> actual = new ConcurrentHashMap<>();

        expected.put("key1", "value1");
        actual.put("key1", "value1");

        Map<String, Object> submap = new ConcurrentHashMap<>();
        expected.put("key2", submap);
        actual.put("key2", submap);
        submap.put("subfield1", "subfield1_value");
        submap.put("subfield2", "subfield2_value");

        Map<String, Object> subsubmap = new ConcurrentHashMap<>();
        submap.put("subsubmap", subsubmap);
        subsubmap.put("subsubfield1", "subsubfield1_value");
        subsubmap.put("subsubfield2", "subsubfield2_value");

        MapVerificationResult result = new MapVerifier("", 0).verifyMap(expected, actual);
        Assert.assertTrue(result.isVerified());
    }

    @Test
    public void shouldNotVerifyCascadedMapsWithSameValuesWithoutWildcards() {
        Map<String, Object> expected = new HashMap<>();
        Map<String, Object> actual = new ConcurrentHashMap<>();

        expected.put("key1", "value1");
        actual.put("key1", "value1");

        Map<String, Object> submap1 = new ConcurrentHashMap<>();
        Map<String, Object> submap2 = new ConcurrentHashMap<>();
        expected.put("submap", submap1);
        actual.put("submap", submap2);
        submap1.put("submapkey", "submapvalue");
        submap2.put("submapkey", "submapvalue");

        Map<String, Object> subsubmap1 = new ConcurrentHashMap<>();
        Map<String, Object> subsubmap2 = new ConcurrentHashMap<>();
        submap1.put("subsubmap", subsubmap1);
        submap2.put("subsubmap", subsubmap2);
        subsubmap1.put("subsubmapfield1", "subsubmapfield1_value");
        subsubmap2.put("subsubmapfield1", "subsubmapfield1_value_bad");
        subsubmap1.put("subsubmapfield2", "subsubmapfield2_value");
        subsubmap2.put("subsubmapfield2", "subsubmapfield2_value_bad");

        subsubmap1.put("subsubmapfield3a", "subsubmapfield3");
        subsubmap2.put("subsubmapfield3b", "subsubmapfield3");

        MapVerificationResult result = new MapVerifier("actualResponse.body", 0).verifyMap(expected, actual);
        Assert.assertArrayEquals(new Object[] {
            "actualResponse.body.submap.subsubmap has unexpected field(s): [subsubmapfield3b]",
            "actualResponse.body.submap.subsubmap lacks [subsubmapfield3a] field(s) that was/were actually "
                + "expected to be there.",
            "actualResponse.body.submap.subsubmap contains 2 bad value(s): [actualResponse.body.submap"
                + ".subsubmap.subsubmapfield1, actualResponse.body.submap.subsubmap.subsubmapfield2]"
        }, result.getAllIssues().toArray());

        Assert.assertFalse(result.isVerified());
    }

    @Test
    public void shouldVerifyABigRealResponseBodyAgainstItselfWithoutWildcards() {

        HashMap<String, Object> expected = (HashMap<String, Object>) TEST_DATA_RESOURCE
                .getDataForTestCall("HttpTestData-with-a-Big-ExpectedResponseBody_expected")
                .getExpectedResponse().getBody();
        HashMap<String, Object> actual = (HashMap<String, Object>) TEST_DATA_RESOURCE
                .getDataForTestCall("HttpTestData-with-a-Big-ExpectedResponseBody_actual")
                .getExpectedResponse().getBody();

        MapVerificationResult result = new MapVerifier("actualResponse.body", 5).verifyMap(expected, actual);
        Assert.assertArrayEquals(new Object[] {
            "actualResponse.body.user contains a bad value: idam[0] contains a bad value: jurisdiction: "
                + "expected 'AUTOTEST1' but got 'AUTOTEST1_x'"
        }, result.getAllIssues().toArray());

        Assert.assertFalse(result.isVerified());
    }

    @Test
    public void shouldVerifyAResponseHeaderMapCaseInsensitively() {

        Map<String, Object> expected = TEST_DATA_RESOURCE
                .getDataForTestCall("HttpTestData-with-a-Big-ExpectedResponseBody_expected").getExpectedResponse()
                .getHeaders();
        Map<String, Object> actual = TEST_DATA_RESOURCE
                .getDataForTestCall("HttpTestData-with-a-Big-ExpectedResponseBody_actual").getExpectedResponse()
                .getHeaders();

        MapVerificationResult result = new MapVerifier("", 5).verifyMap(expected, actual);

        Assert.assertEquals(0, result.getAllIssues().size());
        Assert.assertTrue(result.isVerified());
    }

    @Test
    public void shoudlFailForCollectionsOfDifferentSizes() {
        Map<String, Object> expected = TEST_DATA_RESOURCE.getDataForTestCall("MapWithArray_expected")
                .getExpectedResponse()
                .getBody();
        Map<String, Object> actual = TEST_DATA_RESOURCE.getDataForTestCall("MapWithArray_actual").getExpectedResponse()
                .getBody();

        MapVerificationResult result = new MapVerifier("actualResponse.body", 5).verifyMap(expected, actual);

        Assert.assertArrayEquals(new Object[] {
                "actualResponse.body.details contains a bad value: actualResponse.body.details.field_errors has unexpected number of elements. Expected: 1, but actual: 2." },
                result.getAllIssues().toArray());

    }
}
