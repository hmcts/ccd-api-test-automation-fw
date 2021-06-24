package uk.gov.hmcts.befta.featuretoggle.launchdarkly;

import com.launchdarkly.sdk.server.LDClient;
import io.cucumber.java.Scenario;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import uk.gov.hmcts.befta.exception.FeatureToggleCheckFailureException;
import uk.gov.hmcts.befta.featuretoggle.ScenarioFeatureToggleInfo;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * @author korneleehenry
 */

class LaunchDarklyFeatureToggleServiceTest {
    public static final String LD_SDK_KEY = "LD_SDK_KEY";
    public static final String LD_SDK_KEY_VALUE = "LD_SDK_KEY_VALUE";
    public static final String MICROSERVICE_NAME = "MICROSERVICE_NAME";
    public static final String MICROSERVICE_NAME_VALUE = "MICROSERVICE_NAME_VALUE";
    public static final String LAUNCH_DARKLY_ENV = "LAUNCH_DARKLY_ENV";
    public static final String LAUNCH_DARKLY_ENV_VALUE = "LAUNCH_DARKLY_ENV_VALUE";

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.featuretoggle.launchdarkly.LaunchDarklyFeatureToggleService#getToggleStatusFor(java.lang.String)}.
     */

    @Test
    @SetEnvironmentVariable(key = LD_SDK_KEY, value = LD_SDK_KEY_VALUE)
    @SetEnvironmentVariable(key = MICROSERVICE_NAME, value = MICROSERVICE_NAME_VALUE)
    @SetEnvironmentVariable(key = LAUNCH_DARKLY_ENV, value = LAUNCH_DARKLY_ENV_VALUE)
    void testGetToggleStatusForEmpty() {
        Scenario scenario = mock(Scenario.class);
        LaunchDarklyFeatureToggleService launchDarklyFeatureToggleService = new LaunchDarklyFeatureToggleService();
        ScenarioFeatureToggleInfo status = new ScenarioFeatureToggleInfo();

        assertEquals(status.getDisabledFeatureFlags(), new ArrayList<>());
        assertEquals(status.getEnabledFeatureFlags(), new ArrayList<>());
        assertEquals(false, launchDarklyFeatureToggleService.getToggleStatusFor("DummyFlag"));
    }

    @Test
    @SetEnvironmentVariable(key = LD_SDK_KEY, value = LD_SDK_KEY_VALUE)
    @SetEnvironmentVariable(key = LAUNCH_DARKLY_ENV, value = LAUNCH_DARKLY_ENV_VALUE)
    void testGetToggleStatusForFeature() {
        LDClient ldClient = mock(LDClient.class);
        when(ldClient.boolVariation(anyString(), any(), anyBoolean())).thenReturn(true);

        LaunchDarklyFeatureToggleService launchDarklyFeatureToggleService = new LaunchDarklyFeatureToggleService();

        FeatureToggleCheckFailureException aeThrown = Assertions.assertThrows(FeatureToggleCheckFailureException.class,
                () -> launchDarklyFeatureToggleService.getToggleStatusFor("dummyFlag"));

        assertTrue(aeThrown.getMessage()
                .contains("The Scenario is being skipped as MICROSERVICE_NAME variable is not configured"));
    }

    @Test
    @SetEnvironmentVariable(key = LD_SDK_KEY, value = LD_SDK_KEY_VALUE)
    @SetEnvironmentVariable(key = MICROSERVICE_NAME, value = MICROSERVICE_NAME_VALUE)
    void testGetToggleStatusFor() {

        LaunchDarklyFeatureToggleService launchDarklyFeatureToggleService = new LaunchDarklyFeatureToggleService();

        FeatureToggleCheckFailureException aeThrown = Assertions.assertThrows(FeatureToggleCheckFailureException.class,
                () -> launchDarklyFeatureToggleService.getToggleStatusFor("dummy"));

        assertTrue(
                aeThrown.getMessage().contains("The Scenario is being skipped as LAUNCH_DARKLY_ENV is not configured"));
    }
}
