package com.argus.projectanalysis.domain.services;

import com.argus.projectanalysis.domain.entities.ExistingTest;
import com.argus.projectanalysis.domain.entities.SourceUnit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/** Associe les classes de test existantes à leur cible par convention de nommage. */
public class ExistingTestMatcher {

    private static final List<String> SUFFIXES = List.of("IntegrationTest", "Tests", "Test", "IT");

    /** Retourne : nom complet de la cible vers ses classes de test. */
    public Map<String, List<ExistingTest>> match(Collection<SourceUnit> units, Collection<ExistingTest> tests) {
        Set<String> names = new HashSet<>();
        Map<String, List<String>> bySimpleName = new HashMap<>();
        for (SourceUnit unit : units) {
            names.add(unit.qualifiedName());
            bySimpleName.computeIfAbsent(unit.simpleName(), k -> new ArrayList<>()).add(unit.qualifiedName());
        }

        Map<String, List<ExistingTest>> result = new LinkedHashMap<>();
        for (ExistingTest test : tests) {
            findTarget(test, names, bySimpleName)
                    .ifPresent(target -> result.computeIfAbsent(target, k -> new ArrayList<>()).add(test));
        }
        return result;
    }

    private Optional<String> findTarget(ExistingTest test, Set<String> names, Map<String, List<String>> bySimpleName) {
        String base = targetName(test.simpleName());
        if (base.equals(test.simpleName())) {
            return Optional.empty();
        }
        String samePackage = test.packageName().isEmpty() ? base : test.packageName() + "." + base;
        if (names.contains(samePackage)) {
            return Optional.of(samePackage);
        }
        List<String> candidates = bySimpleName.getOrDefault(base, List.of());
        return candidates.size() == 1 ? Optional.of(candidates.get(0)) : Optional.empty();
    }

    /** UserServiceTest, UserServiceIT, TestUserService : UserService. Sinon retourne le nom tel quel. */
    private String targetName(String testSimpleName) {
        for (String suffix : SUFFIXES) {
            if (testSimpleName.endsWith(suffix) && testSimpleName.length() > suffix.length()) {
                return testSimpleName.substring(0, testSimpleName.length() - suffix.length());
            }
        }
        if (testSimpleName.startsWith("Test") && testSimpleName.length() > 4
                && Character.isUpperCase(testSimpleName.charAt(4))) {
            return testSimpleName.substring(4);
        }
        return testSimpleName;
    }
}