/**
 * 
 */
package uk.gov.hmcts.befta.featuretoggle;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author korneleehenry
 *
 */
class ScenarioFeatureToggleInfoTest {

    private ScenarioFeatureToggleInfo scenarioFeatureToggleInfo = null;

    @BeforeEach
    public void setUp() {
        scenarioFeatureToggleInfo = new ScenarioFeatureToggleInfo();

    }

    @AfterEach
    public void cleanUp() {
        scenarioFeatureToggleInfo = null;
    }

    /**
     * Test method for
     * {@link ScenarioFeatureToggleInfo#ScenarioFeatureToggleInfo()}.
     */
    @Test
    void testFeatureToggleInfo() {
        assertNotNull(scenarioFeatureToggleInfo);
    }

    /**
     * Test method for
     * {@link ScenarioFeatureToggleInfo#addActualStatus(java.lang.String, java.lang.Boolean)}.
     */
    @Test
    void testAdd() {
        scenarioFeatureToggleInfo.addActualStatus("Flag", true);
        assertTrue(scenarioFeatureToggleInfo.isAnyEnabled());
        assertTrue(scenarioFeatureToggleInfo.isAllEnabled());
        assertFalse(scenarioFeatureToggleInfo.isAnyDisabled());
        assertFalse(scenarioFeatureToggleInfo.isAllDisabled());
    }

    /**
     * Test method for
     * {@link ScenarioFeatureToggleInfo#isAnyEnabled()}.
     */
    @Test
    void testIsAnyEnabled() {
        scenarioFeatureToggleInfo.addActualStatus("Flag", false);
        assertFalse(scenarioFeatureToggleInfo.isAnyEnabled());
    }

    /**
     * Test method for
     * {@link ScenarioFeatureToggleInfo#isAllEnabled()}.
     */
    @Test
    void testIsAllEnabled() {
        scenarioFeatureToggleInfo.addActualStatus("Flag1", true);
        scenarioFeatureToggleInfo.addActualStatus("Flag2", true);
        assertTrue(scenarioFeatureToggleInfo.isAllEnabled());
    }

    /**
     * Test method for
     * {@link ScenarioFeatureToggleInfo#isAnyDisabled()}.
     */
    @Test
    void testIsAnyDisabled() {
        scenarioFeatureToggleInfo.addActualStatus("Flag", false);
        assertTrue(scenarioFeatureToggleInfo.isAnyDisabled());
    }

    /**
     * Test method for
     * {@link ScenarioFeatureToggleInfo#isAllDisabled()}.
     */
    @Test
    void testIsAllDisabled() {
        scenarioFeatureToggleInfo.addActualStatus("Flag1", false);
        scenarioFeatureToggleInfo.addActualStatus("Flag2", false);
        assertTrue(scenarioFeatureToggleInfo.isAllDisabled());
    }

    /**
     * Test method for
     * {@link ScenarioFeatureToggleInfo#getDisabledFeatureFlags()}.
     */
    @Test
    void testGetDisabledFeatureFlags() {
        List<String> list = Arrays.asList("Flag1", "Flag2");
        scenarioFeatureToggleInfo.addActualStatus("Flag1", false);
        scenarioFeatureToggleInfo.addActualStatus("Flag2", false);
        scenarioFeatureToggleInfo.addActualStatus("Flag3", true);
        scenarioFeatureToggleInfo.addActualStatus("Flag4", true);
        assertFalse(scenarioFeatureToggleInfo.isAllEnabled());
        assertTrue(scenarioFeatureToggleInfo.getDisabledFeatureFlags().containsAll(list));
    }

    /**
     * Test method for
     * {@link ScenarioFeatureToggleInfo#getEnabledFeatureFlags()}.
     */
    @Test
    void testGetEnabledFeatureFlags() {
        List<String> list = Arrays.asList("Flag3", "Flag4");
        scenarioFeatureToggleInfo.addActualStatus("Flag2", false);
        scenarioFeatureToggleInfo.addActualStatus("Flag3", true);
        scenarioFeatureToggleInfo.addActualStatus("Flag4", true);
        assertTrue(scenarioFeatureToggleInfo.getEnabledFeatureFlags().containsAll(list));
    }

    /**
     * Test method for
     * {@link ScenarioFeatureToggleInfo#shouldScenarioBeRun()}.
     */
    @Test
    void testShouldReturnFalseForAnyDisabledFlag() {
        scenarioFeatureToggleInfo.addActualStatus("Flag2", false);
        scenarioFeatureToggleInfo.addActualStatus("Flag3", true);
        scenarioFeatureToggleInfo.addActualStatus("Flag4", true);
        assertFalse(scenarioFeatureToggleInfo.shouldScenarioBeRun());
    }

    /**
     * Test method for
     * {@link ScenarioFeatureToggleInfo#shouldScenarioBeRun()}.
     */
    @Test
    void testShouldReturnFalseForExpectedFlagAsFalse() {
        scenarioFeatureToggleInfo.addActualStatus("Flag4", true);
        scenarioFeatureToggleInfo.addExpectedStatus("Flag4", false);
        assertFalse(scenarioFeatureToggleInfo.shouldScenarioBeRun());
    }

    /**
     * Test method for
     * {@link ScenarioFeatureToggleInfo#shouldScenarioBeRun()}.
     */
    @Test
    void testShouldReturnTrueForExpectedFlagAsTrue() {
        scenarioFeatureToggleInfo.addActualStatus("Flag4", true);
        scenarioFeatureToggleInfo.addExpectedStatus("Flag4", true);

        assertTrue(scenarioFeatureToggleInfo.shouldScenarioBeRun());
    }

}
