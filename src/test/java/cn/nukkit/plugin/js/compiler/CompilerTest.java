package cn.nukkit.plugin.js.compiler;

import cn.nukkit.item.customitem.ItemCustom;
import lombok.SneakyThrows;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

public class CompilerTest {
    @SneakyThrows
    @Test
    public void simpleCompile() {
        var builder = new JClassBuilder((Context) null)
                .setClassName("SimpleTestClass");
        var clazz = builder.compileToClass();
        System.out.println(clazz);
    }

    @SneakyThrows
    @Test
    public void simpleConstructorCompile() {
        var ctx = makeContext();
        var builder = new JClassBuilder(ctx)
                .setDelegate(execJS(ctx, "simpleConstructorCompile.js", "var a = {testSuper: () => {print('Hello world!');}};a"))
                .setClassName("SimpleConstructorTestClass");
        builder.addConstructor(new JConstructor(builder, "testSuper", "", new JType[0]));
        builder.compileToFile(Path.of("./target/SimpleConstructorTestClass.class"));
        var clazz = builder.compileToClass();
        System.out.println(clazz);
        var obj = clazz.getConstructor().newInstance();
        System.out.println(obj);
    }

    @SneakyThrows
    @Test
    public void oneArgConstructorCompile() {
        var ctx = makeContext();
        var builder = new JClassBuilder(ctx)
                .setDelegate(execJS(ctx, "oneArgConstructorCompile.js", """
                        var a = {testSuper: () => {
                            print('oneArgConstructorCompile!');
                            return ["test:test_item", "JUnit Test Item"];
                        }};
                        a
                        """))
                .setSuperClass(JType.of(ItemCustom.class))
                .setClassName("OneArgConstructorTestClass");
        builder.addConstructor(new JConstructor(builder, "testSuper", "",
                new JType[]{JType.of(String.class), JType.of(String.class)}));
        builder.compileToFile(Path.of("./target/OneArgConstructorTestClass.class"));
        var clazz = builder.compileToClass();
        System.out.println(clazz);
        var obj = clazz.getConstructor().newInstance();
        System.out.println(obj);
    }

    @SneakyThrows
    @Test
    public void delegateConstructorCompile() {
        var ctx = makeContext();
        var builder = new JClassBuilder(ctx)
                .setDelegate(execJS(ctx, "delegateConstructorCompile.js", "var a = {testSuper: () => {}, constructor: (self) => print(self + ' From Delegate!')};a"))
                .setClassName("DelegateConstructorTestClass");
        builder.addConstructor(new JConstructor(builder, "testSuper", "constructor", new JType[0]));
        builder.compileToFile(Path.of("./target/DelegateConstructorTestClass.class"));
        var clazz = builder.compileToClass();
        System.out.println(clazz);
        var obj = clazz.getConstructor().newInstance();
        System.out.println(obj);
    }

    @SneakyThrows
    @Test
    public void unBoxConstructorCompile() {
        var ctx = makeContext();
        var builder = new JClassBuilder(ctx)
                .setDelegate(execJS(ctx, "unBoxConstructorCompile.js", """
                        var a = {testSuper: () => {
                            print('unBoxConstructorCompile!');
                            return 114514;
                        }};
                        a
                        """))
                .setSuperClass(JType.of(AtomicInteger.class))
                .setClassName("UnBoxConstructorTestClass");
        builder.addConstructor(new JConstructor(builder, "testSuper", "",
                new JType[]{JType.of(int.class)}));
        builder.compileToFile(Path.of("./target/UnBoxConstructorTestClass.class"));
        var clazz = builder.compileToClass();
        System.out.println(clazz);
        var obj = clazz.getConstructor().newInstance();
        System.out.println(obj);
    }

    @SneakyThrows
    @Test
    public void simpleMethodCompile() {
        var ctx = makeContext();
        var builder = new JClassBuilder(ctx)
                .setDelegate(execJS(ctx, "simpleMethodCompile.js", """
                        var a = {testSuper: () => {
                            print('simpleMethodCompile!');
                        },
                        add: (javaThis, a, b) => a + b};
                        a
                        """))
                .setClassName("SimpleMethodTestClass");
        builder.addConstructor(new JConstructor(builder, "testSuper", "",
                        new JType[]{}))
                .addMethod(new JMethod(builder, "add", "", JType.of(int.class), JType.of(Integer.class), JType.of(long.class)));
        builder.compileToFile(Path.of("./target/SimpleMethodTestClass.class"));
        var clazz = builder.compileToClass();
        System.out.println(clazz);
        var obj = clazz.getConstructor().newInstance();
        System.out.println(obj);
        var method = clazz.getMethod("add", Integer.class, long.class);
        Assertions.assertEquals(114514, method.invoke(obj, 114000, 514L));
    }

    @SneakyThrows
    @Test
    public void superMethodCompile() {
        var ctx = makeContext();
        var builder = new JClassBuilder(ctx)
                .setDelegate(execJS(ctx, "superMethodCompile.js", """
                        var a = {testSuper: () => {
                            print('superMethodCompile!');
                            return 114513;
                        },
                        incGet(javaThis) {
                            return javaThis.__super__incrementAndGet();
                        }};
                        a
                        """))
                .setSuperClass(JType.of(AtomicInteger.class))
                .setClassName("SuperMethodTestClass");
        builder.addConstructor(new JConstructor(builder, "testSuper", "",
                        new JType[]{JType.of(int.class)}))
                .addSuperMethod(new JSuperMethod(builder, "incrementAndGet", JType.of(int.class)))
                .addMethod(new JMethod(builder, "incGet", JType.of(int.class)));
        builder.compileToFile(Path.of("./target/SuperMethodTestClass.class"));
        var clazz = builder.compileToClass();
        System.out.println(clazz);
        var obj = clazz.getConstructor().newInstance();
        var method = clazz.getMethod("incGet");
        System.out.println(method.invoke(obj));
    }

    @SneakyThrows
    @Test
    public void superFieldCompile() {
        var ctx = makeContext();
        var builder = new JClassBuilder(ctx)
                .setDelegate(execJS(ctx, "superFieldCompile.js", """
                        var a = {new: () => {
                            return ["test:test_item", "JUnit Test Item"];
                        },
                        constructor(javaThis) {
                            print(javaThis.meta);
                            javaThis.setMeta(114514);
                            print(javaThis.meta);
                        }};
                        a
                        """))
                .setSuperClass(JType.of(ItemCustom.class))
                .setClassName("SuperFieldTestClass");
        builder.addConstructor(new JConstructor(builder, "new", "constructor",
                new JType[]{JType.of(String.class), JType.of(String.class)}));
        builder.addSuperField(new JSuperField(builder, "meta", JType.of(int.class)));
        builder.compileToFile(Path.of("./target/SuperFieldTestClass.class"));
        var clazz = builder.compileToClass();
        System.out.println(clazz);
        var obj = clazz.getConstructor().newInstance();
        System.out.println(obj);
    }

    private Context makeContext() {
        return Context.newBuilder("js")
                .allowAllAccess(true)
                .allowHostAccess(HostAccess.ALL)
                .allowHostClassLoading(true)
                .allowHostClassLookup(className -> true)
                .allowIO(true)
                .allowExperimentalOptions(true)
                .option("js.esm-eval-returns-exports", "true")
                .option("js.shared-array-buffer", "true")
                .option("js.foreign-object-prototype", "true")
                .option("js.nashorn-compat", "true")
                .option("js.ecmascript-version", "13")
                .option("engine.WarnInterpreterOnly", "false")
                .build();
    }

    public Value execJS(Context context, String name, String jsCode) {
        try {
            return context.eval(Source.newBuilder("js", jsCode, name).build());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
