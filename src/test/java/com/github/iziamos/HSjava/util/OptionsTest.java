package com.github.iziamos.HSjava.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class OptionsTest {

    @Test
    public void testNoArgs() {

        String clArgs = "";
        String[] args = clArgs.split(" ");
        Options options = Options.parse(args);

        assertEquals(false, options.getDoWide());
        assertEquals(false, options.getDoDeep());
        assertEquals(false, options.getDoMultipath());
        assertEquals(false, options.getDoCircular());
        assertEquals(false, options.getDoPrint());
        assertEquals(9, options.getWidthThreshold());
        assertEquals(6, options.getDepthThresold());
        assertEquals(5, options.getCircularThreshold());
        assertEquals(System.getProperty("user.dir"), options.getProjectDirectory());
    }

    @Test
    public void testSplitArgs() {

        String clArgs = "-w -p -wt 77 -md /Users/chris/project";
        String[] args = clArgs.split(" ");
        Options options = Options.parse(args);

        assertEquals(true, options.getDoWide());
        assertEquals(true, options.getDoDeep());
        assertEquals(true, options.getDoMultipath());
        assertEquals(false, options.getDoCircular());
        assertEquals(true, options.getDoPrint());
        assertEquals(77, options.getWidthThreshold());
        assertEquals(6, options.getDepthThresold());
        assertEquals(5, options.getCircularThreshold());
        assertEquals("/Users/chris/project", options.getProjectDirectory());
    }

    @Test
    public void testAllArgs() {
        String clArgs = "-pdwmc -wt 3 -dt 99 -ct 7 /Users/jack/project ";
        ;
        String[] args = clArgs.split(" ");
        Options options = Options.parse(args);

        assertEquals(true, options.getDoWide());
        assertEquals(true, options.getDoDeep());
        assertEquals(true, options.getDoMultipath());
        assertEquals(true, options.getDoCircular());
        assertEquals(true, options.getDoPrint());
        assertEquals(3, options.getWidthThreshold());
        assertEquals(99, options.getDepthThresold());
        assertEquals(7, options.getCircularThreshold());
        assertEquals("/Users/jack/project", options.getProjectDirectory());
    }

    @Test(expected = NumberFormatException.class)
    public void testNoThresholdValues() {

        String clArgs = "-w -p -wt -md ";
        String[] args = clArgs.split(" ");
        Options options = Options.parse(args);

        assertEquals(true, options.getDoWide());
        assertEquals(true, options.getDoDeep());
        assertEquals(true, options.getDoMultipath());
        assertEquals(false, options.getDoCircular());
        assertEquals(true, options.getDoPrint());
        assertEquals(9, options.getWidthThreshold());
        assertEquals(6, options.getDepthThresold());
        assertEquals(5, options.getCircularThreshold());
        assertEquals(System.getProperty("user.dir"), options.getProjectDirectory());
    }

    @Test
    public void testThresholdFirst() {

        String clArgs = "-wt 4 -pw -dt 6";
        String[] args = clArgs.split(" ");
        Options options = Options.parse(args);

        assertEquals(true, options.getDoWide());
        assertEquals(false, options.getDoDeep());
        assertEquals(false, options.getDoMultipath());
        assertEquals(false, options.getDoCircular());
        assertEquals(true, options.getDoPrint());
        assertEquals(4, options.getWidthThreshold());
        assertEquals(6, options.getDepthThresold());
        assertEquals(5, options.getCircularThreshold());
        assertEquals(System.getProperty("user.dir"), options.getProjectDirectory());
    }

    @Test
    public void testIllegalOptions() {

        String clArgs = "-wt 4 -pqw -dt 6";
        String[] args = clArgs.split(" ");
        Options options = Options.parse(args);

        assertEquals(true, options.getDoWide());
        assertEquals(false, options.getDoDeep());
        assertEquals(false, options.getDoMultipath());
        assertEquals(false, options.getDoCircular());
        assertEquals(true, options.getDoPrint());
        assertEquals(4, options.getWidthThreshold());
        assertEquals(6, options.getDepthThresold());
        assertEquals(5, options.getCircularThreshold());
        assertEquals(System.getProperty("user.dir"), options.getProjectDirectory());
    }

    @Test
    public void testMultipleProjects() {

        String clArgs = "-wt 4 -pw -dt 6 /Users/bob/project /Users/alice/project";
        String[] args = clArgs.split(" ");
        Options options = Options.parse(args);

        assertEquals(true, options.getDoWide());
        assertEquals(false, options.getDoDeep());
        assertEquals(false, options.getDoMultipath());
        assertEquals(false, options.getDoCircular());
        assertEquals(true, options.getDoPrint());
        assertEquals(4, options.getWidthThreshold());
        assertEquals(6, options.getDepthThresold());
        assertEquals(5, options.getCircularThreshold());
        assertEquals("/Users/bob/project", options.getProjectDirectory());
    }
}
