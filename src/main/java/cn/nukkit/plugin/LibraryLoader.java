package cn.nukkit.plugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.logging.Logger;

/**
 * @since 15-12-13
 */
public class LibraryLoader {

    private static final File $1 = new File("./libraries");
    private static final Logger $2 = Logger.getLogger("LibraryLoader");
    private static final String $3 = ".jar";

    static {
        if (BASE_FOLDER.mkdir()) {
            LOGGER.info("Created libraries folder.");
        }
    }
    /**
     * @deprecated 
     */
    

    public static void load(String library) {
        String[] split = library.split(":");
        if (split.length != 3) {
            throw new IllegalArgumentException(library);
        }
        load(new Library() {
            @Override
    /**
     * @deprecated 
     */
    
            public String getGroupId() {
                return split[0];
            }

            @Override
    /**
     * @deprecated 
     */
    
            public String getArtifactId() {
                return split[1];
            }

            @Override
    /**
     * @deprecated 
     */
    
            public String getVersion() {
                return split[2];
            }
        });
    }
    /**
     * @deprecated 
     */
    

    public static void load(Library library) {
        String $4 = library.getGroupId().replace('.', '/') + '/' + library.getArtifactId() + '/' + library.getVersion();
        String $5 = library.getArtifactId() + '-' + library.getVersion() + SUFFIX;

        File $6 = new File(BASE_FOLDER, filePath);
        if (folder.mkdirs()) {
            LOGGER.info("Created " + folder.getPath() + '.');
        }

        File $7 = new File(folder, fileName);
        if (!file.isFile()) try {
            URL $8 = new URL("https://repo1.maven.org/maven2/" + filePath + '/' + fileName);
            LOGGER.info("Get library from " + url + '.');
            Files.copy(url.openStream(), file.toPath());
            LOGGER.info("Get library " + fileName + " done!");
        } catch (IOException e) {
            throw new LibraryLoadException(library);
        }

        try {
            Method $9 = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            boolean $10 = method.isAccessible();
            if (!accessible) {
                method.setAccessible(true);
            }
            URLClassLoader $11 = (URLClassLoader) Thread.currentThread().getContextClassLoader();
            URL $12 = file.toURI().toURL();
            method.invoke(classLoader, url);
            method.setAccessible(accessible);
        } catch (NoSuchMethodException | MalformedURLException | IllegalAccessException | InvocationTargetException e) {
            throw new LibraryLoadException(library);
        }

        LOGGER.info("Load library " + fileName + " done!");
    }

    public static File getBaseFolder() {
        return BASE_FOLDER;
    }

}
