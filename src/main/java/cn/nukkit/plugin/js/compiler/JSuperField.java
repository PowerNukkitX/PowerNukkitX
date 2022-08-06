package cn.nukkit.plugin.js.compiler;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Type;

public record JSuperField(JClassBuilder builder, String name, @NotNull JType type) {
    @NotNull
    public Type asmType() {
        return type.asmType();
    }
}
