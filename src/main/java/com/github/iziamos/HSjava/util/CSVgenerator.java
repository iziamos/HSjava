package com.github.iziamos.HSjava.util;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.github.iziamos.HSjava.tree.ClazzNode;

public final class CSVgenerator {
    private CSVgenerator() {
    }

    public static void generate(final List<ClazzNode> input)
            throws IOException {

        Path outputPath = Files.createFile(Paths.get("./preparse.csv"));
        OutputStreamWriter fileWriter = new OutputStreamWriter(
                new FileOutputStream(outputPath.toString()),
                Charset.defaultCharset());
        try (BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

            for (ClazzNode item : input) {
                if (!item.getName().equals("Object")) {
                    String clazzData = item.getName() + ";"
                            + item.getSuperclass() + ";" + item.getInterfaces()
                            + ";" + item.getDependancyNames();
                    bufferedWriter.write(clazzData + System.lineSeparator());
                }
            }
        }
    }
}
