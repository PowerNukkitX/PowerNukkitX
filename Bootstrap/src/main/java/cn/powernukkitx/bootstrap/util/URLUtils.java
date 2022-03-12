package cn.powernukkitx.bootstrap.util;

import java.net.MalformedURLException;
import java.net.URL;

import static cn.powernukkitx.bootstrap.util.ConfigUtils.get;

public final class URLUtils {
    public static URL graal17URL() {
        try {
            final String s = System.getProperties().getProperty("os.name").toUpperCase();
            if (s.contains("WINDOWS")) {
                return new URL(get("graalvm.win-x86"));
            } else if (s.contains("LINUX")) {
                final String arch = System.getProperty("os.arch").toLowerCase();
                if (arch.contains("arm") || arch.contains("aarch")) {
                    return new URL(get("graalvm.linux-aarch"));
                }
                return new URL(get("graalvm.linux-x86"));
            } else if (s.contains("MACOS") || s.contains("DARWIN") || s.contains("OSX")) {
                return new URL(get("graalvm.darwin-x86"));
            }
        } catch (MalformedURLException ignore) {

        }
        return null;
    }

    public static URL adopt17URL() {
        try {
            final String s = System.getProperties().getProperty("os.name").toUpperCase();
            if (s.contains("WINDOWS")) {
                return new URL(get("adopt.win-x86"));
            } else if (s.contains("LINUX")) {
                final String arch = System.getProperty("os.arch").toLowerCase();
                if (arch.contains("aarch")) {
                    return new URL(get("adopt.linux-aarch"));
                } else if (arch.contains("arm")) {
                    return new URL(get("adopt.linux-arm"));
                }
                return new URL(get("adopt.linux-x86"));
            } else if (s.contains("MACOS") || s.contains("DARWIN") || s.contains("OSX")) {
                return new URL(get("adopt.darwin-x86"));
            }
        } catch (MalformedURLException ignore) {

        }
        return null;
    }
}
