package cn.nukkit;

public final class JarStart {
    /**
     * If the user uses java -jar to start the server.
     */
    private static boolean usingJavaJar = false;

    public static void main(String[] args) {
        try {
            Thread.currentThread().getContextClassLoader().loadClass("joptsimple.OptionSpec");
        } catch (ClassNotFoundException | java.lang.NoClassDefFoundError e) {
            System.out.println("Do NOT use java -jar to run PowerNukkitX!");
            System.out.println("For more information. See https://docs.powernukkitx.com");
            return;
        }
        usingJavaJar = true;
        Nukkit.main(args);
    }

    public static boolean isUsingJavaJar() {
        return usingJavaJar;
    }

    // Method to reset the state for testing
    public static void resetUsingJavaJar() {
        usingJavaJar = false;
    }
}
