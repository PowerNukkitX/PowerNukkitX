package cn.nukkit.plugin.js.compiler;

import cn.nukkit.plugin.CommonJSPlugin;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class JClassBuilder {
    private final Context jsContext;
    private Value delegate;
    private String className;
    private JType superClass = JType.of(Object.class);
    private final List<JType> interfaceClasses;
    private final List<JConstructor> jConstructors;
    private final List<JMethod> jMethods;
    private final List<JSuperMethod> jSuperMethods;
    private final List<JSuperField> jSuperFields;

    Class<?> superJavaClassObj = Object.class;

    public JClassBuilder(CommonJSPlugin jsPlugin) {
        this.jsContext = jsPlugin.getJsContext();
        this.jConstructors = new ArrayList<>();
        this.interfaceClasses = new ArrayList<>();
        this.jMethods = new ArrayList<>();
        this.jSuperMethods = new ArrayList<>();
        this.jSuperFields = new ArrayList<>();
    }

    public JClassBuilder(Context jsContext) {
        this.jsContext = jsContext;
        this.jConstructors = new ArrayList<>();
        this.interfaceClasses = new ArrayList<>();
        this.jMethods = new ArrayList<>();
        this.jSuperMethods = new ArrayList<>();
        this.jSuperFields = new ArrayList<>();
    }

    public Context getJsContext() {
        return jsContext;
    }

    public String getClassName() {
        return className;
    }

    public String getClassInternalName() {
        return className.replace(".", "/");
    }

    public String getClassDescriptor() {
        return "L" + className.replace(".", "/") + ";";
    }

    public JClassBuilder setClassName(String className) {
        this.className = className;
        return this;
    }

    public JClassBuilder addConstructor(JConstructor constructor) {
        jConstructors.add(constructor);
        return this;
    }

    public JClassBuilder clearConstructor() {
        jConstructors.clear();
        return this;
    }

    public List<JConstructor> getAllConstructors() {
        return Collections.unmodifiableList(jConstructors);
    }

    public JClassBuilder setSuperClass(JType superClass) {
        this.superClass = superClass;
        try {
            this.superJavaClassObj = Class.forName(superClass.asmType().getClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return this;
    }

    public JType getSuperClass() {
        return superClass;
    }

    public JClassBuilder addInterfaceClass(JType interfaceClass) {
        interfaceClasses.add(interfaceClass);
        return this;
    }

    public JClassBuilder clearInterfaceNames() {
        interfaceClasses.clear();
        return this;
    }

    public List<JType> getAllInterfaceClasses() {
        return Collections.unmodifiableList(interfaceClasses);
    }

    public JClassBuilder addMethod(JMethod method) {
        jMethods.add(method);
        return this;
    }

    public JClassBuilder clearMethods() {
        jMethods.clear();
        return this;
    }

    public List<JMethod> getAllMethods() {
        return jMethods;
    }

    public JClassBuilder addSuperMethod(JSuperMethod superMethod) {
        this.jSuperMethods.add(superMethod);
        return this;
    }

    public JClassBuilder clearSuperMethods() {
        this.jSuperMethods.clear();
        return this;
    }

    public List<JSuperMethod> getAllSuperMethods() {
        return jSuperMethods;
    }

    public JClassBuilder addSuperField(JSuperField superField) {
        this.jSuperFields.add(superField);
        return this;
    }

    public JClassBuilder clearSuperFields() {
        this.jSuperFields.clear();
        return this;
    }

    public List<JSuperField> getAllSuperFields() {
        return jSuperFields;
    }

    public JClassBuilder setDelegate(Value delegate) {
        this.delegate = delegate;
        return this;
    }

    public Value getDelegate() {
        return delegate;
    }

    private Class<?> injectDelegate(Class<?> clazz) {
        try {
            var contextField = clazz.getField("context");
            var delegateField = clazz.getField("delegate");
            contextField.set(null, jsContext);
            delegateField.set(null, delegate);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return clazz;
    }

    public Class<?> compileToClass() throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        return injectDelegate(new DelegateCompiler(this).compileToClass(Thread.currentThread().getContextClassLoader()));
    }

    public void compileToFile(String path) {
        compileToFile(Path.of(path));
    }

    public void compileToFile(Path path) {
        try {
            Files.write(path, new DelegateCompiler(this).compile(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
