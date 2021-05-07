package uk.gov.hmcts.befta.featuretoggle.launchdarkly;

import com.launchdarkly.sdk.LDUser;
import com.launchdarkly.sdk.server.LDClient;
import io.cucumber.java.Scenario;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import uk.gov.hmcts.befta.exception.FeatureToggleCheckFailureException;
import uk.gov.hmcts.befta.featuretoggle.FeatureToggleInfo;
import uk.gov.hmcts.befta.featuretoggle.FeatureToggleService;
import uk.gov.hmcts.befta.util.EnvironmentVariableUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LaunchDarklyFeatureToggleService implements FeatureToggleService {

    public static final LaunchDarklyFeatureToggleService INSTANCE = new LaunchDarklyFeatureToggleService();

    private static final String BEFTA = "befta";
    private static final String USER = "user";
    private static final String SERVICENAME = "servicename";

    private static final LDUser user = new LDUser.Builder(LaunchDarklyConfig.getEnvironmentName()).firstName(BEFTA)
            .lastName(USER).custom(SERVICENAME, LaunchDarklyConfig.getLDMicroserviceName()).build();

    private static final String LAUNCH_DARKLY_FLAG = "FeatureToggle";
    private static final String LAUNCH_DARKLY_FLAG_WITH_EXPECTED_VALUE = "FeatureFlagWithExpectedValue";
    private static final String DATABASE_FLAG_WITH_EXPECTED_VALUE = "DatabaseFlagWithExpectedValue";

    private final LDClient ldClient = LaunchDarklyConfig.getLdInstance();

    @Override
    public FeatureToggleInfo getToggleStatusFor(Scenario scenario) {
        if (ldClient == null)
            return null;

        FeatureToggleInfo status = new FeatureToggleInfo();
        List<String> flagNames = getFeatureFlagsOn(scenario);
        if (flagNames.isEmpty())
            return status;

        checkLaunchDarklyConfig(scenario);

        for (String flag : flagNames) {
            boolean isLDFlagEnabled = ldClient.boolVariation(flag, user, false);
            status.add(flag, isLDFlagEnabled);
        }

        scenario.log(getFeatureFlagsWithDefaultValue(scenario).toString());

        Map<String, Boolean> mapFeatureWithExpectedValues = getFeatureFlagsWithDefaultValue(scenario);
        mapFeatureWithExpectedValues.forEach((flagName, expectedValue) -> {
            boolean isLDFlagEnabled = ldClient.boolVariation(flagName, user, false);
            status.add(flagName, isLDFlagEnabled == expectedValue);
        });

        scenario.log(getDatabaseFlagsWithDefaultValue(scenario).toString());
        Map<String, Boolean> dbFlagMap = getDatabaseFlagsWithDefaultValue(scenario);
        dbFlagMap.forEach((dbFlagName, expectedValue) -> {
            boolean dbFlagValue = getDbFlagValue(dbFlagName);
            System.out.println("isBdFlagEnabled: " + dbFlagValue);

            System.out.println("Is Equals: " + (dbFlagValue == expectedValue));

            scenario.log("isBdFlagEnabled: " + dbFlagValue);
            scenario.log("triplet.right :" + expectedValue);
            scenario.log("Is Equals: " + (dbFlagValue == expectedValue));
            status.add(dbFlagName, dbFlagValue == expectedValue);
        });

        System.out.println("Enabled Flags is  :" + status.getEnabledFeatureFlags());
        System.out.println("Disabled is  :" + status.getDisabledFeatureFlags());

        scenario.log("Enabled Flags is  :" + status.getEnabledFeatureFlags());
        scenario.log("Disabled is  :" + status.getDisabledFeatureFlags());
        return status;
    }

    private void checkLaunchDarklyConfig(Scenario scenario) {
        if (LaunchDarklyConfig.getLDMicroserviceName() == null) {
            throw new FeatureToggleCheckFailureException(
                    "The Scenario is being skipped as MICROSERVICE_NAME variable is not configured");
        }
        if (LaunchDarklyConfig.getEnvironmentName() == null) {
            throw new FeatureToggleCheckFailureException(
                    "The Scenario is being skipped as LAUNCH_DARKLY_ENV is not configured");
        }
    }

    private List<String> getFeatureFlagsOn(Scenario scenario) {
        return scenario.getSourceTagNames().stream().filter(tag -> tag.contains(LAUNCH_DARKLY_FLAG))
                .map(tag -> tag.substring(tag.indexOf("(") + 1, tag.indexOf(")"))).collect(Collectors.toList());
    }

    private Map<String, Boolean> getFeatureFlagsWithDefaultValue(Scenario scenario) {
        scenario.log("Getting getFeatureFlagsWithDefaultValue ");

        System.out.println(scenario.getSourceTagNames());

        System.out.println("Nitish2 + " + scenario.getSourceTagNames()
                .stream()
                .filter(tag -> tag.contains(LAUNCH_DARKLY_FLAG_WITH_EXPECTED_VALUE))
                .map(tag -> tag.substring(tag.indexOf("(") + 1, tag.indexOf(")")))
                .map(str -> str.split(","))
                .collect(Collectors.toMap(str -> str[0], str -> Boolean.parseBoolean(str[1]))));

        scenario.log("Nitish2 + " + scenario.getSourceTagNames()
                .stream()
                .filter(tag -> tag.contains(LAUNCH_DARKLY_FLAG_WITH_EXPECTED_VALUE))
                .map(tag -> tag.substring(tag.indexOf("(") + 1, tag.indexOf(")")))
                .map(str -> str.split(","))
                .collect(Collectors.toMap(str -> str[0], str -> Boolean.parseBoolean(str[1]))));

        return scenario.getSourceTagNames()
                .stream()
                .filter(tag -> tag.contains(LAUNCH_DARKLY_FLAG_WITH_EXPECTED_VALUE))
                .map(tag -> tag.substring(tag.indexOf("(") + 1, tag.indexOf(")")))
                .map(str -> str.split(","))
                .collect(Collectors.toMap(str -> str[0], str -> Boolean.parseBoolean(str[1])));
    }

    private Map<String, Boolean> getDatabaseFlagsWithDefaultValue(Scenario scenario) {
        System.out.println("getting dbFlagValue");
        Map<String, Boolean> dbFlagMap = new HashMap<>();
        scenario.getSourceTagNames().forEach(tagname -> {
            if (tagname.contains(DATABASE_FLAG_WITH_EXPECTED_VALUE)) {
                String[] array = tagname.substring(tagname.indexOf("(") + 1, tagname.indexOf(")")).split(",");
                scenario.log(array.toString());
                System.out.println(array.toString());

                dbFlagMap.put(array[0].trim(), Boolean.valueOf(array[1].trim()));
            }
        });
        System.out.println(dbFlagMap.get(0));
        return dbFlagMap;
    }

    private boolean getDbFlagValue(String dbFlag) {
        ///fetchFlagStatus
        RestAssured.useRelaxedHTTPSValidation();
        StringBuilder path = new StringBuilder();
        path.append("/")
                .append(EnvironmentVariableUtils.getRequiredVariable("DB_FLAG_QUERY_PATH"))
                .append(dbFlag);
        RestAssured.baseURI = "http://localhost:4096";
        System.out.println("path" + path.toString());
        Response response = RestAssured.get(path.toString());
        System.out.println(response);

        System.out.println(response.getStatusCode());
        System.out.println(response.getBody().prettyPrint());

        System.out.println("nitish 5");
        boolean bool = response.getBody().as(Boolean.class);
        return bool;
    }
}
