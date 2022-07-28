package cn.nukkit.plugin.js.compiler;

import org.objectweb.asm.*;

import java.lang.ref.WeakReference;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

import static org.objectweb.asm.Opcodes.*;

public final class DelegateCompiler {
    private final JClassBuilder builder;

    public DelegateCompiler(JClassBuilder builder) {
        this.builder = builder;
    }

    public byte[] compile() {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        FieldVisitor fieldVisitor;
        RecordComponentVisitor recordComponentVisitor;
        MethodVisitor methodVisitor;
        classWriter.visit(V17, ACC_PUBLIC | ACC_SUPER, builder.getClassInternalName(), null,
                builder.getSuperClass().asmType().getInternalName(), builder.getAllInterfaceClasses().stream()
                        .map(e -> e.asmType().getInternalName()).toArray(String[]::new));
        compileBasicStaticFields(classWriter);
        compileConstructorIniter(classWriter);
        builder.getAllConstructors().forEach(e -> compileConstructor(classWriter, e));
        return classWriter.toByteArray();
    }

    public Class<?> compileToClass(ClassLoader classLoader) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        return loadClass(classLoader, compile());
    }

    public void compileConstructor(ClassWriter classWriter, JConstructor jConstructor) {
        var constructorType = Type.getMethodType(Type.VOID_TYPE, jConstructor.argAsmTypes());
        var superType = Type.getMethodType(Type.VOID_TYPE, jConstructor.superAsmTypes());
        var methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", constructorType.getDescriptor(), null, null);
        methodVisitor.visitCode();
        // 调用初始化静态函数
        var label1 = new Label();
        methodVisitor.visitLabel(label1);
        methodVisitor.visitLdcInsn(jConstructor.delegateName());
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
        methodVisitor.visitInsn(RETURN);
        Label label4 = new Label();
        methodVisitor.visitLabel(label4);
        methodVisitor.visitLocalVariable("this", builder.getClassDescriptor(), null, label1, label4, 0);
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
        var methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_STATIC, "__initJSConstructor__", "(Ljava/lang/String;[Ljava/lang/Object;)V", null, null);
        methodVisitor.visitCode();
        var label0 = new Label();
        var label1 = new Label();
        var label2 = new Label();
        methodVisitor.visitTryCatchBlock(label0, label1, label2, null);
        var label3 = new Label();
        methodVisitor.visitTryCatchBlock(label2, label3, label2, null);
        var label4 = new Label();
        methodVisitor.visitLabel(label4);
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
        var label5 = new Label();
        methodVisitor.visitLabel(label5);
        methodVisitor.visitVarInsn(ALOAD, 3);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "org/graalvm/polyglot/Value", "getArraySize", "()J", false);
        methodVisitor.visitInsn(L2I);
        methodVisitor.visitTypeInsn(ANEWARRAY, "org/graalvm/polyglot/Value");
        methodVisitor.visitFieldInsn(PUTSTATIC, builder.getClassInternalName(), "cons", "[Lorg/graalvm/polyglot/Value;");
        var label6 = new Label();
        methodVisitor.visitLabel(label6);
        methodVisitor.visitInsn(ICONST_0);
        methodVisitor.visitVarInsn(ISTORE, 4);
        var label7 = new Label();
        methodVisitor.visitLabel(label7);
        methodVisitor.visitFieldInsn(GETSTATIC, builder.getClassInternalName(), "cons", "[Lorg/graalvm/polyglot/Value;");
        methodVisitor.visitInsn(ARRAYLENGTH);
        methodVisitor.visitVarInsn(ISTORE, 5);
        var label8 = new Label();
        methodVisitor.visitLabel(label8);
        methodVisitor.visitFrame(Opcodes.F_FULL, 6, new Object[]{"java/lang/String", "[Ljava/lang/Object;", "java/lang/Object", "org/graalvm/polyglot/Value", Opcodes.INTEGER, Opcodes.INTEGER}, 0, new Object[]{});
        methodVisitor.visitVarInsn(ILOAD, 4);
        methodVisitor.visitVarInsn(ILOAD, 5);
        var label9 = new Label();
        methodVisitor.visitJumpInsn(IF_ICMPGE, label9);
        var label10 = new Label();
        methodVisitor.visitLabel(label10);
        methodVisitor.visitFieldInsn(GETSTATIC, builder.getClassInternalName(), "cons", "[Lorg/graalvm/polyglot/Value;");
        methodVisitor.visitVarInsn(ILOAD, 4);
        methodVisitor.visitVarInsn(ALOAD, 3);
        methodVisitor.visitVarInsn(ILOAD, 4);
        methodVisitor.visitInsn(I2L);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "org/graalvm/polyglot/Value", "getArrayElement", "(J)Lorg/graalvm/polyglot/Value;", false);
        methodVisitor.visitInsn(AASTORE);
        var label11 = new Label();
        methodVisitor.visitLabel(label11);
        methodVisitor.visitIincInsn(4, 1);
        methodVisitor.visitJumpInsn(GOTO, label8);
        methodVisitor.visitLabel(label9);
        methodVisitor.visitFrame(Opcodes.F_CHOP, 3, null, 0, null);
        methodVisitor.visitVarInsn(ALOAD, 2);
        methodVisitor.visitInsn(MONITOREXIT);
        methodVisitor.visitLabel(label1);
        var label12 = new Label();
        methodVisitor.visitJumpInsn(GOTO, label12);
        methodVisitor.visitLabel(label2);
        methodVisitor.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/Throwable"});
        methodVisitor.visitVarInsn(ASTORE, 6);
        methodVisitor.visitVarInsn(ALOAD, 2);
        methodVisitor.visitInsn(MONITOREXIT);
        methodVisitor.visitLabel(label3);
        methodVisitor.visitVarInsn(ALOAD, 6);
        methodVisitor.visitInsn(ATHROW);
        methodVisitor.visitLabel(label12);
        methodVisitor.visitFrame(Opcodes.F_CHOP, 1, null, 0, null);
        methodVisitor.visitInsn(RETURN);
        var label13 = new Label();
        methodVisitor.visitLabel(label13);
        methodVisitor.visitLocalVariable("i", "I", null, label7, label9, 4);
        methodVisitor.visitLocalVariable("len", "I", null, label8, label9, 5);
        methodVisitor.visitLocalVariable("tmp", "Lorg/graalvm/polyglot/Value;", null, label5, label9, 3);
        methodVisitor.visitLocalVariable("delegateName", "Ljava/lang/String;", null, label4, label13, 0);
        methodVisitor.visitLocalVariable("args", "[Ljava/lang/Object;", null, label4, label13, 1);
        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitEnd();
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
