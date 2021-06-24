package uk.gov.hmcts.befta.featuretoggle;

import io.cucumber.java.Scenario;
import uk.gov.hmcts.befta.exception.FeatureToggleCheckFailureException;
import uk.gov.hmcts.befta.featuretoggle.launchdarkly.LaunchDarklyFeatureToggleService;

public class DefaultMultiSourceFeatureToggleService implements FeatureToggleService<Scenario, ScenarioFeatureToggleInfo> {

    public static final DefaultMultiSourceFeatureToggleService INSTANCE = new DefaultMultiSourceFeatureToggleService();
    private static final String FEATURE_TOGGLE_SIGNER = "FeatureToggle";

    public static final String COLON = ":";
    public static final String STRING_EQUALS = "=";

    @Override
    public ScenarioFeatureToggleInfo getToggleStatusFor(Scenario toggleable) throws FeatureToggleCheckFailureException {
        ScenarioFeatureToggleInfo scenarioFeatureToggleInfo = new ScenarioFeatureToggleInfo();
        // Examples : @FeatureToggle(LD:feature_id_1=on) @FeatureToggle(IAC:feature_id_2=off)
        toggleable.getSourceTagNames().forEach(tag -> {
            if (tag.contains(FEATURE_TOGGLE_SIGNER)) {
                String domain = null;
                String id = null;
                Boolean expectedStatus = null;
                Boolean actualStatus;

                domain = tag.contains(COLON) ? tag.substring(tag.indexOf("(") + 1, tag.indexOf(COLON)) : "LD";

                FeatureToggleService service = getToggleService(domain);

                if (!tag.contains(COLON) && !tag.contains(STRING_EQUALS)) {
                    id = tag.substring(tag.indexOf("(") + 1, tag.indexOf(")"));
                } else if (tag.contains(COLON) && !tag.contains(STRING_EQUALS)) {
                    id = tag.substring(tag.indexOf(COLON) + 1, tag.indexOf(")"));
                } else if (tag.contains(COLON) && tag.contains(STRING_EQUALS)) {
                    id = tag.substring(tag.indexOf(COLON) + 1, tag.indexOf(STRING_EQUALS));
                }

                if (tag.contains(STRING_EQUALS)) {
                    String expectedStatusString = tag.substring(tag.indexOf(STRING_EQUALS) + 1, tag.indexOf(")"));
                    expectedStatus = expectedStatusString.equalsIgnoreCase("on");
                    scenarioFeatureToggleInfo.addExpectedStatus(id, expectedStatus);
                }

                actualStatus = (Boolean) service.getToggleStatusFor(id);
                scenarioFeatureToggleInfo.addActualStatus(id, actualStatus);
            }
        });
        return scenarioFeatureToggleInfo;
    }

    protected FeatureToggleService getToggleService(String toggleDomain) {
        if (toggleDomain.equalsIgnoreCase("LD") || toggleDomain.equalsIgnoreCase("LaunchDarkly")) {
            return new LaunchDarklyFeatureToggleService();
        } else
            throw new FeatureToggleCheckFailureException("Doesn't know FeatureToggleService for Domain " + toggleDomain);
    }
}
