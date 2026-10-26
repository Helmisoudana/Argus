package com.argus.projectanalysis.infrastructure.adapters.outbound.javaparser;

import com.argus.projectanalysis.domain.entities.ExistingTest;
import com.argus.projectanalysis.domain.entities.MethodInfo;
import com.argus.projectanalysis.domain.entities.SourceUnit;
import com.argus.projectanalysis.domain.ports.outbound.SourceCodeReader;
import com.argus.projectanalysis.domain.valueobjects.CodeLocation;
import com.argus.projectanalysis.domain.valueobjects.ComplexityMetrics;
import com.argus.projectanalysis.domain.valueobjects.DependencyKind;
import com.argus.projectanalysis.domain.valueobjects.DependencyRef;
import com.argus.projectanalysis.domain.valueobjects.ParameterInfo;
import com.argus.projectanalysis.domain.valueobjects.ParseIssue;
import com.argus.projectanalysis.domain.valueobjects.ReadResult;
import com.argus.projectanalysis.domain.valueobjects.Stereotype;
import com.argus.projectanalysis.domain.valueobjects.UnitKind;
import com.argus.projectanalysis.domain.valueobjects.Visibility;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.RecordDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.WildcardType;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Lecteur de code Java (JavaParser). La v1 ne supporte aucun autre langage. */
public class JavaParserSourceCodeReader implements SourceCodeReader {

    private static final Set<String> TEST_ANNOTATIONS =
            Set.of("Test", "ParameterizedTest", "RepeatedTest", "TestFactory");

    private final JavaParser parser = new JavaParser(
            new ParserConfiguration().setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_21));

    // ------------------------------------------------------------------ sources

    @Override
    public ReadResult<SourceUnit> readSources(Path projectRoot, Collection<Path> files) {
        List<SourceUnit> units = new ArrayList<>();
        List<ParseIssue> issues = new ArrayList<>();

        for (Path relative : files) {
            String file = normalize(relative);
            parse(projectRoot, relative, issues).ifPresent(cu -> {
                String pkg = packageOf(cu);
                Imports imports = new Imports(cu);
                for (TypeDeclaration<?> type : cu.getTypes()) {
                    collectUnits(type, pkg, qualify(pkg, type.getNameAsString()), file, imports, units);
                }
            });
        }
        return new ReadResult<>(units, issues);
    }

    private void collectUnits(TypeDeclaration<?> type, String pkg, String qualifiedName,
                              String file, Imports imports, List<SourceUnit> out) {
        out.add(toUnit(type, pkg, qualifiedName, file, imports));
        for (BodyDeclaration<?> member : type.getMembers()) {
            if (member instanceof TypeDeclaration<?> nested) {
                collectUnits(nested, pkg, qualifiedName + "." + nested.getNameAsString(), file, imports, out);
            }
        }
    }

    private SourceUnit toUnit(TypeDeclaration<?> type, String pkg, String qualifiedName,
                              String file, Imports imports) {
        UnitKind kind = kindOf(type);
        Set<String> annotations = annotationNames(type.getAnnotations());
        List<String> superTypes = superTypesOf(type).stream().map(imports::resolve).toList();

        List<MethodInfo> methods = type.getMethods().stream()
                .map(m -> toMethod(m, kind == UnitKind.INTERFACE, file))
                .toList();
        List<MethodInfo> constructors = type.getConstructors().stream()
                .map(c -> toConstructor(c, file))
                .toList();

        CodeLocation location = locationOf(type, file);
        ComplexityMetrics sum = Stream.concat(methods.stream(), constructors.stream())
                .map(MethodInfo::metrics)
                .reduce(ComplexityMetrics.ZERO, ComplexityMetrics::plus);
        ComplexityMetrics metrics = new ComplexityMetrics(sum.cyclomatic(), sum.branches(), location.lineCount());

        return new SourceUnit(qualifiedName, pkg, type.getNameAsString(), kind,
                Stereotype.of(annotations, superTypes), annotations, superTypes,
                dependenciesOf(type, superTypes, imports), methods, constructors, location, metrics);
    }

    private MethodInfo toMethod(MethodDeclaration m, boolean inInterface, String file) {
        List<ParameterInfo> params = parametersOf(m.getParameters());
        Visibility visibility = inInterface && !m.isPrivate()
                ? Visibility.PUBLIC
                : visibility(m.isPublic(), m.isProtected(), m.isPrivate());
        boolean hasBody = m.getBody().isPresent();
        CodeLocation location = locationOf(m, file);
        return new MethodInfo(m.getNameAsString(), signature(m.getNameAsString(), params), m.getType().asString(),
                visibility, params, m.getThrownExceptions().stream().map(Type::asString).toList(),
                m.isStatic(), !hasBody, location, measure(m, hasBody, location.lineCount()));
    }

    private MethodInfo toConstructor(ConstructorDeclaration c, String file) {
        List<ParameterInfo> params = parametersOf(c.getParameters());
        CodeLocation location = locationOf(c, file);
        return new MethodInfo(c.getNameAsString(), signature(c.getNameAsString(), params), "void",
                visibility(c.isPublic(), c.isProtected(), c.isPrivate()), params,
                c.getThrownExceptions().stream().map(Type::asString).toList(),
                false, false, location, measure(c, true, location.lineCount()));
    }

    private List<DependencyRef> dependenciesOf(TypeDeclaration<?> type, List<String> superTypes, Imports imports) {
        Set<DependencyRef> refs = new LinkedHashSet<>();

        for (ConstructorDeclaration constructor : type.getConstructors()) {
            constructor.getParameters().forEach(p -> addTypes(p.getType(), DependencyKind.CONSTRUCTOR, imports, refs));
        }
        if (type instanceof RecordDeclaration record) {
            record.getParameters().forEach(p -> addTypes(p.getType(), DependencyKind.CONSTRUCTOR, imports, refs));
        }
        for (FieldDeclaration field : type.getFields()) {
            for (VariableDeclarator variable : field.getVariables()) {
                addTypes(variable.getType(), DependencyKind.FIELD, imports, refs);
            }
        }
        superTypes.forEach(s -> refs.add(new DependencyRef(s, DependencyKind.SUPERTYPE)));
        return new ArrayList<>(refs);
    }

    private void addTypes(Type type, DependencyKind kind, Imports imports, Set<DependencyRef> out) {
        Set<String> names = new LinkedHashSet<>();
        collectTypeNames(type, names);
        names.forEach(n -> out.add(new DependencyRef(imports.resolve(n), kind)));
    }

    /** Extrait les types référencés, génériques inclus : List<UserRepository> donne List et UserRepository. */
    private void collectTypeNames(Type type, Set<String> out) {
        if (type.isArrayType()) {
            collectTypeNames(type.asArrayType().getComponentType(), out);
        } else if (type.isClassOrInterfaceType()) {
            ClassOrInterfaceType c = type.asClassOrInterfaceType();
            out.add(c.getNameWithScope());
            c.getTypeArguments().ifPresent(args -> args.forEach(a -> collectTypeNames(a, out)));
        } else if (type.isWildcardType()) {
            WildcardType w = type.asWildcardType();
            w.getExtendedType().ifPresent(t -> collectTypeNames(t, out));
            w.getSuperType().ifPresent(t -> collectTypeNames(t, out));
        }
    }

    private List<String> superTypesOf(TypeDeclaration<?> type) {
        List<String> names = new ArrayList<>();
        if (type instanceof ClassOrInterfaceDeclaration c) {
            c.getExtendedTypes().forEach(t -> names.add(t.getNameWithScope()));
            c.getImplementedTypes().forEach(t -> names.add(t.getNameWithScope()));
        } else if (type instanceof EnumDeclaration e) {
            e.getImplementedTypes().forEach(t -> names.add(t.getNameWithScope()));
        } else if (type instanceof RecordDeclaration r) {
            r.getImplementedTypes().forEach(t -> names.add(t.getNameWithScope()));
        }
        return names;
    }

    /** Complexité : 1 + points de décision. Une méthode sans corps vaut 0. */
    private static ComplexityMetrics measure(Node node, boolean hasBody, int lines) {
        if (!hasBody) {
            return new ComplexityMetrics(0, 0, lines);
        }
        int decisions = node.findAll(IfStmt.class).size()
                + node.findAll(ForStmt.class).size()
                + node.findAll(ForEachStmt.class).size()
                + node.findAll(WhileStmt.class).size()
                + node.findAll(DoStmt.class).size()
                + node.findAll(CatchClause.class).size()
                + node.findAll(ConditionalExpr.class).size()
                + node.findAll(SwitchEntry.class, e -> !e.getLabels().isEmpty()).size()
                + node.findAll(BinaryExpr.class, b -> b.getOperator() == BinaryExpr.Operator.AND
                || b.getOperator() == BinaryExpr.Operator.OR).size();
        return new ComplexityMetrics(1 + decisions, 2 * decisions, lines);
    }

    // -------------------------------------------------------------------- tests

    @Override
    public ReadResult<ExistingTest> readTests(Path projectRoot, Collection<Path> files) {
        List<ExistingTest> tests = new ArrayList<>();
        List<ParseIssue> issues = new ArrayList<>();

        for (Path relative : files) {
            String file = normalize(relative);
            parse(projectRoot, relative, issues).ifPresent(cu -> {
                String pkg = packageOf(cu);
                for (TypeDeclaration<?> type : cu.getTypes()) {
                    int count = type.findAll(MethodDeclaration.class, m -> m.getAnnotations().stream()
                            .anyMatch(a -> TEST_ANNOTATIONS.contains(a.getName().getIdentifier()))).size();
                    if (count > 0) { // on ignore les classes utilitaires de test
                        tests.add(new ExistingTest(qualify(pkg, type.getNameAsString()), pkg,
                                type.getNameAsString(), count, locationOf(type, file)));
                    }
                }
            });
        }
        return new ReadResult<>(tests, issues);
    }

    // ------------------------------------------------------------------ helpers

    private Optional<CompilationUnit> parse(Path projectRoot, Path relative, List<ParseIssue> issues) {
        String file = normalize(relative);
        try {
            ParseResult<CompilationUnit> result = parser.parse(projectRoot.resolve(relative));
            if (!result.isSuccessful()) {
                String problem = result.getProblems().isEmpty()
                        ? "erreur de syntaxe"
                        : result.getProblems().get(0).getMessage();
                issues.add(new ParseIssue(file, problem)); // un arbre partiel reste exploitable
            }
            return result.getResult();
        } catch (IOException e) {
            issues.add(new ParseIssue(file, "lecture impossible : " + e.getMessage()));
            return Optional.empty();
        }
    }

    private static String packageOf(CompilationUnit cu) {
        return cu.getPackageDeclaration().map(p -> p.getNameAsString()).orElse("");
    }

    private static String qualify(String pkg, String name) {
        return pkg.isEmpty() ? name : pkg + "." + name;
    }

    private static String normalize(Path relative) {
        return relative.toString().replace('\\', '/');
    }

    private static CodeLocation locationOf(Node node, String file) {
        return node.getRange()
                .map(r -> new CodeLocation(file, r.begin.line, r.end.line))
                .orElse(new CodeLocation(file, 1, 1));
    }

    private static UnitKind kindOf(TypeDeclaration<?> type) {
        if (type instanceof ClassOrInterfaceDeclaration c) {
            return c.isInterface() ? UnitKind.INTERFACE : UnitKind.CLASS;
        }
        if (type instanceof EnumDeclaration) return UnitKind.ENUM;
        if (type instanceof RecordDeclaration) return UnitKind.RECORD;
        if (type instanceof AnnotationDeclaration) return UnitKind.ANNOTATION;
        return UnitKind.CLASS;
    }

    private static Visibility visibility(boolean isPublic, boolean isProtected, boolean isPrivate) {
        if (isPublic) return Visibility.PUBLIC;
        if (isProtected) return Visibility.PROTECTED;
        if (isPrivate) return Visibility.PRIVATE;
        return Visibility.PACKAGE;
    }

    private static Set<String> annotationNames(NodeList<AnnotationExpr> annotations) {
        return annotations.stream().map(a -> a.getName().getIdentifier()).collect(Collectors.toSet());
    }

    private static List<ParameterInfo> parametersOf(NodeList<Parameter> parameters) {
        return parameters.stream()
                .map(p -> new ParameterInfo(p.getNameAsString(), p.getType().asString()))
                .toList();
    }

    private static String signature(String name, List<ParameterInfo> params) {
        return name + "(" + params.stream().map(ParameterInfo::type).collect(Collectors.joining(",")) + ")";
    }

    /** Résout un nom de type en nom complet quand un import explicite le permet. */
    private static final class Imports {

        private final Map<String, String> explicit = new HashMap<>();

        Imports(CompilationUnit cu) {
            for (ImportDeclaration imp : cu.getImports()) {
                if (!imp.isAsterisk() && !imp.isStatic()) {
                    String name = imp.getNameAsString();
                    explicit.put(name.substring(name.lastIndexOf('.') + 1), name);
                }
            }
        }

        String resolve(String raw) {
            int dot = raw.indexOf('.');
            String first = dot < 0 ? raw : raw.substring(0, dot);
            if (dot >= 0 && Character.isLowerCase(first.charAt(0))) {
                return raw; // déjà un nom complet
            }
            String fqn = explicit.get(first);
            if (fqn == null) {
                return raw;
            }
            return dot < 0 ? fqn : fqn + raw.substring(dot);
        }
    }
}