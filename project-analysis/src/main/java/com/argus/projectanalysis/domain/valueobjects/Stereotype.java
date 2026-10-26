package com.argus.projectanalysis.domain.valueobjects;

import java.util.List;
import java.util.Set;

public enum Stereotype {
    REST_CONTROLLER, CONTROLLER, SERVICE, REPOSITORY, ENTITY, CONFIGURATION, COMPONENT, OTHER;

    private static final Set<String> REPOSITORY_TYPES =
            Set.of("JpaRepository", "CrudRepository", "PagingAndSortingRepository", "Repository");

    /** À partir des noms simples d'annotations et des super-types. */
    public static Stereotype of(Set<String> annotations, List<String> superTypes) {
        if (annotations.contains("RestController")) return REST_CONTROLLER;
        if (annotations.contains("Controller")) return CONTROLLER;
        if (annotations.contains("Service")) return SERVICE;
        if (annotations.contains("Repository")) return REPOSITORY;
        if (annotations.contains("Entity")) return ENTITY;
        if (annotations.contains("Configuration")) return CONFIGURATION;
        if (annotations.contains("Component")) return COMPONENT;
        boolean repositoryByType = superTypes.stream()
                .map(t -> t.substring(t.lastIndexOf('.') + 1))
                .anyMatch(REPOSITORY_TYPES::contains);
        return repositoryByType ? REPOSITORY : OTHER;
    }
}