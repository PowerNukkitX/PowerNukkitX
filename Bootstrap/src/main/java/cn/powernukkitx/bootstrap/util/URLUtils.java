package cn.powernukkitx.bootstrap.util;

import com.github.kevinsawicki.http.HttpRequest;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

import static cn.powernukkitx.bootstrap.util.ConfigUtils.get;
import static cn.powernukkitx.bootstrap.util.StringUtils.displayableBytes;

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
            } else if (s.contains("MACOS") || s.contains("DARWIN") || s.contains("OSX") || s.contains("MAC")) {
                return new URL(get("graalvm.darwin-x86"));
            }
        } catch (MalformedURLException ignore) {

        }
        return null;
    }

    public static URL adopt17URL() {
        try {
            final String s = System.getProperties().getProperty("os.name").toUpperCase();
            final String arch = System.getProperty("os.arch").toLowerCase();
            if (s.contains("WINDOWS")) {
                return new URL(get("adopt.win-x86"));
            } else if (s.contains("LINUX")) {
                if (arch.contains("aarch")) {
                    return new URL(get("adopt.linux-aarch"));
                } else if (arch.contains("arm")) {
                    return new URL(get("adopt.linux-arm"));
                }
                return new URL(get("adopt.linux-x86"));
            } else if (s.contains("MACOS") || s.contains("DARWIN") || s.contains("OSX") || s.contains("MAC")) {
                if (arch.contains("aarch") || arch.contains("arm")) {
                    return new URL(get("adopt.darwin-aarch"));
                }
                return new URL(get("adopt.darwin-x86"));
            }
        } catch (MalformedURLException ignore) {

        }
        return null;
    }

    public static URL getAssetsLink(String category, String name) {
        try {
            return new URL("https://assets.powernukkitx.cn/" + category + "/" + name);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public static void downloadWithBar(URL downloadURL, File target, String displayName, Timer timer) {
        try {
            HttpRequest request = HttpRequest.get(downloadURL);
            Logger.trInfo("display.connecting", downloadURL.toString());
            Logger.newLine();
            final AtomicLong atomicLong = new AtomicLong(0);
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    final long finished = target.length();
                    final long total = request.getConnection().getContentLength();
                    final long speed = finished - atomicLong.get();
                    atomicLong.set(finished);
                    Logger.bar((float) (finished * 1.0 / total), displayableBytes(finished) + "/" +
                            displayableBytes(total) + " (" + displayableBytes(speed) + "/s)");
                    if (finished == total) {
                        this.cancel();
                    }
                }
            }, 500, 500);
            request.receive(target);
            Logger.clearUpLine();
            Logger.trInfo("display.success.download", displayName);
        } catch (Exception e) {
            Logger.trWarn("display.fail.download", displayName);
        }
    }
}
