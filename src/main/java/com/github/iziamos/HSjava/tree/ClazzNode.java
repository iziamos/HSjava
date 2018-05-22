package com.github.iziamos.HSjava.tree;

import org.eclipse.jdt.core.dom.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Type;

public final class ClazzNode {

    private String name = "";
    private String superclassName = "";
    private List<String> interfaceNames = new ArrayList<String>();
    private ClazzNode superclass = null;
    private final List<ClazzNode> subclasses = new ArrayList<ClazzNode>();
    private List<String> dependancyNames = new ArrayList<String>();
    private List<ClazzNode> dependencies = new ArrayList<ClazzNode>();

    public List<ClazzNode> getSubclasses() {
        return this.subclasses;
    }

    public void addSubclass(final ClazzNode subclass) {
        this.subclasses.add(subclass);
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getSuperclass() {
        return this.superclassName;
    }

    public void setSuperclassName(final String name) {
        final String input = name;

        if (input.equals("")) {
            this.superclassName = "Object";
        } else {
            this.superclassName = input;
        }
        // else {
        //
        // if(input.contains("."))
        // {
        // String [] inputSplit = input.split("\\.");
        // input = inputSplit[inputSplit.length - 1];
        // }
        //
        // this.superclassName = input;
        // }
    }

    public void setSuperclass(final String pkg, final Type type) {
        if (type == null) {
            this.setSuperclassName("Object");
            return;
        }

        if (type.isSimpleType()) {
            if (type.toString().contains(".")) {
                this.setSuperclassName(type.toString());
            } else {
                this.setSuperclassName(pkg + "." + type.toString());
            }
        }
        if (type.isParameterizedType()) {
            String name = type.toString().split("<")[0];

            if (name.contains(".")) {
                this.setSuperclassName(name);
            } else {
                this.setSuperclassName(pkg + "." + name);
            }
        }
    }

    public void setSuperclass(final Type type) {
        if (type == null) {
            this.setSuperclassName("Object");
            return;
        }

        if (type.isSimpleType()) {
            this.setSuperclassName(type.toString());
        }
        if (type.isParameterizedType()) {
            final String name = type.toString().split("<")[0];
            this.setSuperclassName(name);
        }

    }

    public List<String> getInterfaces() {
        return interfaceNames;
    }

    public void setInterfaces(final List<Type> interfaces) {
        List<String> interfaceNames = new ArrayList<String>();
        for (Type item : interfaces) {
            if (item.isSimpleType()) {
                interfaceNames.add(((SimpleType) item).getName().toString());
            }
            if (item.isParameterizedType()) {
                interfaceNames
                        .add(((ParameterizedType) item).getType().toString());
            }
        }
        this.interfaceNames = interfaceNames;
    }

    @Override
    public String toString() {
        final StringBuffer subclassBuffer = new StringBuffer();
        for (ClazzNode item : this.getSubclasses()) {
            subclassBuffer.append(item.getName());
            subclassBuffer.append(", ");
        }
        final String subclasses;
        if (subclassBuffer.length() > 0) {
            subclasses = subclassBuffer.substring(0,
                    subclassBuffer.length() - 2);
        } else {
            subclasses = subclassBuffer.toString();
        }

        return "Type: " + this.getName() + System.lineSeparator()
                + "SuperClass: " + this.getSuperclass() + System.lineSeparator()
                + "Interfaces: " + this.getInterfaces() + System.lineSeparator()
                + "Subclasses: " + subclasses + System.lineSeparator();
    }

    public ClazzNode getParentClass() {
        return superclass;
    }

    public void setParentClass(final ClazzNode parentClass) {
        this.superclass = parentClass;
    }

    public List<ClazzNode> getDependancies() {
        return dependencies;
    }

    public void setDependancies(final List<ClazzNode> dependancies) {
        this.dependencies = dependancies;
    }

    public List<String> getDependancyNames() {
        return dependancyNames;
    }

    public void setDependancyNames(final List<String> dependancynames) {
        this.dependancyNames = dependancynames;
    }

}
