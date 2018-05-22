package com.github.iziamos.HSjava;

import java.nio.file.Paths;
import java.util.List;

import com.github.iziamos.HSjava.tree.ClazzNode;
import com.github.iziamos.HSjava.tree.TreeBuilder;
import com.github.iziamos.HSjava.tree.TreeMethods;
import com.github.iziamos.HSjava.util.Options;

public final class App {
    private App() {
    }

    public static void main(final String[] args) throws Exception {

        final Options options = Options.parse(args);

        System.out.println("Starting with options " + options.flags() + "..");
        System.out.println("Threshold for depth, width, circular are: "
                + options.getCircularThreshold() + " ,"
                + options.getWidthThreshold() + " ,"
                + options.getCircularThreshold());

        final TreeBuilder tb = new TreeBuilder();
        final List<ClazzNode> projectTypes = tb
                .buildTree(Paths.get(options.getProjectDirectory()));
        // Path javaSrc =
        // Paths.get("/Users/iziamos/Documents/unidocs/dissertation/src/");
        // List<ClazzNode> javaTypes = tb.buildTree(javaSrc);
        // CSVgenerator.generate(javaTypes); if(true) return; // use this line
        // to
        // generate a "csv" with the java types

        List<ClazzNode> finalTree;
        if (options.getIncludeJavaLibs()) {
            List<ClazzNode> javaTypes = tb.buildTreeCSV("./preparse.csv"); // line
                                                                        // of
                                                                        // code
                                                                        // mntn
                                                                        // in
                                                                        // RDME

            finalTree = TreeMethods.fixTree(projectTypes, javaTypes);
            TreeMethods.pruneTree(finalTree);
        } else {
            finalTree = projectTypes;
        }

        TreeMethods.removeOrphans(finalTree);
        TreeMethods.removeCircles(finalTree);
        TreeMethods.removeOrphans(finalTree);
        TreeMethods.removeOrphans(projectTypes);
        TreeMethods.removeCircles(projectTypes);
        TreeMethods.removeOrphans(projectTypes);

        if (options.getDoPrint()) {
            TreeMethods.printTree(tb.getObj());
        }

        if (options.getDoMultipath()) {
            for (ClazzNode item : projectTypes) {
                if (item.getParentClass() == null) {
                    continue;
                }
                List<ClazzNode> chain = TreeMethods
                        .getHiearchyChain(item.getParentClass());

                for (ClazzNode link : chain) {
                    for (String i : link.getInterfaces()) {
                        for (String j : item.getInterfaces()) {
                            String iSuffix = i;
                            if (i.contains(".")) {
                                String[] substrings = i.split("\\.");
                                iSuffix = substrings[substrings.length - 1];
                            }
                            String jSuffix = j;
                            if (j.contains(".")) {
                                String[] substrings = j.split("\\.");
                                jSuffix = substrings[substrings.length - 1];
                            }
                            if (iSuffix.equals(jSuffix)) {
                                System.out.println("MULTIPATH HIERARCHY: Class "
                                    + item.getName()
                                    + " implements interface " + i
                                    + " but also inherits it from "
                                    + link.getName());
                            }
                        }
                    }
                }
            }
        }

        if (options.getDoCircular()) {
            for (ClazzNode item : projectTypes) {
                if (item.getName().equals("Object")) {
                    continue;
                }
                TreeMethods.findCircular(TreeMethods.getAllSubclasses(item),
                        "", item, 0, options.getCircularThreshold());
            }
        }

        if (options.getDoWide()) {
            for (ClazzNode item : finalTree) {
                int size = item.getSubclasses().size();

                if (size > options.getWidthThreshold()) {
                    System.out.println("WIDE HIERARCHY: Class " + item.getName()
                            + " at level of inheritance "
                            + TreeMethods.getHiearchyChain(item).size()
                            + " has " + size + " subclasses ");
                }
            }
        }

        if (options.getDoDeep()) {
            for (ClazzNode item : finalTree) {
                int size = TreeMethods.getHiearchyChain(item).size();
                if (size > options.getDepthThresold()) {
                    System.out.println("DEEP HIERARCHY: Class " + item.getName()
                            + " has " + size + " superclasses ");
                }
            }
        }
    }
}
