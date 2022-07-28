package cn.nukkit.plugin.js.compiler;

import cn.nukkit.plugin.CommonJSPlugin;
import lombok.SneakyThrows;
import org.graalvm.polyglot.Context;
import org.junit.jupiter.api.Test;

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
    public void constructorCompile() {
        var builder = new JClassBuilder((Context) null)
                .setClassName("ConstructorTestClass");
        builder.addConstructor(new JConstructor(builder, "super_0_js", new JType[0], JType.of(String.class)));
        builder.compileToFile(Path.of("./target/ConstructorTestClass.class"));
        var clazz = builder.compileToClass();
        System.out.println(clazz);
    }
}
