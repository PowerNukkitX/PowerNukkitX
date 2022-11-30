/*
 * Decompiled with CFR 0.1.1 (FabricMC 57d88659).
 *
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package cn.nukkit.utils;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import org.jetbrains.annotations.Nullable;

/**
 * 命名空间标识符
 */
@PowerNukkitXOnly
@Since("1.19.50-r1")
public class Identifier {
    public static final char NAMESPACE_SEPARATOR = ':';
    public static final String DEFAULT_NAMESPACE = "minecraft";

    protected final String namespace;
    protected final String path;

    protected Identifier(String[] id) {
        this.namespace = id[0].isEmpty() ? DEFAULT_NAMESPACE : id[0];
        this.path = id[1];
        if (!Identifier.isNamespaceValid(this.namespace)) {
            throw new InvalidIdentifierException("Non [a-z0-9_.-] character in namespace of location: " + this.namespace + ":" + this.path);
        }
        if (!Identifier.isPathValid(this.path)) {
            throw new InvalidIdentifierException("Non [a-z0-9/._-] character in path of location: " + this.namespace + ":" + this.path);
        }
    }

    public Identifier(String id) {
        this(Identifier.split(id, ':'));
    }

    public Identifier(String namespace, String path) {
        this(new String[]{namespace, path});
    }

    /**
     * 通过自定义的命名空间分割符分割并返回一个Identifier对象
     * @param id 字符串
     * @param delimiter 分割符
     * @return 命名空间对象
     */
    public static Identifier splitOn(String id, char delimiter) {
        return new Identifier(Identifier.split(id, delimiter));
    }

    @Nullable
    public static Identifier tryParse(String id) {
        try {
            return new Identifier(id);
        } catch (InvalidIdentifierException lv) {
            return null;
        }
    }

    @Nullable
    public static Identifier of(String namespace, String path) {
        try {
            return new Identifier(namespace, path);
        } catch (InvalidIdentifierException lv) {
            return null;
        }
    }

    protected static String[] split(String id, char delimiter) {
        String[] strings = new String[]{DEFAULT_NAMESPACE, id};
        int i = id.indexOf(delimiter);
        if (i >= 0) {
            strings[1] = id.substring(i + 1, id.length());
            if (i >= 1) {
                strings[0] = id.substring(0, i);
            }
        }
        return strings;
    }

    public static boolean isCharValid(char c) {
        return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == '_' || c == ':' || c == '/' || c == '.' || c == '-';
    }

    private static boolean isPathValid(String path) {
        for (int i = 0; i < path.length(); ++i) {
            if (Identifier.isPathCharacterValid(path.charAt(i))) continue;
            return false;
        }
        return true;
    }

    private static boolean isNamespaceValid(String namespace) {
        for (int i = 0; i < namespace.length(); ++i) {
            if (Identifier.isNamespaceCharacterValid(namespace.charAt(i))) continue;
            return false;
        }
        return true;
    }

    public static boolean isPathCharacterValid(char character) {
        return character == '_' || character == '-' || character >= 'a' && character <= 'z' || character >= '0' && character <= '9' || character == '/' || character == '.';
    }

    private static boolean isNamespaceCharacterValid(char character) {
        return character == '_' || character == '-' || character >= 'a' && character <= 'z' || character >= '0' && character <= '9' || character == '.';
    }

    public static boolean isValid(String id) {
        String[] strings = Identifier.split(id, ':');
        return Identifier.isNamespaceValid(strings[0].isEmpty() ? DEFAULT_NAMESPACE : strings[0]) && Identifier.isPathValid(strings[1]);
    }

    public String getPath() {
        return this.path;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public String toString() {
        return this.namespace + ":" + this.path;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Identifier lv) {
            return this.namespace.equals(lv.namespace) && this.path.equals(lv.path);
        }
        return false;
    }

    public int hashCode() {
        return 31 * this.namespace.hashCode() + this.path.hashCode();
    }
}

