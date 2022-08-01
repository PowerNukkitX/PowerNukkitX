package cn.nukkit.plugin.js.compiler;

import org.objectweb.asm.*;

import java.lang.ref.WeakReference;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

import static org.objectweb.asm.Opcodes.*;

@SuppressWarnings("DuplicatedCode")
public final class DelegateCompiler {
    private final JClassBuilder builder;

    public DelegateCompiler(JClassBuilder builder) {
        this.builder = builder;
    }

    public byte[] compile() {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        classWriter.visit(V17, ACC_PUBLIC | ACC_SUPER, builder.getClassInternalName(), null,
                builder.getSuperClass().asmType().getInternalName(), builder.getAllInterfaceClasses().stream()
                        .map(e -> e.asmType().getInternalName()).toArray(String[]::new));
        compileBasicStaticFields(classWriter);
        compileConstructorIniter(classWriter);
        compileJSCaller(classWriter);
        builder.getAllConstructors().forEach(e -> compileConstructor(classWriter, e));
        builder.getAllMethods().forEach(e -> compileMethod(classWriter, e));
        return classWriter.toByteArray();
    }

    public Class<?> compileToClass(ClassLoader classLoader) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        return loadClass(classLoader, compile());
    }

    public void compileMethod(ClassWriter classWriter, JMethod jMethod) {
        var returnAsmType = jMethod.returnAsmType();
        var argAsmTypes = jMethod.argAsmTypes();
        var methodType = Type.getMethodType(returnAsmType, argAsmTypes);
        var methodVisitor = classWriter.visitMethod(ACC_PUBLIC, jMethod.methodName(), methodType.getDescriptor(), null, null);
        methodVisitor.visitCode();
        var label0 = new Label();
        methodVisitor.visitLabel(label0);
        methodVisitor.visitLdcInsn(jMethod.methodName());
        methodVisitor.visitLdcInsn(argAsmTypes.length + 1);
        methodVisitor.visitTypeInsn(ANEWARRAY, "java/lang/Object");
        {
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitInsn(ICONST_0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitInsn(AASTORE);
        }
        for (int i = 0, len = argAsmTypes.length; i < len; i++) {
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitLdcInsn(i + 1);
            methodVisitor.visitVarInsn(ALOAD, i + 1);
            methodVisitor.visitInsn(AASTORE);
        }
        methodVisitor.visitMethodInsn(INVOKESTATIC, builder.getClassInternalName(), "__callJS__", "(Ljava/lang/String;[Ljava/lang/Object;)Lorg/graalvm/polyglot/Value;", false);
        var sort = returnAsmType.getSort();
        if (sort == Type.VOID) {
            methodVisitor.visitInsn(POP);
            methodVisitor.visitInsn(RETURN);
        } else if (sort == Type.ARRAY || sort == Type.OBJECT) {
            methodVisitor.visitLdcInsn(returnAsmType);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "org/graalvm/polyglot/Value", "as", "(Ljava/lang/Class;)Ljava/lang/Object;", false);
            methodVisitor.visitTypeInsn(CHECKCAST, returnAsmType.getInternalName());
            methodVisitor.visitInsn(ARETURN);
        } else {
            var boxInternalName = internalNameOfPrimitive(returnAsmType.getClassName());
            methodVisitor.visitFieldInsn(GETSTATIC, boxInternalName, "TYPE", "Ljava/lang/Class;");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "org/graalvm/polyglot/Value", "as", "(Ljava/lang/Class;)Ljava/lang/Object;", false);
            methodVisitor.visitTypeInsn(CHECKCAST, boxInternalName);
            unBox(methodVisitor, boxInternalName);
            methodVisitor.visitInsn(returnAsmType.getOpcode(IRETURN));
        }
        var label1 = new Label();
        methodVisitor.visitLabel(label1);
        methodVisitor.visitLocalVariable("this", builder.getClassDescriptor(), null, label0, label1, 0);
        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitEnd();
    }

    public void compileConstructor(ClassWriter classWriter, JConstructor jConstructor) {
        var constructorType = Type.getMethodType(Type.VOID_TYPE, jConstructor.argAsmTypes());
        var superType = Type.getMethodType(Type.VOID_TYPE, jConstructor.superAsmTypes());
        var methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", constructorType.getDescriptor(), null, null);
        methodVisitor.visitCode();
        // 调用初始化静态函数
        var label1 = new Label();
        methodVisitor.visitLabel(label1);
        methodVisitor.visitLdcInsn(jConstructor.superDelegateName());
        methodVisitor.visitLdcInsn(jConstructor.argTypes().length);
        methodVisitor.visitTypeInsn(ANEWARRAY, "java/lang/Object");
        for (int i = 0, len = jConstructor.argTypes().length; i < len; i++) {
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitLdcInsn(i);
            methodVisitor.visitVarInsn(ALOAD, 1 + i);
            methodVisitor.visitInsn(AASTORE);
        }
        methodVisitor.visitMethodInsn(INVOKESTATIC, builder.getClassInternalName(), "__initJSConstructor__", "(Ljava/lang/String;[Ljava/lang/Object;)V", false);
        // 调用super
        var label2 = new Label();
        methodVisitor.visitLabel(label2);
        methodVisitor.visitVarInsn(ALOAD, 0); // 把this先堆到栈顶
        var superAsmTypes = jConstructor.superAsmTypes();
        for (int i = 0, len = superAsmTypes.length; i < len; i++) {
            methodVisitor.visitFieldInsn(GETSTATIC, builder.getClassInternalName(), "cons", "[Lorg/graalvm/polyglot/Value;");
            methodVisitor.visitLdcInsn(i);
            methodVisitor.visitInsn(AALOAD);
            methodVisitor.visitLdcInsn(superAsmTypes[i]);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "org/graalvm/polyglot/Value", "as", "(Ljava/lang/Class;)Ljava/lang/Object;", false);
            methodVisitor.visitTypeInsn(CHECKCAST, superAsmTypes[i].getInternalName());
        }
        methodVisitor.visitMethodInsn(INVOKESPECIAL, builder.getSuperClass().asmType().getInternalName(), "<init>", superType.getDescriptor(), false);
        // 清理cons临时静态变量
        var label3 = new Label();
        methodVisitor.visitLabel(label3);
        methodVisitor.visitInsn(ACONST_NULL);
        methodVisitor.visitFieldInsn(PUTSTATIC, builder.getClassInternalName(), "cons", "[Lorg/graalvm/polyglot/Value;");
        // 调用主构造代理函数
        if (jConstructor.constructorDelegateName() != null && !"".equals(jConstructor.constructorDelegateName())) {
            var label4 = new Label();
            methodVisitor.visitLabel(label4);
            methodVisitor.visitLdcInsn(jConstructor.constructorDelegateName());
            var argAsmTypes = jConstructor.argAsmTypes();
            methodVisitor.visitLdcInsn(argAsmTypes.length + 1);
            methodVisitor.visitTypeInsn(ANEWARRAY, "java/lang/Object");
            {
                methodVisitor.visitInsn(DUP);
                methodVisitor.visitInsn(ICONST_0);
                methodVisitor.visitVarInsn(ALOAD, 0);
                methodVisitor.visitInsn(AASTORE);
            }
            for (int i = 0, len = argAsmTypes.length; i < len; i++) {
                methodVisitor.visitInsn(DUP);
                methodVisitor.visitLdcInsn(i + 1);
                methodVisitor.visitVarInsn(ALOAD, i + 1);
                methodVisitor.visitInsn(AASTORE);
            }
            methodVisitor.visitMethodInsn(INVOKESTATIC, builder.getClassInternalName(), "__callJS__", "(Ljava/lang/String;[Ljava/lang/Object;)Lorg/graalvm/polyglot/Value;", false);
            methodVisitor.visitInsn(POP);
            methodVisitor.visitInsn(RETURN);
        } else {
            methodVisitor.visitInsn(RETURN);
        }
        var label5 = new Label();
        methodVisitor.visitLabel(label5);
        methodVisitor.visitLocalVariable("this", builder.getClassDescriptor(), null, label1, label5, 0);
        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitEnd();
    }

    public void compileBasicStaticFields(ClassWriter classWriter) {
        var fieldVisitor = classWriter.visitField(ACC_PUBLIC | ACC_STATIC, "context", "Lorg/graalvm/polyglot/Context;", null, null);
        fieldVisitor.visitEnd(); // static content
        fieldVisitor = classWriter.visitField(ACC_PUBLIC | ACC_STATIC, "delegate", "Lorg/graalvm/polyglot/Value;", null, null);
        fieldVisitor.visitEnd(); // static delegate
        fieldVisitor = classWriter.visitField(ACC_PRIVATE | ACC_STATIC, "cons", "[Lorg/graalvm/polyglot/Value;", null, null);
        fieldVisitor.visitEnd(); // static cons
    }

    public void compileConstructorIniter(ClassWriter classWriter) {
        var methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "__initJSConstructor__", "(Ljava/lang/String;[Ljava/lang/Object;)V", null, null);
        methodVisitor.visitCode();
        var label0 = new Label();
        var label1 = new Label();
        var label2 = new Label();
        methodVisitor.visitTryCatchBlock(label0, label1, label2, null);
        var label3 = new Label();
        var label4 = new Label();
        methodVisitor.visitTryCatchBlock(label3, label4, label2, null);
        var label5 = new Label();
        methodVisitor.visitTryCatchBlock(label2, label5, label2, null);
        var label6 = new Label();
        methodVisitor.visitLabel(label6);
        methodVisitor.visitFieldInsn(GETSTATIC, builder.getClassInternalName(), "context", "Lorg/graalvm/polyglot/Context;");
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitVarInsn(ASTORE, 2);
        methodVisitor.visitInsn(MONITORENTER);
        methodVisitor.visitLabel(label0);
        methodVisitor.visitFieldInsn(GETSTATIC, builder.getClassInternalName(), "delegate", "Lorg/graalvm/polyglot/Value;");
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "org/graalvm/polyglot/Value", "getMember", "(Ljava/lang/String;)Lorg/graalvm/polyglot/Value;", false);
        methodVisitor.visitVarInsn(ALOAD, 1);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "org/graalvm/polyglot/Value", "execute", "([Ljava/lang/Object;)Lorg/graalvm/polyglot/Value;", false);
        methodVisitor.visitVarInsn(ASTORE, 3);
        var label7 = new Label();
        methodVisitor.visitLabel(label7);
        methodVisitor.visitVarInsn(ALOAD, 3);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "org/graalvm/polyglot/Value", "isNull", "()Z", false);
        methodVisitor.visitJumpInsn(IFEQ, label3);
        methodVisitor.visitVarInsn(ALOAD, 2);
        methodVisitor.visitInsn(MONITOREXIT);
        methodVisitor.visitLabel(label1);
        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitLabel(label3);
        methodVisitor.visitFrame(Opcodes.F_APPEND, 2, new Object[]{"java/lang/Object", "org/graalvm/polyglot/Value"}, 0, null);
        methodVisitor.visitVarInsn(ALOAD, 3);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "org/graalvm/polyglot/Value", "hasArrayElements", "()Z", false);
        var label8 = new Label();
        methodVisitor.visitJumpInsn(IFEQ, label8);
        var label9 = new Label();
        methodVisitor.visitLabel(label9);
        methodVisitor.visitVarInsn(ALOAD, 3);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "org/graalvm/polyglot/Value", "getArraySize", "()J", false);
        methodVisitor.visitInsn(L2I);
        methodVisitor.visitTypeInsn(ANEWARRAY, "org/graalvm/polyglot/Value");
        methodVisitor.visitFieldInsn(PUTSTATIC, builder.getClassInternalName(), "cons", "[Lorg/graalvm/polyglot/Value;");
        var label10 = new Label();
        methodVisitor.visitLabel(label10);
        methodVisitor.visitInsn(ICONST_0);
        methodVisitor.visitVarInsn(ISTORE, 4);
        var label11 = new Label();
        methodVisitor.visitLabel(label11);
        methodVisitor.visitFieldInsn(GETSTATIC, builder.getClassInternalName(), "cons", "[Lorg/graalvm/polyglot/Value;");
        methodVisitor.visitInsn(ARRAYLENGTH);
        methodVisitor.visitVarInsn(ISTORE, 5);
        var label12 = new Label();
        methodVisitor.visitLabel(label12);
        methodVisitor.visitVarInsn(ILOAD, 4);
        methodVisitor.visitVarInsn(ILOAD, 5);
        var label13 = new Label();
        methodVisitor.visitJumpInsn(IF_ICMPGE, label13);
        var label14 = new Label();
        methodVisitor.visitLabel(label14);
        methodVisitor.visitFieldInsn(GETSTATIC, builder.getClassInternalName(), "cons", "[Lorg/graalvm/polyglot/Value;");
        methodVisitor.visitVarInsn(ILOAD, 4);
        methodVisitor.visitVarInsn(ALOAD, 3);
        methodVisitor.visitVarInsn(ILOAD, 4);
        methodVisitor.visitInsn(I2L);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "org/graalvm/polyglot/Value", "getArrayElement", "(J)Lorg/graalvm/polyglot/Value;", false);
        methodVisitor.visitInsn(AASTORE);
        var label15 = new Label();
        methodVisitor.visitLabel(label15);
        methodVisitor.visitIincInsn(4, 1);
        methodVisitor.visitJumpInsn(GOTO, label12);
        methodVisitor.visitLabel(label13);
        var label16 = new Label();
        methodVisitor.visitJumpInsn(GOTO, label16);
        methodVisitor.visitLabel(label8);
        methodVisitor.visitInsn(ICONST_1);
        methodVisitor.visitTypeInsn(ANEWARRAY, "org/graalvm/polyglot/Value");
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitInsn(ICONST_0);
        methodVisitor.visitVarInsn(ALOAD, 3);
        methodVisitor.visitInsn(AASTORE);
        methodVisitor.visitFieldInsn(PUTSTATIC, builder.getClassInternalName(), "cons", "[Lorg/graalvm/polyglot/Value;");
        methodVisitor.visitLabel(label16);
        methodVisitor.visitVarInsn(ALOAD, 2);
        methodVisitor.visitInsn(MONITOREXIT);
        methodVisitor.visitLabel(label4);
        var label17 = new Label();
        methodVisitor.visitJumpInsn(GOTO, label17);
        methodVisitor.visitLabel(label2);
        methodVisitor.visitVarInsn(ASTORE, 6);
        methodVisitor.visitVarInsn(ALOAD, 2);
        methodVisitor.visitInsn(MONITOREXIT);
        methodVisitor.visitLabel(label5);
        methodVisitor.visitVarInsn(ALOAD, 6);
        methodVisitor.visitInsn(ATHROW);
        methodVisitor.visitLabel(label17);
        methodVisitor.visitInsn(RETURN);
        var label18 = new Label();
        methodVisitor.visitLabel(label18);
        // 加上局部变量名称，免得某些反编译器暴毙
        methodVisitor.visitLocalVariable("i", "I", null, label11, label13, 4);
        methodVisitor.visitLocalVariable("len", "I", null, label12, label13, 5);
        methodVisitor.visitLocalVariable("tmp", "Lorg/graalvm/polyglot/Value;", null, label7, label16, 3);
        methodVisitor.visitLocalVariable("delegateName", "Ljava/lang/String;", null, label6, label18, 0);
        methodVisitor.visitLocalVariable("args", "[Ljava/lang/Object;", null, label6, label18, 1);
        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitEnd();
    }

    public void compileJSCaller(ClassWriter classWriter) {
        var methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "__callJS__", "(Ljava/lang/String;[Ljava/lang/Object;)Lorg/graalvm/polyglot/Value;", null, null);
        methodVisitor.visitCode();
        var label0 = new Label();
        var label1 = new Label();
        var label2 = new Label();
        methodVisitor.visitTryCatchBlock(label0, label1, label2, null);
        var label3 = new Label();
        var label4 = new Label();
        methodVisitor.visitTryCatchBlock(label3, label4, label2, null);
        var label5 = new Label();
        methodVisitor.visitTryCatchBlock(label2, label5, label2, null);
        var label6 = new Label();
        methodVisitor.visitLabel(label6);
        methodVisitor.visitFieldInsn(GETSTATIC, builder.getClassInternalName(), "context", "Lorg/graalvm/polyglot/Context;");
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitVarInsn(ASTORE, 2);
        methodVisitor.visitInsn(MONITORENTER);
        methodVisitor.visitLabel(label0);
        methodVisitor.visitFieldInsn(GETSTATIC, builder.getClassInternalName(), "delegate", "Lorg/graalvm/polyglot/Value;");
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "org/graalvm/polyglot/Value", "getMember", "(Ljava/lang/String;)Lorg/graalvm/polyglot/Value;", false);
        methodVisitor.visitVarInsn(ASTORE, 3);
        var label7 = new Label();
        methodVisitor.visitLabel(label7);
        methodVisitor.visitVarInsn(ALOAD, 3);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "org/graalvm/polyglot/Value", "canExecute", "()Z", false);
        methodVisitor.visitJumpInsn(IFEQ, label3);
        var label8 = new Label();
        methodVisitor.visitLabel(label8);
        methodVisitor.visitVarInsn(ALOAD, 3);
        methodVisitor.visitVarInsn(ALOAD, 1);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "org/graalvm/polyglot/Value", "execute", "([Ljava/lang/Object;)Lorg/graalvm/polyglot/Value;", false);
        methodVisitor.visitVarInsn(ALOAD, 2);
        methodVisitor.visitInsn(MONITOREXIT);
        methodVisitor.visitLabel(label1);
        methodVisitor.visitInsn(ARETURN);
        methodVisitor.visitLabel(label3);
        methodVisitor.visitInsn(ACONST_NULL);
        methodVisitor.visitMethodInsn(INVOKESTATIC, "org/graalvm/polyglot/Value", "asValue", "(Ljava/lang/Object;)Lorg/graalvm/polyglot/Value;", false);
        methodVisitor.visitVarInsn(ALOAD, 2);
        methodVisitor.visitInsn(MONITOREXIT);
        methodVisitor.visitLabel(label4);
        methodVisitor.visitInsn(ARETURN);
        methodVisitor.visitLabel(label2);
        methodVisitor.visitVarInsn(ASTORE, 4);
        methodVisitor.visitVarInsn(ALOAD, 2);
        methodVisitor.visitInsn(MONITOREXIT);
        methodVisitor.visitLabel(label5);
        methodVisitor.visitVarInsn(ALOAD, 4);
        methodVisitor.visitInsn(ATHROW);
        var label9 = new Label();
        methodVisitor.visitLabel(label9);
        methodVisitor.visitLocalVariable("func", "Lorg/graalvm/polyglot/Value;", null, label7, label2, 3);
        methodVisitor.visitLocalVariable("delegateName", "Ljava/lang/String;", null, label6, label9, 0);
        methodVisitor.visitLocalVariable("args", "[Ljava/lang/Object;", null, label6, label9, 1);
        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitEnd();
    }

    private String internalNameOfPrimitive(String primitiveType) {
        return switch (primitiveType) {
            case "boolean" -> "java/lang/Boolean";
            case "byte" -> "java/lang/Byte";
            case "char" -> "java/lang/Character";
            case "short" -> "java/lang/Short";
            case "int" -> "java/lang/Integer";
            case "long" -> "java/lang/Long";
            case "float" -> "java/lang/Float";
            case "double" -> "java/lang/Double";
            default -> throw new IllegalArgumentException("Unknown primitive type: " + primitiveType);
        };
    }

    private void unBox(MethodVisitor methodVisitor, String boxType) {
        switch (boxType) {
            case "java/lang/Boolean" -> methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
            case "java/lang/Byte" -> methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B", false);
            case "java/lang/Short" -> methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S", false);
            case "java/lang/Character" -> methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C", false);
            case "java/lang/Integer" -> methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            case "java/lang/Long" -> methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false);
            case "java/lang/Float" -> methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false);
            case "java/lang/Double" -> methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
        }
    }

    private static WeakReference<Method> defineClassMethodRef = new WeakReference<>(null);

    @SuppressWarnings("DuplicatedCode")
    private Class<?> loadClass(ClassLoader loader, byte[] b) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InaccessibleObjectException {
        Class<?> clazz;
        java.lang.reflect.Method method;
        if (defineClassMethodRef.get() == null) {
            var cls = Class.forName("java.lang.ClassLoader");
            method = cls.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
            defineClassMethodRef = new WeakReference<>(method);
        } else {
            method = defineClassMethodRef.get();
        }
        Objects.requireNonNull(method).setAccessible(true);
        try {
            var args = new Object[]{builder.getClassName(), b, 0, b.length};
            clazz = (Class<?>) method.invoke(loader, args);
        } finally {
            method.setAccessible(false);
        }
        return clazz;
    }
}
