package com.argus.projectanalysis.infrastructure.adapters.outbound.filesystem;

import com.argus.projectanalysis.domain.exceptions.ProjectNotFoundError;
import com.argus.projectanalysis.domain.ports.outbound.ProjectFileScanner;
import com.argus.projectanalysis.domain.valueobjects.ProjectFiles;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** Ne retient que les fichiers .java de src/main/java et src/test/java (projet multi-module inclus). */
public class FileSystemProjectFileScanner implements ProjectFileScanner {

    private static final Set<String> IGNORED_DIRECTORIES =
            Set.of("target", "build", "out", ".git", ".idea", ".gradle", "node_modules");

    @Override
    public ProjectFiles scan(Path projectRoot) {
        Path root = projectRoot.toAbsolutePath().normalize();
        if (!Files.isDirectory(root)) {
            throw new ProjectNotFoundError(root.toString());
        }

        List<Path> main = new ArrayList<>();
        List<Path> test = new ArrayList<>();

        try {
            Files.walkFileTree(root, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    boolean ignored = !dir.equals(root) && IGNORED_DIRECTORIES.contains(dir.getFileName().toString());
                    return ignored ? FileVisitResult.SKIP_SUBTREE : FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    classify(root.relativize(file), main, test);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new ProjectNotFoundError(root + " (lecture impossible : " + e.getMessage() + ")");
        }

        main.sort(null);
        test.sort(null);
        return new ProjectFiles(main, test);
    }

    private static void classify(Path relative, List<Path> main, List<Path> test) {
        String name = relative.getFileName().toString();
        if (!name.endsWith(".java") || name.equals("package-info.java") || name.equals("module-info.java")) {
            return;
        }
        String path = "/" + relative.toString().replace('\\', '/');
        if (path.contains("/src/main/java/")) {
            main.add(relative);
        } else if (path.contains("/src/test/java/")) {
            test.add(relative);
        }
    }
}