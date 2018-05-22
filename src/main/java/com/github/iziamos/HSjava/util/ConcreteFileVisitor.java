package com.github.iziamos.HSjava.util;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public final class ConcreteFileVisitor implements FileVisitor<Path> {
    private final List<Path> javaFileList = new ArrayList<Path>();
    private final List<Path> zipOrJarFileList = new ArrayList<Path>();

    public List<Path> getZipOrJarFileList() {
        return zipOrJarFileList;
    }

    public List<Path> getJavaFileList() {
        return javaFileList;
    }

    @Override
    public FileVisitResult preVisitDirectory(final Path dir,
            final BasicFileAttributes attrs) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(final Path file,
            final BasicFileAttributes attrs) throws IOException {
        if (file.toString().endsWith(".java")) {
            javaFileList.add(file);
        }

        if (file.toString().endsWith(".zip")
                || file.toString().endsWith(".jar")) {
            zipOrJarFileList.add(file);
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(final Path file,
            final IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(final Path dir,
            final IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

}
