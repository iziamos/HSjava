package com.github.iziamos.HSjava.tree;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public final class ClazzVisitor extends ASTVisitor {

    private List<ClazzNode> types = new ArrayList<ClazzNode>();

    public List<ClazzNode> getTypes() {
        return this.types;
    }

    public boolean visit(final CompilationUnit node) {
        ClazzNode info = new ClazzNode();

        ExtendVisitor extendVisitor = new ExtendVisitor();
        node.accept(extendVisitor);
        PackageVisitor pv = new PackageVisitor();
        node.accept(pv);
        // KEEP IN MIND THAT ALOT OF PROBLEMS START HERE

        if (extendVisitor.getName().contains(".")) {
            info.setName(extendVisitor.getName());
        } else {
            info.setName(pv.getPackageName() + "." + extendVisitor.getName());
        }

        info.setSuperclass(extendVisitor.getSuperClass());
        info.setInterfaces(extendVisitor.getInterfaces());

        for (String item : extendVisitor.getDependancyNames()) {
            if (!info.getDependancyNames().contains(item)) {
                info.getDependancyNames().add(item);
            }
        }

        if (!info.getName().equals("") && !info.getName().equals("Object")) {
            if (!extendVisitor.isInterface()) {
                types.add(info);
            }
        }

        return false;
    }

    private static class PackageVisitor extends ASTVisitor {
        private String packageName = "";

        public boolean visit(final PackageDeclaration declaration) {
            packageName = declaration.getName().toString();
            return false;
        }

        public String getPackageName() {
            return packageName.isEmpty() ? "default" : packageName;
        }
    }

    private static final class ExtendVisitor extends ASTVisitor {
        public String getName() {
            return name;
        }

        public Type getSuperClass() {
            return superClass;
        }

        public List<Type> getInterfaces() {
            return interfaces;
        }

        private String name = "";
        private Type superClass;
        private List<Type> interfaces = new ArrayList<>();
        private List<String> dependancyNames = new ArrayList<String>();
        private boolean isInterface = false;

        public boolean isInterface() {
            return isInterface;
        }

        public boolean visit(final TypeDeclaration node) {
            // TODO fix this debacle, internal class is getting visited. FIXED
            // BUT LEAVE
            // THIS
            if (name.equals("")) {
                name = node.getName().toString();
                superClass = node.getSuperclassType();
                interfaces = node.superInterfaceTypes();


                DependancyVisitor dependancyVisitor = new DependancyVisitor();
                node.accept(dependancyVisitor);

                for (String s : dependancyVisitor.getDependancyNames()) {
                    if ((!dependancyNames.contains(s))
                            && (!node.getName().toString().equals(s))) {
                        dependancyNames.add(s);
                    }
                }
            }
            if (node.isInterface()) {
                isInterface = true;
            }
            return false;
        }

        public List<String> getDependancyNames() {
            return dependancyNames;
        }

        public void setDependancyNames(final List<String> dependancyNames) {
            this.dependancyNames = dependancyNames;
        }
    }

    private static final class DependancyVisitor extends ASTVisitor {
        private List<String> dependancyNames = new ArrayList<String>();

        public List<String> getDependancyNames() {
            return dependancyNames;
        }

        public void setDependancyNames(final List<String> dependancyNames) {
            this.dependancyNames = dependancyNames;
        }

        public boolean visit(final TypeDeclaration node) {
            return true;
        }

        public boolean visit(final SimpleType node) {
            dependancyNames.add(node.toString());
            return true;
        }

        public boolean visit(final PrimitiveType node) {

            dependancyNames.add(node.toString());
            return true;
        }

        public boolean visit(final ParameterizedType node) {
            dependancyNames.add(node.toString());
            return true;
        }

        public boolean visit(final SimpleName node) {
            dependancyNames.add(node.toString());
            return true;
        }
    }
}
