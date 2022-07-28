package cn.nukkit.plugin.js.compiler;

import lombok.SneakyThrows;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

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
                .setDelegate(execJS(ctx, "simpleConstructorCompile.js", "var a = {testSuper: () => {print('Hello world!');return [];}};a"))
                .setClassName("SimpleConstructorTestClass");
        System.out.println(builder.getDelegate().getMember("testSuper"));
        builder.addConstructor(new JConstructor(builder, "testSuper", new JType[0]));
        builder.compileToFile(Path.of("./target/SimpleConstructorTestClass.class"));
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
