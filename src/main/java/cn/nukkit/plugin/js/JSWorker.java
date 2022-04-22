package cn.nukkit.plugin.js;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import static cn.nukkit.plugin.js.JSConcurrentManager.PROMISE_FAILED;
import static cn.nukkit.plugin.js.JSConcurrentManager.wrapPromise;

public final class JSWorker implements AutoCloseable {
    private static final Value NULL = Value.asValue(null);

    private final Context sourceContext;
    private final ESMFileSystem fileSystem;
    private final String workerSourcePath;
    private Context workerContext;
    private Reader sourceReader;
    private Path sourcePath;
    private Thread workerThread;

    private Value sourceReceiveCallback;
    private Value workerReceiveCallback;

    public JSWorker(Context sourceContext, ESMFileSystem fileSystem, String workerSourcePath) {
        this.sourceContext = sourceContext;
        this.fileSystem = fileSystem;
        this.workerSourcePath = workerSourcePath;
    }

    public void init() throws IOException {
        this.workerContext = Context.newBuilder("js")
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
        var workerGlobal = workerContext.getBindings("js");
        workerGlobal.putMember("postMessage", (ProxyExecutable) arguments -> {
            synchronized (sourceContext) {
                if (sourceReceiveCallback != null)
                    return sourceReceiveCallback.execute((Object[]) arguments);
                else return NULL;
            }
        });
        workerGlobal.putMember("postMessageAsync", (ProxyExecutable) arguments -> JSConcurrentManager.wrapPromise(workerContext,
                CompletableFuture.supplyAsync(() -> {
                    synchronized (sourceContext) {
                        if (sourceReceiveCallback != null)
                            return sourceReceiveCallback.execute((Object[]) arguments);
                        else return PROMISE_FAILED;
                    }
                })));
        this.sourcePath = fileSystem.parsePath(workerSourcePath);
        this.sourceReader = fileSystem.newReader(sourcePath);
    }

    public void start() {
        if (this.workerThread != null && workerThread.isAlive()) {
            workerContext.close(true);
            workerThread.interrupt();
        }
        this.workerThread = new Thread(() -> {
            try {
                var exports = workerContext.eval(Source.newBuilder("js", sourceReader,
                                fileSystem.baseDir.getName() + "/worker-" + sourcePath.getFileName() + "-" + workerThread.getId())
                        .mimeType("application/javascript+module").build());
                this.setWorkerReceiveCallback(exports.getMember("onmessage"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        this.workerThread.start();
    }

    public void setSourceReceiveCallback(Value value) {
        if (value != null && value.canExecute()) {
            this.sourceReceiveCallback = value;
        }
    }

    public void setWorkerReceiveCallback(Value value) {
        if (value != null && value.canExecute()) {
            this.workerReceiveCallback = value;
        }
    }

    public Value getSourceReceiveCallback() {
        return sourceReceiveCallback;
    }

    public Value getWorkerReceiveCallback() {
        return workerReceiveCallback;
    }

    @SuppressWarnings("SynchronizeOnNonFinalField")
    public Value postMessage(Value... arguments) {
        synchronized (workerContext) {
            if (workerReceiveCallback != null) {
                return workerReceiveCallback.execute((Object[]) arguments);
            } else {
                return NULL;
            }
        }
    }

    @SuppressWarnings("SynchronizeOnNonFinalField")
    public JSConcurrentManager.JPromise postMessageAsync(Value... arguments) {
        return wrapPromise(sourceContext, CompletableFuture.supplyAsync(() -> {
            synchronized (workerContext) {
                if (workerReceiveCallback != null) {
                    return workerReceiveCallback.execute((Object[]) arguments);
                } else {
                    return PROMISE_FAILED;
                }
            }
        }));
    }

    @Override
    public void close() throws Exception {
        workerContext.close(true);
        this.workerThread.interrupt();
        this.workerThread = null;
    }
}
