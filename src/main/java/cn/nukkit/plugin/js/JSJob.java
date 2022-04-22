package cn.nukkit.plugin.js;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import static cn.nukkit.plugin.js.JSConcurrentManager.PROMISE_FAILED;

public final class JSJob implements AutoCloseable {
    private final Context sourceContext;
    private final ESMFileSystem fileSystem;
    private final String jobSourcePath;
    private Context jobContext;
    private Reader sourceReader;
    private Path sourcePath;

    private Value jobMainFunc;

    public JSJob(Context sourceContext, ESMFileSystem fileSystem, String jobSourcePath) {
        this.sourceContext = sourceContext;
        this.fileSystem = fileSystem;
        this.jobSourcePath = jobSourcePath;
    }

    public void init() throws IOException {
        this.jobContext = Context.newBuilder("js")
                .fileSystem(fileSystem)
                .allowAllAccess(true)
                .allowHostAccess(HostAccess.ALL)
                .allowHostClassLoading(true)
                .allowHostClassLookup(className -> true)
                .allowIO(true)
                .allowExperimentalOptions(true)
                .option("js.esm-eval-returns-exports", "true")
                .option("js.foreign-object-prototype", "true")
                .build();
        this.sourcePath = fileSystem.parsePath(jobSourcePath);
        this.sourceReader = fileSystem.newReader(sourcePath);
    }

    public void start() {
        try {
            var exports = jobContext.eval(Source.newBuilder("js", sourceReader,
                            fileSystem.baseDir.getName() + "/job-" + sourcePath.getFileName())
                    .mimeType("application/javascript+module").build());
            this.jobMainFunc = exports.getMember("main");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("SynchronizeOnNonFinalField")
    public JSConcurrentManager.JPromise work(Object... args) {
        synchronized (sourceContext) {
            return JSConcurrentManager.wrapPromise(sourceContext, CompletableFuture.supplyAsync(() -> {
                synchronized (jobContext) {
                    if (jobMainFunc == null || !jobMainFunc.canExecute()) {
                        return PROMISE_FAILED;
                    }
                    try {
                        return jobMainFunc.execute(args);
                    } catch (Exception e) {
                        return PROMISE_FAILED;
                    }
                }
            }));
        }
    }

    @SuppressWarnings("SynchronizeOnNonFinalField")
    @Override
    public void close() throws Exception {
        synchronized (jobContext) {
            jobContext.close(true);
        }
    }
}
