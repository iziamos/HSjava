package com.github.iziamos.HSjava.tree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public final class TreeMethods {
    private static final int FORMATTING_CONSTANT = 55;
    private static final int MILLION = 1000000;

    private TreeMethods() {
    }

    public static void printTree(final ClazzNode node) {

        final int depth = 1;
        String nodeString = nodeToString(node, depth);
        System.out.println(nodeString);
        for (ClazzNode child : node.getSubclasses()) {
            printTree(child, depth);
        }
    }

    private static void printTree(final ClazzNode node, final int depth) {

        String nodeString = nodeToString(node, depth);
        for (int i = 1; i < depth; i++) {
            System.out.print("| ");
        }
        System.out.println("|___" + nodeString);

        for (ClazzNode item : node.getSubclasses()) {
            printTree(item, depth + 1);
        }
    }

    private static String nodeToString(final ClazzNode node, final int depth) {

        final String[] names = node.getName().split("\\.");
        final String name = names[names.length - 1];
        int qackageLength = node.getName().length() - name.length() - 1;

        if (qackageLength < 0) {
            qackageLength = 0;
        }
        final String qackage = node.getName().substring(0, qackageLength);
        final String interfaces = node.getInterfaces().toString();

        final int width = name.length();
        StringBuffer tab = new StringBuffer();
        for (int i = 0; i < FORMATTING_CONSTANT - width - (depth * 2); i++) {
            tab.append(" ");
        }
        return name + tab.toString() + " Interfaces: " + interfaces
                + " --- Package: " + qackage;
    }

    public static List<ClazzNode> fixTree(final List<ClazzNode> target,
            final List<ClazzNode> source) {
        Long time = -System.nanoTime();
        List<ClazzNode> types = new ArrayList<ClazzNode>();
        types.addAll(target);

        for (ClazzNode item : target) {
            if ((item.getParentClass() == null)
                    && !item.getName().equals("Object")) {
                ClazzNode sourceItem = TreeMethods.findClazz(source,
                        item.getSuperclass());
                if (sourceItem == null) {
                    continue;
                }
                sourceItem.getSubclasses().add(item);
                item.setParentClass(sourceItem);
                types.addAll(getHiearchyChain(sourceItem));
            }
        }

        List<ClazzNode> ret = new ArrayList<ClazzNode>();
        for (ClazzNode item : types) {
            if (!ret.contains(item)) {
                ret.add(item);
            }
        }

        time += System.nanoTime();
        System.out.println("Time for fixing tree " + time / MILLION + "ms");
        return ret;
    }

    public static void pruneTree(final List<ClazzNode> tree) {

        List<ClazzNode> newSubclasses = new ArrayList<ClazzNode>();
        for (ClazzNode item : tree) {
            for (ClazzNode child : item.getSubclasses()) {
                if (tree.contains(child)) {
                    newSubclasses.add(child);
                }
            }
            item.getSubclasses().clear();
            item.getSubclasses().addAll(newSubclasses);
            newSubclasses.clear();
        }
    }

    public static void removeCircles(final List<ClazzNode> tree) {
        List<ClazzNode> blacklist = new LinkedList<ClazzNode>();
        for (ClazzNode item : tree) {
            if (item.getName().equals("Object")) {
                continue;
            }
            List<ClazzNode> beenThere = new ArrayList<ClazzNode>();
            for (ClazzNode itItem = item
                    .getParentClass(); (itItem != null); itItem = itItem
                            .getParentClass()) {
                if (itItem.equals(item) || beenThere.contains(itItem)) {
                    blacklist.add(item);
                    break;
                }
                beenThere.add(itItem);
            }
            beenThere.clear();
        }
        for (ClazzNode item : blacklist) {
            System.out.println("CIRCLE, Removing: " + item.getName());
            tree.remove(item);
        }
    }

    public static void removeOrphans(final List<ClazzNode> tree) {
        List<ClazzNode> removables = new ArrayList<ClazzNode>();

        for (ClazzNode item : tree) {
            if (item.getName().equals("Object")) {
                continue;
            }
            if (item.getParentClass() == null) {
                System.out.println("ORPHAN, Removing: " + item.getName());
                removables.add(item);
            }
        }

        for (ClazzNode item : removables) {
            tree.remove(item);
        }
    }

    public static List<ClazzNode> getHiearchyChain(final ClazzNode node) {
        List<ClazzNode> ret = new ArrayList<ClazzNode>();

        if (node.getParentClass() != null) {
            ret.add(node);
            if (!node.getSuperclass().equals("Object")) {
                ret.addAll(getHiearchyChain(node.getParentClass()));
            }
        }

        return ret;
    }

    public static void printHiearchyChain(final ClazzNode node) {
        System.out.print(node.getName() + " <- ");
        if (node.getName().equals("Object")) {
            System.out.println();
            return;
        }
        if (node.getParentClass() != null) {
            printHiearchyChain(node.getParentClass());
        }
    }

    public static List<ClazzNode> getAllSubclasses(final ClazzNode node) {
        List<ClazzNode> ret = new ArrayList<ClazzNode>();
        ret.addAll(node.getSubclasses());
        for (ClazzNode item : node.getSubclasses()) {
            ret.addAll(getAllSubclasses(item));
        }
        return ret;
    }

    public static ClazzNode findClazz(final List<ClazzNode> allNodes,
            final String name) {
        if (name.contains(".")) {
            for (ClazzNode item : allNodes) {
                if (item.getName().equals(name)) {
                    return item;
                }
            }

        } else {
            for (ClazzNode item : allNodes) {
                String[] splits = item.getName().split("\\.");
                String cleanname = splits[splits.length - 1];
                if (cleanname.equals(name)) {
                    return item;
                }
            }
        }

        return null;
    }

    public static void findCircular(final List<ClazzNode> targets,
            final String path, final ClazzNode node, final int depth,
            final int maxDepth) {

        if (depth > maxDepth) {
            return;
        }

        for (ClazzNode target : targets) {
            if (node.getName().equals(target.getName())) {
                System.out.println(
                        "CYCLIC HIERARCHY: " + path + " -> " + node.getName());
                return;
            }
        }

        for (ClazzNode child : node.getDependancies()) {
            findCircular(targets, path + " -> " + node.getName(), child,
                    depth + 1, maxDepth);
        }
    }
}
