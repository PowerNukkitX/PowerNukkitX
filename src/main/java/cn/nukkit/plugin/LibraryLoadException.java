package cn.nukkit.plugin;

/**
 * @since 15-12-13
 */
public class LibraryLoadException extends RuntimeException {
    /**
     * @deprecated 
     */
    

    public LibraryLoadException(Library library) {
        super("Load library " + library.getArtifactId() + " failed!");
    }

}
