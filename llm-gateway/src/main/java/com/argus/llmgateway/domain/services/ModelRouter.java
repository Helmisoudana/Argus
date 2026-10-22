package com.argus.llmgateway.domain.services;
import com.argus.llmgateway.domain.exceptions.InvalidLlmRequestError;
import com.argus.llmgateway.domain.exceptions.PrivacyViolationError;
import com.argus.llmgateway.domain.valueobjects.ModelProfile;
import com.argus.llmgateway.domain.valueobjects.PrivacyPolicy;
import com.argus.llmgateway.domain.valueobjects.TaskType;

import java.util.Map;

public final class ModelRouter {

    private final Map<TaskType, ModelProfile> catalogue;

    public ModelRouter(Map<TaskType, ModelProfile> catalogue) {
        this.catalogue = Map.copyOf(catalogue);
    }

    public ModelProfile route(TaskType task, PrivacyPolicy policy) {
        ModelProfile profile = catalogue.get(task);
        if (profile == null) {
            throw new InvalidLlmRequestError("task", "aucun modèle configuré pour " + task);
        }
        if (policy == PrivacyPolicy.LOCAL_ONLY && !profile.local()) {
            throw new PrivacyViolationError(profile.provider());
        }
        return profile;
    }
}