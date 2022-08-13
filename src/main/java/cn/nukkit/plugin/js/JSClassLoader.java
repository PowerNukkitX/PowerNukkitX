package cn.nukkit.plugin.js;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.plugin.CommonJSPlugin;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@PowerNukkitXOnly
@Since("1.19.20-r2")
public class JSClassLoader extends URLClassLoader {
    public final static Map<String, Class<?>> javaClassCache = new ConcurrentHashMap<>();

    static {
        javaClassCache.put("int", int.class);
        javaClassCache.put("long", long.class);
        javaClassCache.put("float", float.class);
        javaClassCache.put("double", double.class);
        javaClassCache.put("byte", byte.class);
        javaClassCache.put("short", short.class);
        javaClassCache.put("char", char.class);
        javaClassCache.put("boolean", boolean.class);
        javaClassCache.put("void", void.class);
        javaClassCache.put("int[]", int[].class);
        javaClassCache.put("long[]", long[].class);
        javaClassCache.put("float[]", float[].class);
        javaClassCache.put("double[]", double[].class);
        javaClassCache.put("byte[]", byte[].class);
        javaClassCache.put("short[]", short[].class);
        javaClassCache.put("char[]", char[].class);
        javaClassCache.put("boolean[]", boolean[].class);
    }

    @Getter
    private final CommonJSPlugin jsPlugin;

    public JSClassLoader(CommonJSPlugin jsPlugin, ClassLoader parent) {
        super(new URL[0], parent);
        this.jsPlugin = jsPlugin;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (javaClassCache.containsKey(name)) {
            return javaClassCache.get(name);
        }
        return super.findClass(name);
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }

    public void addJar(@NotNull File jarFile) {
        try {
            addURL(jarFile.toURI().toURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void addJar(@NotNull String jarPath) {
        addJar(new File(jarPath));
    }
}
