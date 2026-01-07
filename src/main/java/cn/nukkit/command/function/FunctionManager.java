package cn.nukkit.command.function;

import cn.nukkit.command.data.CommandEnum;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages the loading, storage, and retrieval of function files for PowerNukkitX.
 * <p>
 * The FunctionManager is responsible for discovering, parsing, and providing access to all available
 * function files (typically .mcfunction files) within a specified root directory. It supports reloading,
 * dynamic updates to the command enum for auto-completion, and efficient lookup of functions by name.
 * <p>
 * Features:
 * <ul>
 *   <li>Recursively scans the root directory for .mcfunction files and loads them as {@link Function} instances.</li>
 *   <li>Maintains a mapping from function names (relative to root, without extension) to {@link Function} objects.</li>
 *   <li>Supports reloading all functions and updating the {@link CommandEnum#FUNCTION_FILE} for client auto-completion.</li>
 *   <li>Provides methods to check for function existence and retrieve functions by name.</li>
 *   <li>Ensures the root directory exists, creating it if necessary.</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Instantiate with a root path (as {@link Path} or String) to manage function files in that directory.</li>
 *   <li>Call {@link #reload()} to refresh the function list and update client-side enums.</li>
 *   <li>Use {@link #getFunction(String)} to retrieve a loaded function by name.</li>
 *   <li>Use {@link #containFunction(String)} to check if a function exists.</li>
 * </ul>
 * <p>
 * Example:
 * <pre>
 * FunctionManager manager = new FunctionManager(Paths.get("functions"));
 * if (manager.containFunction("myfunc")) {
 *     Function func = manager.getFunction("myfunc");
 *     func.dispatch(player);
 * }
 * </pre>
 *
 * @author PowerNukkitX Project Team
 * @since PowerNukkitX 2.0.0
 */
@Getter
public class FunctionManager {
    /**
     * The root directory where function files are stored.
     */
    private final Path rootPath;
    /**
     * A map of function names (relative to root, without extension) to loaded {@link Function} instances.
     */
    private final Map<String, Function> functions = new HashMap<>();

    /**
     * Constructs a FunctionManager with the specified root directory.
     * Ensures the directory exists and loads all function files.
     *
     * @param rootPath the root directory as a {@link Path}
     */
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

    /**
     * Constructs a FunctionManager with the specified root directory as a string.
     *
     * @param rootPath the root directory as a string
     */
    public FunctionManager(String rootPath) {
        this(Path.of(rootPath));
    }

    /**
     * Loads all .mcfunction files from the root directory and its subdirectories.
     * Populates the {@link #functions} map with function names and instances.
     */
    public void loadFunctions() {
        try {
            Files.walkFileTree(this.rootPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                    if (path.toString().endsWith(".mcfunction")) {
                        functions.put(path.toString().replace(rootPath + "\\", "").replaceAll("\\\\", "/").replace(".mcfunction", ""), Function.fromPath(path));
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reloads all function files, clearing the current map and updating the client-side enum for auto-completion.
     * Calls {@link #loadFunctions()} and updates {@link CommandEnum#FUNCTION_FILE}.
     */
    public void reload() {
        functions.clear();
        loadFunctions();
        CommandEnum.FUNCTION_FILE.updateSoftEnum();
    }

    /**
     * Checks if a function with the given name exists in the manager.
     *
     * @param name the function name (relative to root, without extension)
     * @return true if the function exists, false otherwise
     */
    public boolean containFunction(String name) {
        return functions.containsKey(name);
    }

    /**
     * Retrieves a loaded function by its name.
     *
     * @param name the function name (relative to root, without extension)
     * @return the {@link Function} instance, or null if not found
     */
    public Function getFunction(String name) {
        return functions.get(name);
    }
}
