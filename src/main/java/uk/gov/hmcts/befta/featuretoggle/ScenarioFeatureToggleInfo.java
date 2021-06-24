package uk.gov.hmcts.befta.featuretoggle;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class ScenarioFeatureToggleInfo {

    private Map<String, Boolean> actualStatuses = new ConcurrentHashMap<>();
    private Map<String, Boolean> expectedStatuses = new ConcurrentHashMap<>();

    public ScenarioFeatureToggleInfo() {
    }

    public void addActualStatus(String flag, Boolean enabled) {
        actualStatuses.put(flag, enabled);
    }

    public void addExpectedStatus(String flag, Boolean enabled) {
        expectedStatuses.put(flag, enabled);
    }

    public boolean isAnyEnabled() {
        return actualStatuses.containsValue(Boolean.TRUE);
    }

    public boolean isAllEnabled() {
        return !isAnyDisabled();
    }

    public boolean isAnyDisabled() {
        return actualStatuses.containsValue(Boolean.FALSE);
    }

    public boolean isAllDisabled() {
        return !isAnyEnabled();
    }

    public List<String> getDisabledFeatureFlags() {
        return actualStatuses.entrySet().stream().filter(e -> !e.getValue()).map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public List<String> getEnabledFeatureFlags() {
        return actualStatuses.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    public boolean shouldScenarioBeRun() {
        AtomicBoolean matchesExpectations = new AtomicBoolean(true);
        if (expectedStatuses.isEmpty() && isAnyDisabled()) {
            matchesExpectations.set(false);
        } else {
            actualStatuses.forEach((actualStatusKey, actualStatusValue) -> {
                if (expectedStatuses.containsKey(actualStatusKey) &&
                        !expectedStatuses.get(actualStatusKey).equals(actualStatusValue)) {
                    matchesExpectations.set(false);
                }
            });
        }
        return matchesExpectations.get();
    }

}
