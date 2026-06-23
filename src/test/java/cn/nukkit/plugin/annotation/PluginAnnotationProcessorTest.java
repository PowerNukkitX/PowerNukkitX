package cn.nukkit.plugin.annotation;

import cn.nukkit.Server;
import cn.nukkit.command.SimpleCommandMap;
import cn.nukkit.event.Listener;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginDescription;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.scheduler.ServerScheduler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Compiles real source through {@link PluginAnnotationProcessor} in-process (JDK
 * {@link JavaCompiler} + an in-memory file manager) and asserts on diagnostics
 * and on the generated {@code plugin.yml} / {@code PNXPluginBootstrap} sources.
 * <p>
 * The compilation runs on the test classpath, so the real {@code cn.nukkit}
 * types ({@code PluginBase}, {@code Listener}, {@code Task}, {@code Command},
 * ...) are resolved exactly as a plugin build would resolve them.
 */
public class PluginAnnotationProcessorTest {

    // A valid, minimal main plugin class used by tests that only care about the
    // other annotations.
    private static final JavaSource MAIN = new JavaSource("demo.DemoPlugin", """
            package demo;
            import cn.nukkit.plugin.PluginBase;
            import cn.nukkit.plugin.annotation.PluginMeta;
            @PluginMeta(name = "Demo", version = "1.0.0", api = {"1.0.0"})
            public class DemoPlugin extends PluginBase {}
            """);

    // ------------------------------------------------------------------
    // @PluginMeta -> plugin.yml
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("@PluginMeta / plugin.yml")
    class Meta {

        @Test
        void minimalDescriptorHasRequiredKeysAndBootstrap() {
            Result r = compile(MAIN);
            r.assertSuccess();
            String yml = r.pluginYml();
            assertNotNull(yml, "plugin.yml should be generated");
            assertContains(yml, "name: \"Demo\"");
            assertContains(yml, "main: \"demo.DemoPlugin\"");
            assertContains(yml, "version: \"1.0.0\"");
            assertContains(yml, "api:\n  - \"1.0.0\"");
            assertContains(yml, "bootstrap: \"demo.PNXPluginBootstrap\"");
        }

        @Test
        void defaultLoadOrderIsOmitted() {
            Result r = compile(MAIN);
            r.assertSuccess();
            assertFalse(r.pluginYml().contains("load:"), "POSTWORLD default must be omitted");
        }

        @Test
        void optionalFieldsAreEmittedAndStartupOrderIsWritten() {
            Result r = compile(new JavaSource("demo.DemoPlugin", """
                    package demo;
                    import cn.nukkit.plugin.PluginBase;
                    import cn.nukkit.plugin.PluginLoadOrder;
                    import cn.nukkit.plugin.annotation.PluginMeta;
                    @PluginMeta(
                        name = "Demo",
                        version = "2.0",
                        api = {"1.0.0", "1.1.0"},
                        authors = {"Alice", "Bob"},
                        description = "A demo",
                        website = "https://example.com",
                        prefix = "DEMO",
                        order = PluginLoadOrder.STARTUP,
                        depend = {"Core"},
                        softDepend = {"Econ"},
                        loadBefore = {"Other"},
                        features = {"flagA", "flagB"}
                    )
                    public class DemoPlugin extends PluginBase {}
                    """));
            r.assertSuccess();
            String yml = r.pluginYml();
            assertContains(yml, "version: \"2.0\"");
            assertContains(yml, "api:\n  - \"1.0.0\"\n  - \"1.1.0\"");
            assertContains(yml, "authors:\n  - \"Alice\"\n  - \"Bob\"");
            assertContains(yml, "description: \"A demo\"");
            assertContains(yml, "website: \"https://example.com\"");
            assertContains(yml, "prefix: \"DEMO\"");
            assertContains(yml, "load: STARTUP");
            assertContains(yml, "depend:\n  - \"Core\"");
            assertContains(yml, "softdepend:\n  - \"Econ\"");
            assertContains(yml, "loadbefore:\n  - \"Other\"");
            assertContains(yml, "features:\n  - \"flagA\"\n  - \"flagB\"");
        }

        @Test
        void emptyOptionalFieldsAreOmitted() {
            Result r = compile(MAIN);
            r.assertSuccess();
            String yml = r.pluginYml();
            assertFalse(yml.contains("authors:"), "empty authors omitted");
            assertFalse(yml.contains("description:"), "empty description omitted");
            assertFalse(yml.contains("website:"), "empty website omitted");
            assertFalse(yml.contains("prefix:"), "empty prefix omitted");
            assertFalse(yml.contains("depend:"), "empty depend omitted");
            assertFalse(yml.contains("features:"), "empty features omitted");
        }

        @Test
        void backslashInValueIsEscaped() {
            // Annotation value is a\b (single backslash); yml must escape to a\\b.
            Result r = compile(new JavaSource("demo.DemoPlugin", """
                    package demo;
                    import cn.nukkit.plugin.PluginBase;
                    import cn.nukkit.plugin.annotation.PluginMeta;
                    @PluginMeta(name = "Demo", version = "1", api = {"1"}, description = "a\\\\b")
                    public class DemoPlugin extends PluginBase {}
                    """));
            r.assertSuccess();
            assertContains(r.pluginYml(), "description: \"a\\\\b\"");
        }

        @Test
        void mainNotExtendingPluginBaseIsAnError() {
            Result r = compile(new JavaSource("demo.DemoPlugin", """
                    package demo;
                    import cn.nukkit.plugin.annotation.PluginMeta;
                    @PluginMeta(name = "Demo", version = "1", api = {"1"})
                    public class DemoPlugin {}
                    """));
            r.assertFailureContains("must extend cn.nukkit.plugin.PluginBase");
            assertNull(r.pluginYml(), "no descriptor on validation failure");
        }

        @Test
        void duplicatePluginMetaIsAnError() {
            Result r = compile(
                    new JavaSource("demo.A", """
                            package demo;
                            import cn.nukkit.plugin.PluginBase;
                            import cn.nukkit.plugin.annotation.PluginMeta;
                            @PluginMeta(name = "A", version = "1", api = {"1"})
                            public class A extends PluginBase {}
                            """),
                    new JavaSource("demo.B", """
                            package demo;
                            import cn.nukkit.plugin.PluginBase;
                            import cn.nukkit.plugin.annotation.PluginMeta;
                            @PluginMeta(name = "B", version = "1", api = {"1"})
                            public class B extends PluginBase {}
                            """));
            r.assertFailureContains("Only one type may be annotated with @PluginMeta");
        }

        @Test
        void noPluginMetaSkipsGenerationWithoutError() {
            // A lone valid listener and nothing else: no main, so nothing is
            // generated, but it must not be an error (keeps incremental builds sane).
            Result r = compile(new JavaSource("demo.L", """
                    package demo;
                    import cn.nukkit.event.Listener;
                    import cn.nukkit.plugin.annotation.EventListener;
                    @EventListener
                    public class L implements Listener {}
                    """));
            r.assertSuccess();
            assertNull(r.pluginYml());
            assertNull(r.bootstrap());
        }
    }

    // ------------------------------------------------------------------
    // @EventListener
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("@EventListener")
    class Listeners {

        @Test
        void validListenerIsRegistered() {
            Result r = compile(MAIN, new JavaSource("demo.MyListener", """
                    package demo;
                    import cn.nukkit.event.Listener;
                    import cn.nukkit.plugin.annotation.EventListener;
                    @EventListener
                    public class MyListener implements Listener {}
                    """));
            r.assertSuccess();
            assertContains(r.bootstrap(), "__pm.registerEvents(new demo.MyListener(), plugin);");
        }

        @Test
        void listenerThroughIndirectInterfaceIsAccepted() {
            Result r = compile(MAIN, new JavaSource("demo.MyListener", """
                    package demo;
                    import cn.nukkit.event.Listener;
                    import cn.nukkit.plugin.annotation.EventListener;
                    interface Mid extends Listener {}
                    @EventListener
                    public class MyListener implements Mid {}
                    """));
            r.assertSuccess();
            assertContains(r.bootstrap(), "new demo.MyListener()");
        }

        @Test
        void notImplementingListenerIsAnError() {
            Result r = compile(MAIN, new JavaSource("demo.MyListener", """
                    package demo;
                    import cn.nukkit.plugin.annotation.EventListener;
                    @EventListener
                    public class MyListener {}
                    """));
            r.assertFailureContains("must implement cn.nukkit.event.Listener");
        }

        @Test
        void abstractListenerIsAnError() {
            Result r = compile(MAIN, new JavaSource("demo.MyListener", """
                    package demo;
                    import cn.nukkit.event.Listener;
                    import cn.nukkit.plugin.annotation.EventListener;
                    @EventListener
                    public abstract class MyListener implements Listener {}
                    """));
            r.assertFailureContains("must not be abstract");
        }

        @Test
        void privateNoArgConstructorIsAnError() {
            Result r = compile(MAIN, new JavaSource("demo.MyListener", """
                    package demo;
                    import cn.nukkit.event.Listener;
                    import cn.nukkit.plugin.annotation.EventListener;
                    @EventListener
                    public class MyListener implements Listener {
                        private MyListener() {}
                    }
                    """));
            r.assertFailureContains("non-private no-argument constructor");
        }

        @Test
        void onlyParameterizedConstructorIsAnError() {
            Result r = compile(MAIN, new JavaSource("demo.MyListener", """
                    package demo;
                    import cn.nukkit.event.Listener;
                    import cn.nukkit.plugin.annotation.EventListener;
                    @EventListener
                    public class MyListener implements Listener {
                        public MyListener(int x) {}
                    }
                    """));
            r.assertFailureContains("must have a no-argument constructor");
        }
    }

    // ------------------------------------------------------------------
    // @ScheduleTask
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("@ScheduleTask")
    class Tasks {

        @Test
        void repeatingClassTaskUsesRepeatingSchedule() {
            Result r = compile(MAIN, new JavaSource("demo.MyTask", """
                    package demo;
                    import cn.nukkit.scheduler.Task;
                    import cn.nukkit.plugin.annotation.ScheduleTask;
                    @ScheduleTask(delay = 5, period = 20, async = true)
                    public class MyTask extends Task {
                        @Override public void onRun(int t) {}
                    }
                    """));
            r.assertSuccess();
            assertContains(r.bootstrap(),
                    "__sch.scheduleDelayedRepeatingTask(plugin, new demo.MyTask(), 5, 20, true);");
        }

        @Test
        void oneShotClassTaskUsesDelayedSchedule() {
            Result r = compile(MAIN, new JavaSource("demo.MyTask", """
                    package demo;
                    import cn.nukkit.scheduler.Task;
                    import cn.nukkit.plugin.annotation.ScheduleTask;
                    @ScheduleTask(delay = 40)
                    public class MyTask extends Task {
                        @Override public void onRun(int t) {}
                    }
                    """));
            r.assertSuccess();
            assertContains(r.bootstrap(),
                    "__sch.scheduleDelayedTask(plugin, new demo.MyTask(), 40, false);");
        }

        @Test
        void plainRunnableClassIsAccepted() {
            Result r = compile(MAIN, new JavaSource("demo.MyTask", """
                    package demo;
                    import cn.nukkit.plugin.annotation.ScheduleTask;
                    @ScheduleTask(period = 10)
                    public class MyTask implements Runnable {
                        @Override public void run() {}
                    }
                    """));
            r.assertSuccess();
            assertContains(r.bootstrap(), "new demo.MyTask(), 0, 10, false);");
        }

        @Test
        void staticMethodTaskSchedulesLambda() {
            Result r = compile(MAIN, new JavaSource("demo.Jobs", """
                    package demo;
                    import cn.nukkit.plugin.annotation.ScheduleTask;
                    public class Jobs {
                        @ScheduleTask(delay = 100, period = 200, async = true)
                        public static void tick() {}
                    }
                    """));
            r.assertSuccess();
            assertContains(r.bootstrap(),
                    "__sch.scheduleDelayedRepeatingTask(plugin, () -> demo.Jobs.tick(), 100, 200, true);");
        }

        @Test
        void nonRunnableClassIsAnError() {
            Result r = compile(MAIN, new JavaSource("demo.MyTask", """
                    package demo;
                    import cn.nukkit.plugin.annotation.ScheduleTask;
                    @ScheduleTask
                    public class MyTask {}
                    """));
            r.assertFailureContains("must implement Runnable");
        }

        @Test
        void nonStaticMethodIsAnError() {
            Result r = compile(MAIN, new JavaSource("demo.Jobs", """
                    package demo;
                    import cn.nukkit.plugin.annotation.ScheduleTask;
                    public class Jobs {
                        @ScheduleTask
                        public void tick() {}
                    }
                    """));
            r.assertFailureContains("must be public and static");
        }

        @Test
        void methodWithParametersIsAnError() {
            Result r = compile(MAIN, new JavaSource("demo.Jobs", """
                    package demo;
                    import cn.nukkit.plugin.annotation.ScheduleTask;
                    public class Jobs {
                        @ScheduleTask
                        public static void tick(int x) {}
                    }
                    """));
            r.assertFailureContains("must take no parameters");
        }
    }

    // ------------------------------------------------------------------
    // @Command
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("@Command")
    class Commands {

        @Test
        void commandIsRegisteredWithAllMetadata() {
            Result r = compile(MAIN, new JavaSource("demo.HealCommand", """
                    package demo;
                    import cn.nukkit.command.CommandSender;
                    @cn.nukkit.plugin.annotation.Command(name = "heal", aliases = {"h", "hp"},
                            permission = "demo.heal", description = "Heals", usage = "/heal <p>")
                    public class HealCommand extends cn.nukkit.command.Command {
                        @Override public boolean execute(CommandSender s, String l, String[] a) { return true; }
                    }
                    """));
            r.assertSuccess();
            String b = r.bootstrap();
            assertContains(b, "demo.HealCommand __cmd = new demo.HealCommand();");
            assertContains(b, "__cmd.setName(\"heal\");");
            assertContains(b, "__cmd.setAliases(new String[]{\"h\", \"hp\"});");
            assertContains(b, "__cmd.setPermission(\"demo.heal\");");
            assertContains(b, "__cmd.setDescription(\"Heals\");");
            assertContains(b, "__cmd.setUsage(\"/heal <p>\");");
            assertContains(b, "__cmap.register(__prefix, __cmd);");
        }

        @Test
        void optionalCommandMetadataIsOmitted() {
            Result r = compile(MAIN, new JavaSource("demo.PingCommand", """
                    package demo;
                    import cn.nukkit.command.CommandSender;
                    @cn.nukkit.plugin.annotation.Command(name = "ping")
                    public class PingCommand extends cn.nukkit.command.Command {
                        @Override public boolean execute(CommandSender s, String l, String[] a) { return true; }
                    }
                    """));
            r.assertSuccess();
            String b = r.bootstrap();
            assertContains(b, "__cmd.setName(\"ping\");");
            assertFalse(b.contains("setAliases"), "no aliases call when none given");
            assertFalse(b.contains("setPermission"), "no permission call when empty");
            assertFalse(b.contains("setDescription"), "no description call when empty");
            assertFalse(b.contains("setUsage"), "no usage call when empty");
        }

        @Test
        void paramTreeIsEnabledByDefault() {
            Result r = compile(MAIN, new JavaSource("demo.PingCommand", """
                    package demo;
                    import cn.nukkit.command.CommandSender;
                    @cn.nukkit.plugin.annotation.Command(name = "ping")
                    public class PingCommand extends cn.nukkit.command.Command {
                        @Override public boolean execute(CommandSender s, String l, String[] a) { return true; }
                    }
                    """));
            r.assertSuccess();
            assertContains(r.bootstrap(), "__cmd.enableParamTree();");
        }

        @Test
        void paramTreeCanBeDisabled() {
            Result r = compile(MAIN, new JavaSource("demo.PingCommand", """
                    package demo;
                    import cn.nukkit.command.CommandSender;
                    @cn.nukkit.plugin.annotation.Command(name = "ping", enableParamTree = false)
                    public class PingCommand extends cn.nukkit.command.Command {
                        @Override public boolean execute(CommandSender s, String l, String[] a) { return true; }
                    }
                    """));
            r.assertSuccess();
            assertFalse(r.bootstrap().contains("enableParamTree"),
                    "param tree call omitted when disabled");
        }

        @Test
        void notExtendingCommandIsAnError() {
            Result r = compile(MAIN, new JavaSource("demo.NotCmd", """
                    package demo;
                    @cn.nukkit.plugin.annotation.Command(name = "x")
                    public class NotCmd {}
                    """));
            r.assertFailureContains("must extend cn.nukkit.command.Command");
        }
    }

    // ------------------------------------------------------------------
    // Bootstrap structure / integration
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("bootstrap")
    class Bootstrap {

        @Test
        void emptyBootstrapStillHasInitAndNoRegistrations() {
            Result r = compile(MAIN);
            r.assertSuccess();
            String b = r.bootstrap();
            assertNotNull(b);
            assertContains(b, "public static void init(cn.nukkit.plugin.Plugin plugin)");
            assertFalse(b.contains("registerEvents"));
            assertFalse(b.contains("scheduleDelayed"));
            assertFalse(b.contains("__cmap"));
        }

        @Test
        void everythingTogetherCompilesAndEmitsAllWiring() {
            Result r = compile(
                    MAIN,
                    new JavaSource("demo.L1", """
                            package demo;
                            import cn.nukkit.event.Listener;
                            import cn.nukkit.plugin.annotation.EventListener;
                            @EventListener public class L1 implements Listener {}
                            """),
                    new JavaSource("demo.L2", """
                            package demo;
                            import cn.nukkit.event.Listener;
                            import cn.nukkit.plugin.annotation.EventListener;
                            @EventListener public class L2 implements Listener {}
                            """),
                    new JavaSource("demo.T1", """
                            package demo;
                            import cn.nukkit.scheduler.Task;
                            import cn.nukkit.plugin.annotation.ScheduleTask;
                            @ScheduleTask(period = 20) public class T1 extends Task {
                                @Override public void onRun(int t) {}
                            }
                            """),
                    new JavaSource("demo.Jobs", """
                            package demo;
                            import cn.nukkit.plugin.annotation.ScheduleTask;
                            public class Jobs {
                                @ScheduleTask(delay = 1) public static void run() {}
                            }
                            """),
                    new JavaSource("demo.C1", """
                            package demo;
                            import cn.nukkit.command.CommandSender;
                            @cn.nukkit.plugin.annotation.Command(name = "c1")
                            public class C1 extends cn.nukkit.command.Command {
                                @Override public boolean execute(CommandSender s, String l, String[] a) { return true; }
                            }
                            """));
            r.assertSuccess();
            String b = r.bootstrap();
            assertContains(b, "new demo.L1()");
            assertContains(b, "new demo.L2()");
            assertContains(b, "new demo.T1()");
            assertContains(b, "() -> demo.Jobs.run()");
            assertContains(b, "new demo.C1()");
            // The generated bootstrap must itself compile against the real API.
            assertTrue(r.bootstrapCompiled(),
                    "generated PNXPluginBootstrap should compile to a class file");
        }
    }

    // ------------------------------------------------------------------
    // Runtime: load the generated bytecode and execute init(plugin)
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("runtime wiring")
    class RuntimeWiring {

        @Test
        void initRegistersListenerSchedulesTasksAndRegistersCommand() throws Exception {
            Result r = compile(
                    MAIN,
                    new JavaSource("demo.RuntimeListener", """
                            package demo;
                            import cn.nukkit.event.Listener;
                            import cn.nukkit.plugin.annotation.EventListener;
                            @EventListener public class RuntimeListener implements Listener {}
                            """),
                    new JavaSource("demo.RuntimeTask", """
                            package demo;
                            import cn.nukkit.scheduler.Task;
                            import cn.nukkit.plugin.annotation.ScheduleTask;
                            @ScheduleTask(period = 20) public class RuntimeTask extends Task {
                                @Override public void onRun(int t) {}
                            }
                            """),
                    new JavaSource("demo.Jobs", """
                            package demo;
                            import cn.nukkit.plugin.annotation.ScheduleTask;
                            public class Jobs {
                                public static boolean ran = false;
                                @ScheduleTask(delay = 1) public static void run() { ran = true; }
                            }
                            """),
                    new JavaSource("demo.RtCommand", """
                            package demo;
                            import cn.nukkit.command.CommandSender;
                            @cn.nukkit.plugin.annotation.Command(name = "rt", aliases = {"r"}, permission = "demo.rt")
                            public class RtCommand extends cn.nukkit.command.Command {
                                @Override public boolean execute(CommandSender s, String l, String[] a) { return true; }
                            }
                            """));
            r.assertSuccess();

            BytesClassLoader loader = new BytesClassLoader(r.classBytes(), getClass().getClassLoader());
            Class<?> bootstrap = loader.loadClass("demo.PNXPluginBootstrap");

            // Mocked server side; the listener/task/command instances are real.
            Plugin plugin = mock(Plugin.class);
            Server server = mock(Server.class);
            PluginManager pm = mock(PluginManager.class);
            ServerScheduler scheduler = mock(ServerScheduler.class);
            SimpleCommandMap commandMap = mock(SimpleCommandMap.class);
            PluginDescription description = mock(PluginDescription.class);
            when(plugin.getServer()).thenReturn(server);
            when(plugin.getDescription()).thenReturn(description);
            when(description.getName()).thenReturn("Demo");
            when(server.getPluginManager()).thenReturn(pm);
            when(server.getScheduler()).thenReturn(scheduler);
            when(server.getCommandMap()).thenReturn(commandMap);

            bootstrap.getMethod("init", Plugin.class).invoke(null, plugin);

            // Listener registered with this plugin.
            ArgumentCaptor<Listener> listenerCaptor = ArgumentCaptor.forClass(Listener.class);
            verify(pm).registerEvents(listenerCaptor.capture(), eq(plugin));
            assertEquals("demo.RuntimeListener", listenerCaptor.getValue().getClass().getName());

            // Repeating class task scheduled with the right delay/period/async.
            verify(scheduler).scheduleDelayedRepeatingTask(eq(plugin), any(Runnable.class), eq(0), eq(20), eq(false));

            // One-shot method task scheduled; running its Runnable must call Jobs.run().
            ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
            verify(scheduler).scheduleDelayedTask(eq(plugin), runnableCaptor.capture(), eq(1), eq(false));
            runnableCaptor.getValue().run();
            assertTrue(loader.loadClass("demo.Jobs").getField("ran").getBoolean(null),
                    "scheduled method-task Runnable should invoke the annotated static method");

            // Command registered under the plugin name, fully configured.
            ArgumentCaptor<cn.nukkit.command.Command> commandCaptor =
                    ArgumentCaptor.forClass(cn.nukkit.command.Command.class);
            verify(commandMap).register(eq("Demo"), commandCaptor.capture());
            cn.nukkit.command.Command command = commandCaptor.getValue();
            assertInstanceOf(cn.nukkit.command.Command.class, command);
            assertEquals("rt", command.getName());
            assertTrue(java.util.Arrays.asList(command.getAliases()).contains("r"));
            assertEquals("demo.rt", command.getPermission());
            assertTrue(command.hasParamTree(), "enableParamTree() should have run by default");
        }
    }

    // ==================================================================
    // Compilation harness
    // ==================================================================

    private static Result compile(JavaSource... sources) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        Assertions.assertNotNull(compiler, "JDK compiler required to run these tests");
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        StandardJavaFileManager std = compiler.getStandardFileManager(diagnostics, null, StandardCharsets.UTF_8);
        MemoryFileManager fm = new MemoryFileManager(std);

        List<JavaFileObject> units = new ArrayList<>();
        for (JavaSource s : sources) {
            units.add(s.toFileObject());
        }

        // Resolve cn.nukkit.* against this test JVM's classpath explicitly, so it
        // does not depend on how the test runner seeds the default class path.
        List<String> options = List.of("-classpath", System.getProperty("java.class.path"));
        JavaCompiler.CompilationTask task =
                compiler.getTask(null, fm, diagnostics, options, null, units);
        task.setProcessors(List.of(new PluginAnnotationProcessor()));
        boolean success = task.call();
        return new Result(success, diagnostics, fm);
    }

    private static void assertContains(String haystack, String needle) {
        assertNotNull(haystack, "expected generated output, got null");
        assertTrue(haystack.contains(needle),
                "expected to contain:\n" + needle + "\n--- actual ---\n" + haystack);
    }

    /** An in-memory Java source compilation unit. */
    private record JavaSource(String fqn, String code) {
        JavaFileObject toFileObject() {
            URI uri = URI.create("string:///" + fqn.replace('.', '/') + ".java");
            return new SimpleJavaFileObject(uri, JavaFileObject.Kind.SOURCE) {
                @Override
                public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                    return code;
                }
            };
        }
    }

    /** Result of a compilation: success flag, diagnostics and captured outputs. */
    private static final class Result {
        private final boolean success;
        private final DiagnosticCollector<JavaFileObject> diagnostics;
        private final MemoryFileManager fm;

        Result(boolean success, DiagnosticCollector<JavaFileObject> diagnostics, MemoryFileManager fm) {
            this.success = success;
            this.diagnostics = diagnostics;
            this.fm = fm;
        }

        String pluginYml() {
            MemoryFileObject o = fm.resources.get("plugin.yml");
            return o == null ? null : o.content();
        }

        String bootstrap() {
            for (Map.Entry<String, MemoryFileObject> e : fm.sources.entrySet()) {
                if (e.getKey().endsWith("PNXPluginBootstrap")) {
                    return e.getValue().content();
                }
            }
            return null;
        }

        boolean bootstrapCompiled() {
            return fm.classes.keySet().stream().anyMatch(k -> k.endsWith("PNXPluginBootstrap"));
        }

        Map<String, byte[]> classBytes() {
            Map<String, byte[]> out = new LinkedHashMap<>();
            for (Map.Entry<String, MemoryFileObject> e : fm.classes.entrySet()) {
                out.put(e.getKey(), e.getValue().bytes());
            }
            return out;
        }

        List<String> errors() {
            List<String> out = new ArrayList<>();
            for (Diagnostic<? extends JavaFileObject> d : diagnostics.getDiagnostics()) {
                if (d.getKind() == Diagnostic.Kind.ERROR) {
                    out.add(d.getMessage(Locale.ENGLISH));
                }
            }
            return out;
        }

        void assertSuccess() {
            assertTrue(success, "compilation should succeed, errors:\n" + String.join("\n", errors()));
        }

        void assertFailureContains(String fragment) {
            assertFalse(success, "compilation should fail");
            String all = String.join("\n", errors());
            assertTrue(all.contains(fragment),
                    "expected an error containing:\n" + fragment + "\n--- actual errors ---\n" + all);
        }
    }

    /** Captures processor output (sources, classes, resources) in memory. */
    private static final class MemoryFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
        final Map<String, MemoryFileObject> sources = new LinkedHashMap<>();
        final Map<String, MemoryFileObject> classes = new LinkedHashMap<>();
        final Map<String, MemoryFileObject> resources = new LinkedHashMap<>();

        MemoryFileManager(StandardJavaFileManager delegate) {
            super(delegate);
        }

        @Override
        public JavaFileObject getJavaFileForOutput(Location location, String className,
                                                   JavaFileObject.Kind kind, javax.tools.FileObject sibling) {
            MemoryFileObject o = new MemoryFileObject(
                    URI.create("mem:///" + className.replace('.', '/') + kind.extension), kind);
            if (kind == JavaFileObject.Kind.SOURCE) {
                sources.put(className, o);
            } else {
                classes.put(className, o);
            }
            return o;
        }

        @Override
        public javax.tools.FileObject getFileForOutput(Location location, String packageName,
                                                       String relativeName, javax.tools.FileObject sibling) {
            MemoryFileObject o = new MemoryFileObject(
                    URI.create("mem:///" + relativeName), JavaFileObject.Kind.OTHER);
            resources.put(relativeName, o);
            return o;
        }
    }

    /** A writable in-memory file object whose written bytes are readable back. */
    private static final class MemoryFileObject extends SimpleJavaFileObject {
        private final ByteArrayOutputStream out = new ByteArrayOutputStream();

        MemoryFileObject(URI uri, Kind kind) {
            super(uri, kind);
        }

        @Override
        public OutputStream openOutputStream() {
            return out;
        }

        @Override
        public Writer openWriter() {
            return new OutputStreamWriter(openOutputStream(), StandardCharsets.UTF_8);
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return content();
        }

        String content() {
            return out.toString(StandardCharsets.UTF_8);
        }

        byte[] bytes() {
            return out.toByteArray();
        }
    }

    /** Loads classes from in-memory bytes (parent-first, so cn.nukkit resolves). */
    private static final class BytesClassLoader extends ClassLoader {
        private final Map<String, byte[]> byName;

        BytesClassLoader(Map<String, byte[]> byName, ClassLoader parent) {
            super(parent);
            this.byName = byName;
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            byte[] b = byName.get(name);
            if (b == null) {
                throw new ClassNotFoundException(name);
            }
            return defineClass(name, b, 0, b.length);
        }
    }
}
