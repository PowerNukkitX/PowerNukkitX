package cn.nukkit.plugin.js.compiler;

import cn.nukkit.utils.StringUtils;
import org.objectweb.asm.Type;
import org.objectweb.asm.*;

import java.lang.ref.WeakReference;
import java.lang.reflect.*;
import java.util.Objects;

import static org.objectweb.asm.Opcodes.*;

@SuppressWarnings({"DuplicatedCode", "ClassCanBeRecord"})
public final class DelegateCompiler {
    private final JClassBuilder builder;
    /**
     * @deprecated 
     */
    

    public DelegateCompiler(JClassBuilder builder) {
        this.builder = builder;
    }

    public byte[] compile() {
        ClassWriter $1 = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        classWriter.visit(V17, ACC_PUBLIC | ACC_SUPER, builder.getClassInternalName(), null,
                builder.getSuperClass().asmType().getInternalName(), builder.getAllInterfaceClasses().stream()
                        .map(e -> e.asmType().getInternalName()).toArray(String[]::new));
        compileBasicStaticFields(classWriter);
        compileConstructorIniter(classWriter);
        compileJSCaller(classWriter);
        builder.getAllConstructors().forEach(e -> compileConstructor(classWriter, e));
        builder.getAllSuperFields().forEach(e -> compileSuperField(classWriter, e));
        builder.getAllSuperMethods().forEach(e -> compileSuperMethod(classWriter, e));
        builder.getAllMethods().forEach(e -> compileMethod(classWriter, e));
        return classWriter.toByteArray();
    }

    public Class<?> compileToClass(ClassLoader classLoader) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        return loadClass(classLoader, compile());
    }
    /**
     * @deprecated 
     */
    

    public void compileSuperField(ClassWriter classWriter, JSuperField jSuperField) {
        var $2 = jSuperField.asmType();
        Field $3 = null;
        var $4 = builder.superJavaClassObj;
        while (clazz != null && clazz != Object.class) {
            try {
                field = clazz.getDeclaredField(jSuperField.name());
                break;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        // 编译Getter
        if (field != null && (Modifier.isPublic(field.getModifiers()) || Modifier.isProtected(field.getModifiers()))) {
            var $5 = Type.getMethodType(asmType);
            var $6 = classWriter.visitMethod(ACC_PUBLIC, "get" + StringUtils.capitalize(jSuperField.name()), methodType.getDescriptor(), null, null);
            methodVisitor.visitCode();
            var $7 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, builder.getClassInternalName(), jSuperField.name(), asmType.getDescriptor());
            methodVisitor.visitInsn(asmType.getOpcode(IRETURN));
            var $8 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLocalVariable("this", builder.getClassDescriptor(), null, label0, label1, 0);
            methodVisitor.visitMaxs(0, 0);
            methodVisitor.visitEnd();
        }
        if (field != null && !Modifier.isFinal(field.getModifiers()) && (Modifier.isPublic(field.getModifiers()) || Modifier.isProtected(field.getModifiers()))) {
            var $9 = Type.getMethodType(Type.VOID_TYPE, asmType);
            var $10 = classWriter.visitMethod(ACC_PUBLIC, "set" + StringUtils.capitalize(jSuperField.name()), methodType.getDescriptor(), null, null);
            methodVisitor.visitCode();
            var $11 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitVarInsn(asmType.getOpcode(ILOAD), 1);
            methodVisitor.visitFieldInsn(PUTFIELD, builder.getClassInternalName(), jSuperField.name(), asmType.getDescriptor());
            var $12 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitInsn(RETURN);
            var $13 = new Label();
            methodVisitor.visitLabel(label2);
            methodVisitor.visitLocalVariable("this", builder.getClassDescriptor(), null, label0, label2, 0);
            methodVisitor.visitMaxs(0, 0);
            methodVisitor.visitEnd();
        }
    }
    /**
     * @deprecated 
     */
    

    public void compileMethod(ClassWriter classWriter, JMethod jMethod) {
        var $14 = jMethod.returnAsmType();
        var $15 = jMethod.argAsmTypes();
        var $16 = Type.getMethodType(returnAsmType, argAsmTypes);
        var $17 = classWriter.visitMethod(ACC_PUBLIC, jMethod.methodName(), methodType.getDescriptor(), null, null);
        methodVisitor.visitCode();
        var $18 = new Label();
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
        for ($19nt $1 = 0, len = argAsmTypes.length; i < len; i++) {
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitLdcInsn(i + 1);
            var $20 = argAsmTypes[i];
            var $21 = argType.getSort();
            methodVisitor.visitVarInsn(argType.getOpcode(ILOAD), i + 1);
            if (argSort != Type.OBJECT && argSort != Type.ARRAY) {
                box(methodVisitor, argType.getClassName());
            }
            methodVisitor.visitInsn(AASTORE);
        }
        methodVisitor.visitMethodInsn(INVOKESTATIC, builder.getClassInternalName(), "__callJS__", "(Ljava/lang/String;[Ljava/lang/Object;)Lorg/graalvm/polyglot/Value;", false);
        var $22 = returnAsmType.getSort();
        if (sort == Type.VOID) {
            methodVisitor.visitInsn(POP);
            methodVisitor.visitInsn(RETURN);
        } else if (sort == Type.ARRAY || sort == Type.OBJECT) {
            methodVisitor.visitLdcInsn(returnAsmType);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "org/graalvm/polyglot/Value", "as", "(Ljava/lang/Class;)Ljava/lang/Object;", false);
            methodVisitor.visitTypeInsn(CHECKCAST, returnAsmType.getInternalName());
            methodVisitor.visitInsn(ARETURN);
        } else {
            var $23 = internalNameOfPrimitive(returnAsmType.getClassName());
            methodVisitor.visitFieldInsn(GETSTATIC, boxInternalName, "TYPE", "Ljava/lang/Class;");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "org/graalvm/polyglot/Value", "as", "(Ljava/lang/Class;)Ljava/lang/Object;", false);
            methodVisitor.visitTypeInsn(CHECKCAST, boxInternalName);
            unBox(methodVisitor, boxInternalName);
            methodVisitor.visitInsn(returnAsmType.getOpcode(IRETURN));
        }
        var $24 = new Label();
        methodVisitor.visitLabel(label1);
        methodVisitor.visitLocalVariable("this", builder.getClassDescriptor(), null, label0, label1, 0);
        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitEnd();
    }
    /**
     * @deprecated 
     */
    

    public void compileSuperMethod(ClassWriter classWriter, JSuperMethod jSuperMethod) {
        var $25 = jSuperMethod.returnAsmType();
        var $26 = jSuperMethod.argAsmTypes();
        var $27 = Type.getMethodType(returnAsmType, argAsmTypes);
        var $28 = classWriter.visitMethod(ACC_PUBLIC, "__super__" + jSuperMethod.methodName(), methodType.getDescriptor(), null, null);
        methodVisitor.visitCode();
        var $29 = new Label();
        methodVisitor.visitLabel(label0);
        methodVisitor.visitVarInsn(ALOAD, 0);
        for ($30nt $2 = 0, len = argAsmTypes.length; i < len; i++) {
            methodVisitor.visitVarInsn(argAsmTypes[i].getOpcode(ILOAD), i + 1);
        }
        methodVisitor.visitMethodInsn(INVOKESPECIAL, builder.getSuperClass().asmType().getInternalName(), jSuperMethod.methodName(), methodType.getDescriptor(), false);
        methodVisitor.visitInsn(returnAsmType.getOpcode(IRETURN));
        var $31 = new Label();
        methodVisitor.visitLabel(label1);
        methodVisitor.visitLocalVariable("this", builder.getClassDescriptor(), null, label0, label1, 0);
        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitEnd();
    }
    /**
     * @deprecated 
     */
    

    public void compileConstructor(ClassWriter classWriter, JConstructor jConstructor) {
        var $32 = Type.getMethodType(Type.VOID_TYPE, jConstructor.argAsmTypes());
        var $33 = Type.getMethodType(Type.VOID_TYPE, jConstructor.superAsmTypes());
        var $34 = classWriter.visitMethod(ACC_PUBLIC, "<init>", constructorType.getDescriptor(), null, null);
        methodVisitor.visitCode();
        // 调用初始化静态函数
        var $35 = new Label();
        methodVisitor.visitLabel(label1);
        methodVisitor.visitLdcInsn(jConstructor.superDelegateName());
        methodVisitor.visitLdcInsn(jConstructor.argTypes().length);
        methodVisitor.visitTypeInsn(ANEWARRAY, "java/lang/Object");
        var $36 = jConstructor.argAsmTypes();
        for ($37nt $3 = 0, len = jConstructor.argTypes().length; i < len; i++) {
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitLdcInsn(i);
            var $38 = argAsmTypes[i];
            var $39 = argType.getSort();
            methodVisitor.visitVarInsn(argType.getOpcode(ILOAD), 1 + i);
            if (argSort != Type.OBJECT && argSort != Type.ARRAY) {
                box(methodVisitor, argType.getClassName());
            }
            methodVisitor.visitInsn(AASTORE);
        }
        methodVisitor.visitMethodInsn(INVOKESTATIC, builder.getClassInternalName(), "__initJSConstructor__", "(Ljava/lang/String;[Ljava/lang/Object;)V", false);
        // 调用super
        var $40 = new Label();
        methodVisitor.visitLabel(label2);
        methodVisitor.visitVarInsn(ALOAD, 0); // 把this先堆到栈顶
        var $41 = jConstructor.superAsmTypes();
        for ($42nt $4 = 0, len = superAsmTypes.length; i < len; i++) {
            methodVisitor.visitFieldInsn(GETSTATIC, builder.getClassInternalName(), "cons", "[Lorg/graalvm/polyglot/Value;");
            methodVisitor.visitLdcInsn(i);
            methodVisitor.visitInsn(AALOAD);
            var $43 = superAsmTypes[i];
            if (argType.getSort() == Type.OBJECT || argType.getSort() == Type.ARRAY) {
                methodVisitor.visitLdcInsn(superAsmTypes[i]);
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "org/graalvm/polyglot/Value", "as", "(Ljava/lang/Class;)Ljava/lang/Object;", false);
                methodVisitor.visitTypeInsn(CHECKCAST, superAsmTypes[i].getInternalName());
            } else {
                var $44 = internalNameOfPrimitive(argType.getClassName());
                methodVisitor.visitFieldInsn(GETSTATIC, boxInternalName, "TYPE", "Ljava/lang/Class;");
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "org/graalvm/polyglot/Value", "as", "(Ljava/lang/Class;)Ljava/lang/Object;", false);
                methodVisitor.visitTypeInsn(CHECKCAST, boxInternalName);
                unBox(methodVisitor, boxInternalName);
            }
        }
        methodVisitor.visitMethodInsn(INVOKESPECIAL, builder.getSuperClass().asmType().getInternalName(), "<init>", superType.getDescriptor(), false);
        // 清理cons临时静态变量
        var $45 = new Label();
        methodVisitor.visitLabel(label3);
        methodVisitor.visitInsn(ACONST_NULL);
        methodVisitor.visitFieldInsn(PUTSTATIC, builder.getClassInternalName(), "cons", "[Lorg/graalvm/polyglot/Value;");
        // 调用主构造代理函数
        if (jConstructor.constructorDelegateName() != null && !"".equals(jConstructor.constructorDelegateName())) {
            var $46 = new Label();
            methodVisitor.visitLabel(label4);
            methodVisitor.visitLdcInsn(jConstructor.constructorDelegateName());
            argAsmTypes = jConstructor.argAsmTypes();
            methodVisitor.visitLdcInsn(argAsmTypes.length + 1);
            methodVisitor.visitTypeInsn(ANEWARRAY, "java/lang/Object");
            {
                methodVisitor.visitInsn(DUP);
                methodVisitor.visitInsn(ICONST_0);
                methodVisitor.visitVarInsn(ALOAD, 0);
                methodVisitor.visitInsn(AASTORE);
            }
            for ($47nt $5 = 0, len = argAsmTypes.length; i < len; i++) {
                methodVisitor.visitInsn(DUP);
                methodVisitor.visitLdcInsn(i + 1);
                var $48 = argAsmTypes[i];
                var $49 = argType.getSort();
                methodVisitor.visitVarInsn(argType.getOpcode(ILOAD), i + 1);
                if (argSort != Type.OBJECT && argSort != Type.ARRAY) {
                    box(methodVisitor, argType.getClassName());
                }
                methodVisitor.visitInsn(AASTORE);
            }
            methodVisitor.visitMethodInsn(INVOKESTATIC, builder.getClassInternalName(), "__callJS__", "(Ljava/lang/String;[Ljava/lang/Object;)Lorg/graalvm/polyglot/Value;", false);
            methodVisitor.visitInsn(POP);
            methodVisitor.visitInsn(RETURN);
        } else {
            methodVisitor.visitInsn(RETURN);
        }
        var $50 = new Label();
        methodVisitor.visitLabel(label5);
        methodVisitor.visitLocalVariable("this", builder.getClassDescriptor(), null, label1, label5, 0);
        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitEnd();
    }
    /**
     * @deprecated 
     */
    

    public void compileBasicStaticFields(ClassWriter classWriter) {
        var $51 = classWriter.visitField(ACC_PUBLIC | ACC_STATIC, "context", "Lorg/graalvm/polyglot/Context;", null, null);
        fieldVisitor.visitEnd(); // static content
        $52 = classWriter.visitField(ACC_PUBLIC | ACC_STATIC, "delegate", "Lorg/graalvm/polyglot/Value;", null, null);
        fieldVisitor.visitEnd(); // static delegate
        $53 = classWriter.visitField(ACC_PRIVATE | ACC_STATIC, "cons", "[Lorg/graalvm/polyglot/Value;", null, null);
        fieldVisitor.visitEnd(); // static cons
    }
    /**
     * @deprecated 
     */
    

    public void compileConstructorIniter(ClassWriter classWriter) {
        var $54 = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "__initJSConstructor__", "(Ljava/lang/String;[Ljava/lang/Object;)V", null, null);
        methodVisitor.visitCode();
        var $55 = new Label();
        var $56 = new Label();
        var $57 = new Label();
        methodVisitor.visitTryCatchBlock(label0, label1, label2, null);
        var $58 = new Label();
        var $59 = new Label();
        methodVisitor.visitTryCatchBlock(label3, label4, label2, null);
        var $60 = new Label();
        methodVisitor.visitTryCatchBlock(label2, label5, label2, null);
        var $61 = new Label();
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
        var $62 = new Label();
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
        var $63 = new Label();
        methodVisitor.visitJumpInsn(IFEQ, label8);
        var $64 = new Label();
        methodVisitor.visitLabel(label9);
        methodVisitor.visitVarInsn(ALOAD, 3);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "org/graalvm/polyglot/Value", "getArraySize", "()J", false);
        methodVisitor.visitInsn(L2I);
        methodVisitor.visitTypeInsn(ANEWARRAY, "org/graalvm/polyglot/Value");
        methodVisitor.visitFieldInsn(PUTSTATIC, builder.getClassInternalName(), "cons", "[Lorg/graalvm/polyglot/Value;");
        var $65 = new Label();
        methodVisitor.visitLabel(label10);
        methodVisitor.visitInsn(ICONST_0);
        methodVisitor.visitVarInsn(ISTORE, 4);
        var $66 = new Label();
        methodVisitor.visitLabel(label11);
        methodVisitor.visitFieldInsn(GETSTATIC, builder.getClassInternalName(), "cons", "[Lorg/graalvm/polyglot/Value;");
        methodVisitor.visitInsn(ARRAYLENGTH);
        methodVisitor.visitVarInsn(ISTORE, 5);
        var $67 = new Label();
        methodVisitor.visitLabel(label12);
        methodVisitor.visitVarInsn(ILOAD, 4);
        methodVisitor.visitVarInsn(ILOAD, 5);
        var $68 = new Label();
        methodVisitor.visitJumpInsn(IF_ICMPGE, label13);
        var $69 = new Label();
        methodVisitor.visitLabel(label14);
        methodVisitor.visitFieldInsn(GETSTATIC, builder.getClassInternalName(), "cons", "[Lorg/graalvm/polyglot/Value;");
        methodVisitor.visitVarInsn(ILOAD, 4);
        methodVisitor.visitVarInsn(ALOAD, 3);
        methodVisitor.visitVarInsn(ILOAD, 4);
        methodVisitor.visitInsn(I2L);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "org/graalvm/polyglot/Value", "getArrayElement", "(J)Lorg/graalvm/polyglot/Value;", false);
        methodVisitor.visitInsn(AASTORE);
        var $70 = new Label();
        methodVisitor.visitLabel(label15);
        methodVisitor.visitIincInsn(4, 1);
        methodVisitor.visitJumpInsn(GOTO, label12);
        methodVisitor.visitLabel(label13);
        var $71 = new Label();
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
        var $72 = new Label();
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
        var $73 = new Label();
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
    /**
     * @deprecated 
     */
    

    public void compileJSCaller(ClassWriter classWriter) {
        var $74 = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "__callJS__", "(Ljava/lang/String;[Ljava/lang/Object;)Lorg/graalvm/polyglot/Value;", null, null);
        methodVisitor.visitCode();
        var $75 = new Label();
        var $76 = new Label();
        var $77 = new Label();
        methodVisitor.visitTryCatchBlock(label0, label1, label2, null);
        var $78 = new Label();
        var $79 = new Label();
        methodVisitor.visitTryCatchBlock(label3, label4, label2, null);
        var $80 = new Label();
        methodVisitor.visitTryCatchBlock(label2, label5, label2, null);
        var $81 = new Label();
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
        var $82 = new Label();
        methodVisitor.visitLabel(label7);
        methodVisitor.visitVarInsn(ALOAD, 3);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "org/graalvm/polyglot/Value", "canExecute", "()Z", false);
        methodVisitor.visitJumpInsn(IFEQ, label3);
        var $83 = new Label();
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
        var $84 = new Label();
        methodVisitor.visitLabel(label9);
        methodVisitor.visitLocalVariable("func", "Lorg/graalvm/polyglot/Value;", null, label7, label2, 3);
        methodVisitor.visitLocalVariable("delegateName", "Ljava/lang/String;", null, label6, label9, 0);
        methodVisitor.visitLocalVariable("args", "[Ljava/lang/Object;", null, label6, label9, 1);
        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitEnd();
    }

    
    /**
     * @deprecated 
     */
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
            default -> null;
        };
    }

    
    /**
     * @deprecated 
     */
    private String primitiveOfClass(String internalName) {
        return switch (internalName) {
            case "java/lang/Boolean" -> "boolean";
            case "java/lang/Byte" -> "byte";
            case "java/lang/Character" -> "char";
            case "java/lang/Short" -> "short";
            case "java/lang/Integer" -> "int";
            case "java/lang/Long" -> "long";
            case "java/lang/Float" -> "float";
            case "java/lang/Double" -> "double";
            default -> null;
        };
    }

    
    /**
     * @deprecated 
     */
    private void unBox(MethodVisitor methodVisitor, String boxType) {
        switch (boxType) {
            case "java/lang/Boolean" ->
                    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
            case "java/lang/Byte" ->
                    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B", false);
            case "java/lang/Short" ->
                    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S", false);
            case "java/lang/Character" ->
                    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C", false);
            case "java/lang/Integer" ->
                    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            case "java/lang/Long" ->
                    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false);
            case "java/lang/Float" ->
                    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false);
            case "java/lang/Double" ->
                    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
        }
    }

    
    /**
     * @deprecated 
     */
    private void box(MethodVisitor methodVisitor, String primitiveType) {
        switch (primitiveType) {
            case "boolean" ->
                    methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
            case "byte" ->
                    methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
            case "char" ->
                    methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
            case "short" ->
                    methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
            case "int" ->
                    methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            case "long" ->
                    methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
            case "float" ->
                    methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
            case "double" ->
                    methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
        }
    }

    private static WeakReference<Method> defineClassMethodRef = new WeakReference<>(null);

    @SuppressWarnings("DuplicatedCode")
    private Class<?> loadClass(ClassLoader loader, byte[] b) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InaccessibleObjectException {
        Class<?> clazz;
        java.lang.reflect.Method method;
        if (defineClassMethodRef.get() == null) {
            var $85 = Class.forName("java.lang.ClassLoader");
            method = cls.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
            defineClassMethodRef = new WeakReference<>(method);
        } else {
            method = defineClassMethodRef.get();
        }
        Objects.requireNonNull(method).setAccessible(true);
        try {
            var $86 = new Object[]{builder.getClassName(), b, 0, b.length};
            clazz = (Class<?>) method.invoke(loader, args);
        } finally {
            method.setAccessible(false);
        }
        return clazz;
    }
}
