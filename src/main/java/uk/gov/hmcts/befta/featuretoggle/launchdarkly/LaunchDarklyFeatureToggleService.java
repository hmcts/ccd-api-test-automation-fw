package uk.gov.hmcts.befta.featuretoggle.launchdarkly;

import com.launchdarkly.sdk.LDUser;
import com.launchdarkly.sdk.server.LDClient;
import uk.gov.hmcts.befta.exception.FeatureToggleCheckFailureException;
import uk.gov.hmcts.befta.featuretoggle.FeatureToggleService;

public class LaunchDarklyFeatureToggleService implements FeatureToggleService<String, Boolean> {

    public static final LaunchDarklyFeatureToggleService INSTANCE = new LaunchDarklyFeatureToggleService();

    private static final String BEFTA = "befta";
    private static final String USER = "user";
    private static final String SERVICENAME = "servicename";

    private static final LDUser ldUser = new LDUser.Builder(LaunchDarklyConfig.getEnvironmentName()).firstName(BEFTA)
            .lastName(USER).custom(SERVICENAME, LaunchDarklyConfig.getLDMicroserviceName()).build();

    private final LDClient ldClient = LaunchDarklyConfig.getLdInstance();

    @Override
    public Boolean getToggleStatusFor(String flagId) {
        if (ldClient == null) {
            return Boolean.FALSE;
        }
        checkLaunchDarklyConfig();
        return ldClient.boolVariation(flagId, ldUser, false);

    }

    private void checkLaunchDarklyConfig() {
        if (LaunchDarklyConfig.getLDMicroserviceName() == null) {
            throw new FeatureToggleCheckFailureException(
                    "The Scenario is being skipped as MICROSERVICE_NAME variable is not configured");
        }
        if (LaunchDarklyConfig.getEnvironmentName() == null) {
            throw new FeatureToggleCheckFailureException(
                    "The Scenario is being skipped as LAUNCH_DARKLY_ENV is not configured");
        }
    }

}
