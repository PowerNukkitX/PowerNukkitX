package cn.nukkit.plugin.js;

import cn.nukkit.PowerNukkitX;
import cn.nukkit.Server;
import cn.nukkit.plugin.CommonJSPlugin;
import cn.nukkit.plugin.JavaPluginLoader;
import cn.nukkit.utils.SeekableInMemoryByteChannel;
import org.graalvm.polyglot.io.FileSystem;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import static cn.nukkit.plugin.js.JSClassLoader.javaClassCache;

public final class ESMFileSystem implements FileSystem {
    final File baseDir;
    private final CommonJSPlugin plugin;
    private final JSClassLoader mainClassLoader;

    private final static Map<String, byte[]> innerModuleCache = new WeakHashMap<>(1, 1f);

    public ESMFileSystem(@NotNull File baseDir, @NotNull CommonJSPlugin plugin, JSClassLoader classLoader) {
        this.baseDir = baseDir;
        this.plugin = plugin;
        this.mainClassLoader = classLoader;
        try {
            mainClassLoader.addURL(baseDir.toURI().toURL());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Path parsePath(URI uri) {
        return parsePath(uri.toString());
    }

    @SuppressWarnings("DuplicateExpressions")
    @Override
    public Path parsePath(String path) {
        Path resolvedPath = null;
        var feature = JSFeatures.getFeatureByModule(path);
        if (feature != null && plugin.usedFeatures.containsKey(feature.getName())) {
            resolvedPath = Path.of("jsFeature", feature.getName() + "@" + path);
        } else if (path.startsWith("@")) {
            if (!path.contains("/") && !path.contains("\\")) {
                resolvedPath = Path.of(Server.getInstance().getPluginPath(), path + "/index.js");
            } else {
                resolvedPath = Path.of(Server.getInstance().getPluginPath(), path);
            }
        } else if (path.startsWith(":")) {
            resolvedPath = Path.of("inner-module", path.substring(1));
        } else if ((!path.endsWith(".js") && !path.startsWith("./") && !path.startsWith("../") && path.contains("."))) {
            try {
                if (javaClassCache.containsKey(path)) {
                    return Path.of("java-class", path);
                }
                var clazz = mainClassLoader.loadClass(path);
                if (clazz != null) {
                    javaClassCache.put(path, clazz);
                    resolvedPath = Path.of("java-class", path);
                } else {
                    outer:
                    for (var pl : Server.getInstance().getPluginManager().getFileAssociations().values()) {
                        if (pl instanceof JavaPluginLoader javaPluginLoader) {
                            for (var loader : javaPluginLoader.getClassLoaders().values()) {
                                clazz = loader.loadClass(path);
                                if (clazz != null) {
                                    javaClassCache.put(path, clazz);
                                    resolvedPath = Path.of("java-class", path);
                                    break outer;
                                }
                            }
                        }
                    }
                }
            } catch (ClassNotFoundException ignore) {
                outer2:
                for (var pl : Server.getInstance().getPluginManager().getFileAssociations().values()) {
                    if (pl instanceof JavaPluginLoader javaPluginLoader) {
                        for (var loader : javaPluginLoader.getClassLoaders().values()) {
                            try {
                                var clazz = loader.loadClass(path);
                                if (clazz != null) {
                                    javaClassCache.put(path, clazz);
                                    resolvedPath = Path.of("java-class", path);
                                    break outer2;
                                }
                            } catch (ClassNotFoundException ignore2) {

                            }
                        }
                    }
                }
            }
            if (resolvedPath == null && getDots(path) > 1) {
                // see if the path is a java class file
                var classPath = path.replace('.', '/') + ".class";
                var classFile = new File(baseDir, classPath);
                if (classFile.exists()) {
                    try {
                        var clazz = mainClassLoader.loadClass(path);
                        if (clazz != null) {
                            javaClassCache.put(path, clazz);
                            resolvedPath = Path.of("java-class", path);
                        }
                    } catch (ClassNotFoundException ignore) {
                    }
                }
            }
        }
        if (resolvedPath == null) {
            resolvedPath = baseDir.toPath().resolve(path);
        }
        if (!resolvedPath.startsWith("java-class") && !resolvedPath.startsWith("jsFeature") && !resolvedPath.startsWith("inner-module") && !Files.isRegularFile(resolvedPath)) {
            var tmpPath = Path.of(resolvedPath + ".js");
            if (Files.isRegularFile(tmpPath)) {
                resolvedPath = tmpPath;
            }
        }
        return resolvedPath;
    }

    public static int getDots(@NotNull String originStr) {
        var res = 0;
        var i = originStr.indexOf('.');
        while (i != -1) {
            i = originStr.indexOf('.', i + 1);
            res++;
        }
        return res;
    }

    @Override
    public void checkAccess(Path path, Set<? extends AccessMode> modes, LinkOption... linkOptions) throws IOException {
        if (path.startsWith("inner-module")) {
            for (var each : modes) {
                if (each != AccessMode.READ) {
                    throw new IOException("Inner module cannot be accessed.");
                }
            }
        } else if (path.startsWith("java-class")) {
            for (var each : modes) {
                if (each != AccessMode.READ) {
                    throw new IOException("Java class cannot be accessed.");
                }
            }
        } else if (path.startsWith("jsFeature")) {
            for (var each : modes) {
                if (each != AccessMode.READ) {
                    throw new IOException("JS Feature cannot be accessed.");
                }
            }
        } else {
            path = path.toRealPath(linkOptions);
            for (var each : modes) {
                if (each == AccessMode.READ && !Files.isReadable(path)) {
                    throw new AccessDeniedException(path.toString());
                } else if (each == AccessMode.WRITE && !Files.isWritable(path)) {
                    throw new AccessDeniedException(path.toString());
                } else if (each == AccessMode.EXECUTE && !Files.isExecutable(path)) {
                    throw new AccessDeniedException(path.toString());
                }
            }
        }
    }

    @Override
    public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {
        if (dir.startsWith("inner-module") || dir.startsWith("java-class") || dir.startsWith("jsFeature")) {
            throw new IOException("Inner module cannot be accessed.");
        }
        Files.createDirectories(dir, attrs);
    }

    @Override
    public void delete(Path path) throws IOException {
        if (path.startsWith("inner-module") || path.startsWith("java-class") || path.startsWith("jsFeature")) {
            throw new IOException("Inner module cannot be accessed.");
        }
        Files.delete(path);
    }

    @Override
    public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
        if (path.startsWith("inner-module")) {
            byte[] contents = new byte[0];
            var moduleName = path.toString().substring(13);
            if ("plugin-id".equals(moduleName)) {
                contents = ("export const id = " + plugin.id).getBytes(StandardCharsets.UTF_8);
            } else {
                if (innerModuleCache.containsKey(moduleName)) {
                    contents = innerModuleCache.get(moduleName);
                } else {
                    try (var ins = PowerNukkitX.class.getModule().getResourceAsStream("inner-module/" + moduleName + ".js")) {
                        if (ins != null)
                            contents = ins.readAllBytes();
                    }
                    if (contents.length != 0) {
                        innerModuleCache.put(moduleName, contents);
                    }
                }
            }
            return new SeekableInMemoryByteChannel(contents);
        } else if (path.startsWith("java-class")) {
            var className = path.toString().substring(11);
            return new SeekableInMemoryByteChannel(ESMJavaExporter.exportJava(javaClassCache.get(className)).getBytes(StandardCharsets.UTF_8));
        } else if (path.startsWith("jsFeature")) {
            var tmp = path.toString().substring(10);
            var index = tmp.lastIndexOf("@");
            var featureName = tmp.substring(0, index);
            var moduleName = tmp.substring(index + 1);
            var feature = plugin.usedFeatures.get(featureName);
            var module = feature.generateModule(moduleName, plugin.getJsContext());
            var codeBuilder = new StringBuilder("const JSFeaturesClass = Java.type('cn.nukkit.plugin.js.JSFeatures');\n");
            for (var each : module.entrySet()) {
                var id = JSFeatures.FEATURE_GENERATED_TMP_ID.getAndIncrement();
                JSFeatures.FEATURE_GENERATED_TMP_MAP.put(id, each.getValue());
                codeBuilder.append("export const ").append(each.getKey()).append(" = ").append("JSFeaturesClass.FEATURE_GENERATED_TMP_MAP.remove(").append(id).append(");\n");
            }
            return new SeekableInMemoryByteChannel(codeBuilder.toString().getBytes(StandardCharsets.UTF_8));
        }
        return Files.newByteChannel(path, options, attrs);
    }

    public Reader newReader(Path path) throws IOException {
        if (path.startsWith("inner-module")) {
            String contents = "";
            var moduleName = path.toString().substring(13);
            if ("plugin-id".equals(moduleName)) {
                contents = "export const id = " + plugin.id;
            } else {
                if (innerModuleCache.containsKey(moduleName)) {
                    contents = new String(innerModuleCache.get(moduleName));
                } else {
                    try (var ins = PowerNukkitX.class.getModule().getResourceAsStream("inner-module/" + moduleName + ".js")) {
                        if (ins != null)
                            return new InputStreamReader(ins);
                    }
                }
            }
            return new StringReader(contents);
        } else if (path.startsWith("java-class")) {
            var className = path.toString().substring(11);
            return new StringReader(ESMJavaExporter.exportJava(javaClassCache.get(className)));
        } else if (path.startsWith("jsFeature")) {
            var tmp = path.toString().substring(10);
            var index = tmp.lastIndexOf("@");
            var featureName = tmp.substring(0, index);
            var moduleName = tmp.substring(index + 1);
            var feature = plugin.usedFeatures.get(featureName);
            var module = feature.generateModule(moduleName, plugin.getJsContext());
            var codeBuilder = new StringBuilder("const JSFeaturesClass = Java.type('cn.nukkit.plugin.js.JSFeatures');\n");
            for (var each : module.entrySet()) {
                var id = JSFeatures.FEATURE_GENERATED_TMP_ID.getAndIncrement();
                JSFeatures.FEATURE_GENERATED_TMP_MAP.put(id, each.getValue());
                codeBuilder.append("export const ").append(each.getKey()).append(" = ").append("JSFeaturesClass.FEATURE_GENERATED_TMP_MAP.remove(").append(id).append(");\n");
            }
            return new StringReader(codeBuilder.toString());
        }
        return Files.newBufferedReader(path, StandardCharsets.UTF_8);
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter) throws IOException {
        if (dir.startsWith("inner-module") || dir.startsWith("java-class") || dir.startsWith("jsFeature")) {
            throw new IOException("Inner module cannot be accessed.");
        }
        return Files.newDirectoryStream(dir, filter);
    }

    @Override
    public Path toAbsolutePath(Path path) {
        if (path.startsWith("inner-module") || path.startsWith("java-class") || path.startsWith("jsFeature")) {
            return path;
        }
        return path.toAbsolutePath();
    }

    @Override
    public Path toRealPath(Path path, LinkOption... linkOptions) throws IOException {
        if (path.startsWith("inner-module") || path.startsWith("java-class") || path.startsWith("jsFeature")) {
            return path;
        }
        return path.toRealPath(linkOptions);
    }

    @Override
    public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
        if (path.startsWith("inner-module") || path.startsWith("java-class") || path.startsWith("jsFeature")) {
            throw new IOException("Inner module cannot be accessed.");
        }
        return Files.readAttributes(path, attributes, options);
    }
}
