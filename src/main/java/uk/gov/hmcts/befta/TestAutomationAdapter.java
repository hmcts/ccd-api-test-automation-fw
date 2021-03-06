package uk.gov.hmcts.befta;

import java.util.concurrent.ExecutionException;

import uk.gov.hmcts.befta.data.UserData;
import uk.gov.hmcts.befta.player.BackEndFunctionalTestScenarioContext;

public interface TestAutomationAdapter {

    static String getExecutionFileInfoNameFor(Object environment) {
        String infix = environment == null ? "" : "_" + environment;
        return "./befta_recent_executions_info" + infix + ".json";
    }

    String getNewS2SToken();

    String getNewS2SToken(String clientId);

    String getNewS2SToken(String clientId, String clientKey);

    String getNewS2STokenWithEnvVars(String envVarNameForId, String envVarNameForKey);

    void authenticate(UserData user, String preferredTokenClientId) throws ExecutionException;

    Object calculateCustomValue(BackEndFunctionalTestScenarioContext scenarioContext, Object key);

    BeftaTestDataLoader getDataLoader();
}
