package com.github.iziamos.HSjava.tree;

import static java.lang.System.lineSeparator;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import com.github.iziamos.HSjava.util.ConcreteFileVisitor;

public final class TreeBuilder {
    private static final int MILLION = 1000000;
    private static final int THREE = 1000000;
    private ClazzNode obj;

    public ClazzNode getObj() {
        return obj;
    }

    public TreeBuilder() {
        obj = new ClazzNode();
        obj.setName("Object");
    }

    public List<ClazzNode> buildTree(final Path source) throws IOException {

        Long time = -System.nanoTime();
        ConcreteFileVisitor fileVisitor = new ConcreteFileVisitor();
        Files.walkFileTree(source, fileVisitor);

        final List<String> fileStrings = new ArrayList<String>();
        for (Path item : fileVisitor.getJavaFileList()) {
            byte[] input = Files.readAllBytes(item);
            fileStrings.add(new String(input, Charset.defaultCharset()));
        }
        time += System.nanoTime();

        System.out.println(
                "Time for reading java files(fs): " + time / MILLION + "ms");
        return buildTree(fileStrings);
    }

    public List<ClazzNode> buildTreeCSV(final String path)
            throws FileNotFoundException, IOException {
        final List<ClazzNode> types = new ArrayList<ClazzNode>();
        final InputStreamReader fileReader = new InputStreamReader(
                new FileInputStream(path), Charset.defaultCharset());

        types.add(obj);
        try (final BufferedReader reader = new BufferedReader(fileReader)) {
            while (reader.ready()) {
                final String line = reader.readLine();
                final String[] clazzData;
                if (line != null) {
                    clazzData = line.split(";");
                } else {
                    continue;
                }

                final ClazzNode item = new ClazzNode();
                item.setName(clazzData[0]);
                item.setSuperclassName(clazzData[1]);

                for (String s : clazzData[2]
                        .substring(1, clazzData[2].length() - 1).split(",")) {
                    if (!s.trim().equals("")) {
                        item.getInterfaces().add(s.trim());
                    }
                }
                for (String s : clazzData[THREE]
                        .substring(1, clazzData[THREE].length() - 1)
                        .split(",")) {
                    item.getDependancyNames().add(s.trim());
                }
                types.add(item);
            }
        }
        resolveAll(types);

        return types;
    }

    private void resolveAll(final List<ClazzNode> types) {

        for (ClazzNode itemC : types) {
            for (ClazzNode itemP : types) {
                if ((itemC.getSuperclass() == null)
                        || itemC.getSuperclass().equals("")) {
                    continue;
                }
                if (itemP.getName().equals(itemC.getSuperclass())) {
                    itemC.setParentClass(itemP);
                    break;
                }
            }
        }
        for (ClazzNode item : types) {
            ClazzNode parent = TreeMethods.findClazz(types,
                    item.getSuperclass());
            item.setParentClass(parent);
        }

        for (ClazzNode item : types) {
            if (item.getParentClass() != null) {
                item.getParentClass().addSubclass(item);
            }
        }

        final HashMap<String, ClazzNode> h = new HashMap<>();
        for (ClazzNode item : types) {
            String[] names = item.getName().split("\\.");
            String name = names[names.length - 1];
            h.put(name, item);
        }

        for (ClazzNode item : types) {
            for (String s : item.getDependancyNames()) {
                String[] names = item.getName().split("\\.");
                String name = names[names.length - 1];
                if (!s.equals(name)) {
                    ClazzNode target = (ClazzNode) h.get(s);
                    // ClazzNode target = TreeMethods.findClazz(types, s);

                    if (target != null) {
                        item.getDependancies().add(target);
                    }
                }
            }
        }
    }

    public List<ClazzNode> buildTree(final ZipFile source) throws IOException {
        Long time = -System.nanoTime();
        List<String> fileStrings = new ArrayList<String>();
        Enumeration<? extends ZipEntry> entries = source.entries();

        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (entry.getName().endsWith(".java")) {
                final StringBuffer buffer = new StringBuffer();
                InputStreamReader inputStreamReader = new InputStreamReader(
                        source.getInputStream(entry), Charset.defaultCharset());
                try (BufferedReader br = new BufferedReader(
                        inputStreamReader)) {
                    while (br.ready()) {
                        buffer.append(br.readLine());
                        buffer.append(lineSeparator());
                    }
                }
                fileStrings.add(buffer.toString());
            }
        }
        time += System.nanoTime();
        System.out.println(
                "Time for reading java files(zip): " + time / MILLION + "ms");
        return buildTree(fileStrings);
    }

    private List<ClazzNode> buildTree(final List<String> inputs) {
        Long time = -System.nanoTime();
        // ASTParser parser = ASTParser.newParser(AST.JLS8);
        ASTParser parser = ASTParser.newParser(AST.JLS3);
        ClazzVisitor visitor = new ClazzVisitor();
        for (String item : inputs) {
            parser.setSource(item.toCharArray());
            CompilationUnit cu = (CompilationUnit) parser.createAST(null);
            cu.accept(visitor);
            // cu.accept(dependancyVisitor);
        }

        final List<ClazzNode> types = new ArrayList<ClazzNode>();

        // obj.setSuperclass("Object");
        types.add(obj);
        types.addAll(visitor.getTypes());

        resolveAll(types);

        time += System.nanoTime();
        System.out.println("Time for generating Tree " + time / MILLION + "ms");
        return types;
    }
}
