package cn.nukkit.plugin.js.compiler;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Type;

import java.util.Arrays;

public record JConstructor(JClassBuilder builder, String superDelegateName, String constructorDelegateName,
                           JType[] superTypes, JType... argTypes) {

    public JConstructor(JClassBuilder builder, JType[] superTypes, JType... argTypes) {
        this(builder, "new", "constructor", superTypes, argTypes);
    }

    @NotNull
    public Type[] argAsmTypes() {
        return Arrays.stream(this.argTypes()).map(JType::asmType).toArray(Type[]::new);
    }

    @NotNull
    public Type[] superAsmTypes() {
        return Arrays.stream(this.superTypes()).map(JType::asmType).toArray(Type[]::new);
    }
}
