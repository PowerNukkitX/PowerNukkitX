package cn.nukkit.plugin.js;

import cn.nukkit.Nukkit;
import cn.nukkit.Server;
import cn.nukkit.utils.SeekableInMemoryByteChannel;
import org.graalvm.polyglot.io.FileSystem;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public final class ESMFileSystem implements FileSystem {
    private final File baseDir;

    private final static Map<String, byte[]> innerModuleCache = new WeakHashMap<>(1, 1f);

    public ESMFileSystem(File baseDir) {
        this.baseDir = baseDir;
    }

    @Override
    public Path parsePath(URI uri) {
        return parsePath(uri.toString());
    }

    @Override
    public Path parsePath(String path) {
        if (path.startsWith("@")) {
            path = path.replaceFirst("@", Server.getInstance().getPluginPath() + "/");
        } else if (path.startsWith(":")) {
            return Path.of("inner-module", path.substring(1));
        }
        return baseDir.toPath().resolve(path);
    }

    @Override
    public void checkAccess(Path path, Set<? extends AccessMode> modes, LinkOption... linkOptions) throws IOException {
        if (path.startsWith("inner-module")) {
            for (var each : modes) {
                if (each != AccessMode.READ) {
                    throw new IOException("Inner module cannot be accessed.");
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
        if (dir.startsWith("inner-module")) {
            throw new IOException("Inner module cannot be accessed.");
        }
        Files.createDirectories(dir, attrs);
    }

    @Override
    public void delete(Path path) throws IOException {
        if (path.startsWith("inner-module")) {
            throw new IOException("Inner module cannot be accessed.");
        }
        Files.delete(path);
    }

    @Override
    public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
        if (path.startsWith("inner-module")) {
            byte[] contents = new byte[0];
            var moduleName = path.toString().substring(13);
            if (innerModuleCache.containsKey(moduleName)) {
                contents = innerModuleCache.get(moduleName);
            } else {
                try (var ins = Nukkit.class.getClassLoader().getResourceAsStream("inner-module/" + moduleName + ".js")) {
                    if (ins != null)
                        contents = ins.readAllBytes();
                }
                if (contents.length != 0) {
                    innerModuleCache.put(moduleName, contents);
                }
            }
            return new SeekableInMemoryByteChannel(contents);
        }
        return Files.newByteChannel(path, options, attrs);
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter) throws IOException {
        if (dir.startsWith("inner-module")) {
            throw new IOException("Inner module cannot be accessed.");
        }
        return Files.newDirectoryStream(dir, filter);
    }

    @Override
    public Path toAbsolutePath(Path path) {
        if (path.startsWith("inner-module")) {
            return path;
        }
        return path.toAbsolutePath();
    }

    @Override
    public Path toRealPath(Path path, LinkOption... linkOptions) throws IOException {
        if (path.startsWith("inner-module")) {
            return path;
        }
        return path.toRealPath(linkOptions);
    }

    @Override
    public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
        if (path.startsWith("inner-module")) {
            throw new IOException("Inner module cannot be accessed.");
        }
        return Files.readAttributes(path, attributes, options);
    }
}
