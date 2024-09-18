package cn.nukkit;

/**
 * The JarStart class is the entry point when starting the server using the `java -jar` command.
 * It ensures that the necessary libraries are present before delegating to the main Nukkit class.
 */
public final class JarStart {
    /**
     * Indicates if the server was started using the `java -jar` command.
     */
    private static boolean usingJavaJar = false;

    /**
     * The main method that serves as the entry point for the application.
     * It checks for the presence of required libraries and starts the Nukkit server.
     *
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        try {
            // Check if the required library is present
            Thread.currentThread().getContextClassLoader().loadClass("joptsimple.OptionSpec");
        } catch (ClassNotFoundException | java.lang.NoClassDefFoundError e) {
            // If the library is not found, print an error message and exit
            System.out.println("No libraries detected. PowerNukkitX cannot work without them and will now exit.");
            System.out.println("Do NOT use java -jar to run PowerNukkitX!");
            System.out.println("For more information, see https://docs-pnx.pages.dev");
            return;
        }
        usingJavaJar = true;
        Nukkit.main(args);
    }

    /**
     * Checks if the server was started using the `java -jar` command.
     *
     * @return True if the server was started using `java -jar`, false otherwise.
     */
    public static boolean isUsingJavaJar() {
        return usingJavaJar;
    }

    /**
     * Resets the `usingJavaJar` flag to false. This method is intended for testing purposes.
     */
    public static void resetUsingJavaJar() {
        usingJavaJar = false;
    }
}