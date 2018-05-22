package com.github.iziamos.HSjava.util;

import static java.lang.System.getProperty;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class Options {

    private static final String VALID_OPTIONS = "pwdcmj";
    private boolean multipath = false;
    private boolean deep = false;
    private boolean wide = false;
    private boolean print = false;
    private boolean circular = false;
    private boolean includeJavaLibs = false;
    private int widthThreshold = DefaultThresholds.WIDTH_THRESHOLD;
    private int depthThreshold = DefaultThresholds.DEPTH_THRESHOLD;
    private int circularThreshold = DefaultThresholds.CIRCULAR_THRESHOLD;;
    private String flags = "";
    private String projectDirectory = getProperty("user.dir");
    private final List<Path> libraries = new ArrayList<Path>();

    private Options(final String[] args) {
        setArgs(args);
    }

    public static Options parse(final String[] args) {
        return new Options(args);
    }

    public void setArgs(final String[] args) {

        for (int i = 0; i < args.length; i++) {
            if (args[i].isEmpty()) {
                continue;
            }

            if (args[i].equals("-wt")) {
                widthThreshold = Integer.parseInt(args[++i]);
                continue;
            }

            if (args[i].equals("-dt")) {
                depthThreshold = Integer.parseInt(args[++i]);
                continue;
            }

            if (args[i].equals("-ct")) {
                circularThreshold = Integer.parseInt(args[++i]);
                continue;
            }

            if (args[i].charAt(0) == '-') {
                flags += args[i].substring(1);
                continue;
            }

            if (args[i].charAt(0) == '+') {
                libraries
                        .add(Paths.get(args[i].substring(1, args[i].length())));
                continue;
            }

            if (!projectDirectory.equals(getProperty("user.dir"))) {
                System.out.println(
                        "Cannot operate on multiple projects, argument "
                                + args[i] + " ignored..");
                continue;
            } else {
                projectDirectory = args[i];
            }
            for (String c : flags.split("")) {
                if (!VALID_OPTIONS.contains(c)) {
                    System.out.println("Illegal option '" + c + "' exiting..");
                    return;
                }
            }
        }

        if (flags.contains("p")) {
            print = true;
        }
        if (flags.contains("w")) {
            wide = true;
        }
        if (flags.contains("d")) {
            deep = true;
        }
        if (flags.contains("m")) {
            multipath = true;
        }
        if (flags.contains("c")) {
            circular = true;
        }
        if (flags.contains("j")) {
            includeJavaLibs = true;
        }

    }

    public boolean getDoWide() {
        return wide;
    }

    public boolean getDoDeep() {
        return deep;
    }

    public boolean getDoMultipath() {
        return multipath;
    }

    public boolean getDoCircular() {
        return circular;
    }

    public boolean getDoPrint() {
        return print;
    }

    public boolean getIncludeJavaLibs() {
        return includeJavaLibs;
    }

    public int getWidthThreshold() {
        return widthThreshold;
    }

    public int getDepthThresold() {
        return depthThreshold;
    }

    public int getCircularThreshold() {
        return circularThreshold;
    }

    public String getProjectDirectory() {
        return projectDirectory;
    }

    public String flags() {
        return flags;
    }

    private static final class DefaultThresholds {
        private DefaultThresholds() {
        }
        public static final int WIDTH_THRESHOLD = 9;
        public static final int DEPTH_THRESHOLD = 6;
        public static final int CIRCULAR_THRESHOLD = 5;
    }
}
