package cn.nukkit.command.function;

import lombok.Getter;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

@Getter
public class FunctionManager {

    private Path rootPath;
    private Map<String,Function> functions = new HashMap<>();

    public FunctionManager(Path rootPath) {
        this.rootPath = rootPath;
        try {
            if (!Files.exists(this.rootPath)) {
                Files.createDirectories(this.rootPath);
            }
            loadFunctions();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FunctionManager(String rootPath) {
        this(Path.of(rootPath));
    }

    public void loadFunctions() {
        try {
            Files.walkFileTree(this.rootPath, new SimpleFileVisitor<Path>(){
                @Override
                public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                    if (path.toString().endsWith(".mcfunction")) {
                        functions.put(path.toString().replace(rootPath.toString() + "\\","").replaceAll("\\\\","/").replace(".mcfunction",""), Function.fromPath(FunctionManager.this,path));
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        functions.clear();
        loadFunctions();
    }

    public boolean containFunction(String name) {
        return functions.containsKey(name);
    }

    public Function getFunction(String name) {
        return functions.get(name);
    }
}
