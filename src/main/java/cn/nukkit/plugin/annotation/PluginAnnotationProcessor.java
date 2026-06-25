package cn.nukkit.plugin.annotation;

import cn.nukkit.plugin.PluginLoadOrder;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Compile-time processor for the PNX plugin annotations.
 * <ol>
 *     <li>{@link PluginMeta} on the main class produces a {@code powernukkitx.yml}
 *     descriptor in the plugin jar.</li>
 *     <li>{@link EventListener} types are registered with the plugin manager.</li>
 *     <li>{@link ScheduleTask} classes and methods are scheduled with the server
 *     scheduler.</li>
 * </ol>
 * The listener registrations and task schedules are emitted into a single
 * generated {@code PNXPluginBootstrap} class whose {@code init(Plugin)} method
 * the server invokes automatically when the plugin is enabled (its name is
 * recorded under the {@code bootstrap} key of the generated descriptor), so the
 * author never has to call it.
 */
@SupportedAnnotationTypes({
        "cn.nukkit.plugin.annotation.PluginMeta",
        "cn.nukkit.plugin.annotation.EventListener",
        "cn.nukkit.plugin.annotation.ScheduleTask",
        "cn.nukkit.plugin.annotation.CommandDefinition"
})
public class PluginAnnotationProcessor extends AbstractProcessor {
    private static final String BOOTSTRAP_CLASS = "PNXPluginBootstrap";

    private Elements elements;
    private Types types;
    private Messager messager;
    private Filer filer;

    /** Generated once; guards against re-emission in later processing rounds. */
    private boolean generated = false;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        this.elements = env.getElementUtils();
        this.types = env.getTypeUtils();
        this.messager = env.getMessager();
        this.filer = env.getFiler();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return false;
        }

        List<String> listeners = new ArrayList<>();
        List<ClassTask> classTasks = new ArrayList<>();
        List<MethodTask> methodTasks = new ArrayList<>();
        List<CommandEntry> commands = new ArrayList<>();

        for (Element e : roundEnv.getElementsAnnotatedWith(EventListener.class)) {
            collectListener(e, listeners);
        }
        for (Element e : roundEnv.getElementsAnnotatedWith(ScheduleTask.class)) {
            collectTask(e, classTasks, methodTasks);
        }
        for (Element e : roundEnv.getElementsAnnotatedWith(CommandDefinition.class)) {
            collectCommand(e, commands);
        }

        Set<? extends Element> metas = roundEnv.getElementsAnnotatedWith(PluginMeta.class);
        if (metas.isEmpty() || generated) {
            return false;
        }
        if (metas.size() > 1) {
            for (Element m : metas) {
                error(m, "Only one type may be annotated with @PluginMeta per project; found "
                        + metas.size() + ".");
            }
            return false;
        }

        TypeElement main = (TypeElement) metas.iterator().next();
        if (!validateMain(main)) {
            return false;
        }

        generated = true;
        generate(main, listeners, classTasks, methodTasks, commands);
        return false;
    }

    private void collectListener(Element e, List<String> out) {
        if (e.getKind() != ElementKind.CLASS) {
            error(e, "@EventListener may only be placed on a class.");
            return;
        }
        TypeElement type = (TypeElement) e;
        if (!isConcreteInstantiable(type, "@EventListener")) {
            return;
        }
        if (!isAssignableTo(type, "cn.nukkit.event.Listener")) {
            error(e, "@EventListener class must implement cn.nukkit.event.Listener.");
            return;
        }
        out.add(type.getQualifiedName().toString());
    }

    private void collectTask(Element e, List<ClassTask> classOut, List<MethodTask> methodOut) {
        ScheduleTask cfg = e.getAnnotation(ScheduleTask.class);
        if (e.getKind() == ElementKind.CLASS) {
            TypeElement type = (TypeElement) e;
            if (!isConcreteInstantiable(type, "@ScheduleTask")) {
                return;
            }
            if (!isAssignableTo(type, "java.lang.Runnable")) {
                error(e, "@ScheduleTask class must implement Runnable (e.g. extend cn.nukkit.scheduler.Task).");
                return;
            }
            classOut.add(new ClassTask(type.getQualifiedName().toString(), cfg));
        } else if (e.getKind() == ElementKind.METHOD) {
            ExecutableElement method = (ExecutableElement) e;
            Set<Modifier> mods = method.getModifiers();
            if (!mods.contains(Modifier.PUBLIC) || !mods.contains(Modifier.STATIC)) {
                error(e, "@ScheduleTask method must be public and static.");
                return;
            }
            if (!method.getParameters().isEmpty()) {
                error(e, "@ScheduleTask method must take no parameters.");
                return;
            }
            String owner = ((TypeElement) method.getEnclosingElement()).getQualifiedName().toString();
            methodOut.add(new MethodTask(owner, method.getSimpleName().toString(), cfg));
        } else {
            error(e, "@ScheduleTask may only be placed on a class or a public static method.");
        }
    }

    private void collectCommand(Element e, List<CommandEntry> out) {
        if (e.getKind() != ElementKind.CLASS) {
            error(e, "@CommandDefinition may only be placed on a class.");
            return;
        }
        TypeElement type = (TypeElement) e;
        if (!isConcreteInstantiable(type, "@CommandDefinition")) {
            return;
        }
        if (!isAssignableTo(type, "cn.nukkit.command.Command")) {
            error(e, "@CommandDefinition class must extend cn.nukkit.command.Command.");
            return;
        }
        out.add(new CommandEntry(type.getQualifiedName().toString(), e.getAnnotation(CommandDefinition.class)));
    }

    private boolean validateMain(TypeElement main) {
        if (main.getKind() != ElementKind.CLASS) {
            error(main, "@PluginMeta may only be placed on a class.");
            return false;
        }
        if (!isAssignableTo(main, "cn.nukkit.plugin.PluginBase")) {
            error(main, "@PluginMeta class must extend cn.nukkit.plugin.PluginBase.");
            return false;
        }
        return true;
    }

    /** Concrete class with an accessible no-argument constructor. */
    private boolean isConcreteInstantiable(TypeElement type, String label) {
        if (type.getModifiers().contains(Modifier.ABSTRACT)) {
            error(type, label + " class must not be abstract.");
            return false;
        }
        if (type.getNestingKind().isNested() && !type.getModifiers().contains(Modifier.STATIC)) {
            error(type, label + " nested class must be static so it can be instantiated.");
            return false;
        }
        boolean sawCtor = false;
        for (Element enclosed : type.getEnclosedElements()) {
            if (enclosed.getKind() != ElementKind.CONSTRUCTOR) {
                continue;
            }
            ExecutableElement ctor = (ExecutableElement) enclosed;
            if (ctor.getParameters().isEmpty()) {
                if (ctor.getModifiers().contains(Modifier.PRIVATE)) {
                    error(type, label + " class must have a non-private no-argument constructor.");
                    return false;
                }
                return true;
            }
            sawCtor = true;
        }
        if (sawCtor) {
            error(type, label + " class must have a no-argument constructor.");
            return false;
        }
        return true; // Only the implicit default constructor exists
    }

    private boolean isAssignableTo(TypeElement type, String superFqn) {
        TypeElement superType = elements.getTypeElement(superFqn);
        if (superType == null) {
            return false;
        }
        TypeMirror erasedSuper = types.erasure(superType.asType());
        return types.isAssignable(types.erasure(type.asType()), erasedSuper);
    }

    private void generate(TypeElement main, List<String> listeners,
                          List<ClassTask> classTasks, List<MethodTask> methodTasks,
                          List<CommandEntry> commands) {
        String pkg = elements.getPackageOf(main).getQualifiedName().toString();
        String bootstrapFqn = pkg.isEmpty() ? BOOTSTRAP_CLASS : pkg + "." + BOOTSTRAP_CLASS;
        String mainBinary = elements.getBinaryName(main).toString();

        writeBootstrap(main, pkg, bootstrapFqn, listeners, classTasks, methodTasks, commands);
        writeDescriptor(main, mainBinary, bootstrapFqn);
    }

    private void writeBootstrap(TypeElement main, String pkg, String bootstrapFqn,
                                List<String> listeners, List<ClassTask> classTasks,
                                List<MethodTask> methodTasks, List<CommandEntry> commands) {
        StringBuilder sb = new StringBuilder();
        if (!pkg.isEmpty()) {
            sb.append("package ").append(pkg).append(";\n\n");
        }
        sb.append("// AUTO-GENERATED by PNX-APT. Do not edit.\n");
        sb.append("public final class ").append(BOOTSTRAP_CLASS).append(" {\n");
        sb.append("    private ").append(BOOTSTRAP_CLASS).append("() {}\n\n");
        sb.append("    public static void init(cn.nukkit.plugin.Plugin plugin) {\n");
        if (!listeners.isEmpty()) {
            sb.append("        cn.nukkit.plugin.PluginManager __pm = plugin.getServer().getPluginManager();\n");
        }
        if (!classTasks.isEmpty() || !methodTasks.isEmpty()) {
            sb.append("        cn.nukkit.scheduler.ServerScheduler __sch = plugin.getServer().getScheduler();\n");
        }
        for (String listener : listeners) {
            sb.append("        __pm.registerEvents(new ").append(listener).append("(), plugin);\n");
        }
        for (ClassTask task : classTasks) {
            sb.append("        ").append(scheduleCall("new " + task.fqn + "()", task.cfg)).append('\n');
        }
        for (MethodTask task : methodTasks) {
            sb.append("        ").append(scheduleCall("() -> " + task.owner + "." + task.method + "()", task.cfg))
                    .append('\n');
        }
        if (!commands.isEmpty()) {
            sb.append("        cn.nukkit.command.CommandMap __cmap = plugin.getServer().getCommandMap();\n");
            sb.append("        String __prefix = plugin.getDescription().getName();\n");
            for (CommandEntry cmd : commands) {
                CommandDefinition cfg = cmd.cfg;
                sb.append("        {\n");
                sb.append("            ").append(cmd.fqn).append(" __cmd = new ").append(cmd.fqn).append("();\n");
                sb.append("            __cmd.setName(").append(javaString(cfg.name())).append(");\n");
                if (cfg.aliases().length > 0) {
                    sb.append("            __cmd.setAliases(").append(javaStringArray(cfg.aliases())).append(");\n");
                }
                if (!cfg.permission().isEmpty()) {
                    sb.append("            __cmd.setPermission(").append(javaString(cfg.permission())).append(");\n");
                }
                if (!cfg.description().isEmpty()) {
                    sb.append("            __cmd.setDescription(").append(javaString(cfg.description())).append(");\n");
                }
                if (!cfg.usage().isEmpty()) {
                    sb.append("            __cmd.setUsage(").append(javaString(cfg.usage())).append(");\n");
                }
                switch (cfg.commandMode()) {
                    case COMMAND_TREE -> sb.append("            __cmd.enableCommandTree();\n");
                    case PARAM_TREE -> sb.append("            __cmd.enableParamTree();\n");
                    case RAW -> {}
                }
                sb.append("            __cmap.register(__prefix, __cmd);\n");
                sb.append("        }\n");
            }
        }
        sb.append("    }\n");
        sb.append("}\n");

        try {
            JavaFileObject src = filer.createSourceFile(bootstrapFqn, main);
            try (Writer w = src.openWriter()) {
                w.write(sb.toString());
            }
        } catch (IOException ex) {
            error(main, "Failed to generate " + bootstrapFqn + ": " + ex.getMessage());
        }
    }

    private static String scheduleCall(String runnableExpr, ScheduleTask cfg) {
        if (cfg.period() > 0) {
            return "__sch.scheduleDelayedRepeatingTask(plugin, " + runnableExpr + ", "
                    + cfg.delay() + ", " + cfg.period() + ", " + cfg.async() + ");";
        }
        return "__sch.scheduleDelayedTask(plugin, " + runnableExpr + ", "
                + cfg.delay() + ", " + cfg.async() + ");";
    }

    private void writeDescriptor(TypeElement main, String mainBinary, String bootstrapFqn) {
        PluginMeta meta = main.getAnnotation(PluginMeta.class);
        StringBuilder y = new StringBuilder();
        scalar(y, "name", meta.name());
        scalar(y, "main", mainBinary);
        scalar(y, "version", meta.version());
        list(y, "api", meta.api());
        scalar(y, "bootstrap", bootstrapFqn);
        if (meta.authors().length > 0) {
            list(y, "authors", meta.authors());
        }
        if (!meta.description().isEmpty()) {
            scalar(y, "description", meta.description());
        }
        if (!meta.website().isEmpty()) {
            scalar(y, "website", meta.website());
        }
        if (!meta.prefix().isEmpty()) {
            scalar(y, "prefix", meta.prefix());
        }
        // POSTWORLD is the default load order and is omitted from the descriptor.
        if (meta.order() != PluginLoadOrder.POSTWORLD) {
            y.append("load: ").append(meta.order().name()).append('\n');
        }
        if (meta.depend().length > 0) {
            list(y, "depend", meta.depend());
        }
        if (meta.softDepend().length > 0) {
            list(y, "softdepend", meta.softDepend());
        }
        if (meta.loadBefore().length > 0) {
            list(y, "loadbefore", meta.loadBefore());
        }
        if (meta.features().length > 0) {
            list(y, "features", meta.features());
        }

        try {
            FileObject yml = filer.createResource(StandardLocation.CLASS_OUTPUT, "", "powernukkitx.yml", main);
            try (Writer w = yml.openWriter()) {
                w.write(y.toString());
            }
        } catch (IOException ex) {
            error(main, "Failed to generate powernukkitx.yml: " + ex.getMessage());
        }
    }

    private static void scalar(StringBuilder y, String key, String value) {
        y.append(key).append(": ").append(quote(value)).append('\n');
    }

    private static void list(StringBuilder y, String key, String[] values) {
        y.append(key).append(":\n");
        for (String v : values) {
            y.append("  - ").append(quote(v)).append('\n');
        }
    }

    /** YAML double-quoted scalar; escapes backslash and double-quote. */
    private static String quote(String s) {
        return '"' + s.replace("\\", "\\\\").replace("\"", "\\\"") + '"';
    }

    /** Java source string literal; escapes backslash, double-quote and newlines. */
    private static String javaString(String s) {
        return '"' + s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r") + '"';
    }

    private static String javaStringArray(String[] values) {
        StringBuilder sb = new StringBuilder("new String[]{");
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(javaString(values[i]));
        }
        return sb.append('}').toString();
    }

    private void error(Element e, String message) {
        messager.printMessage(Diagnostic.Kind.ERROR, message, e);
    }

    private record ClassTask(String fqn, ScheduleTask cfg) {
    }

    private record MethodTask(String owner, String method, ScheduleTask cfg) {
    }

    private record CommandEntry(String fqn, CommandDefinition cfg) {
    }
}
