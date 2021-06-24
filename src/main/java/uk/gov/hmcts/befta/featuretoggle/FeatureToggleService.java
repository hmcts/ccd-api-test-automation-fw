package uk.gov.hmcts.befta.featuretoggle;

import uk.gov.hmcts.befta.exception.FeatureToggleCheckFailureException;

public interface FeatureToggleService<T, R> {

    FeatureToggleService DEFAULT_INSTANCE = DefaultMultiSourceFeatureToggleService.INSTANCE;

    R getToggleStatusFor(T toggleable) throws FeatureToggleCheckFailureException;

}
