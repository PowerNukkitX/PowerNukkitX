package cn.powernukkitx.bootstrap.cli;

import cn.powernukkitx.bootstrap.CLI;
import cn.powernukkitx.bootstrap.util.GzipUtils;
import cn.powernukkitx.bootstrap.util.Logger;
import cn.powernukkitx.bootstrap.util.StringUtils;
import cn.powernukkitx.bootstrap.util.URLUtils;
import com.github.kevinsawicki.http.HttpRequest;
import net.lingala.zip4j.ZipFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

import static cn.powernukkitx.bootstrap.util.ConfigUtils.adoptOpenJDKVersion;
import static cn.powernukkitx.bootstrap.util.ConfigUtils.graalvmVersion;
import static cn.powernukkitx.bootstrap.util.StringUtils.displayableBytes;

@SuppressWarnings("DuplicatedCode")
public final class AdoptOpenJDKInstall implements Component {
    private CLI cli;
    private String suffix = "tar.gz";

    @Override
    public void execute(CLI cli, Object... args) {
        this.cli = cli;
        downloadAdoptOpenJDK17();
        uncompressAdoptOpenJDK17();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void downloadAdoptOpenJDK17() {
        final File localJavaDir = new File("./java");
        if (!localJavaDir.exists()) {
            localJavaDir.mkdirs();
        }
        final URL downloadURL = URLUtils.adopt17URL();
        if (downloadURL == null) {
            Logger.trWarn("display.fail.install-java17");
            return;
        }
        try {
            suffix = StringUtils.uriSuffix(downloadURL);
            final File targetZip = new File("tmp." + suffix);
            HttpRequest request = HttpRequest.get(downloadURL);
            Logger.trInfo("display.connecting", downloadURL.toString());
            final AtomicLong atomicLong = new AtomicLong(0);
            cli.timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    final long total = request.getConnection().getContentLength();
                    final long finished = targetZip.length();
                    final long speed = finished - atomicLong.get();
                    atomicLong.set(finished);
                    Logger.bar((float) (finished * 1.0 / total), displayableBytes(finished) + "/" +
                            displayableBytes(total) + " (" + displayableBytes(speed) + "/s)");
                    if (finished == total) {
                        Logger.trInfo("display.success.download", "Java17");
                        this.cancel();
                    }
                }
            }, 500, 500);
            request.receive(targetZip);
        } catch (Exception e) {
            Logger.trWarn("display.fail.install-java17");
        }
    }

    private void uncompressAdoptOpenJDK17() {
        final File file = new File("tmp." + suffix);
        Logger.trInfo("display.uncompressing", file.getAbsolutePath());
        if ("zip".equals(suffix)) {
            final ZipFile zipFile = new ZipFile(file);
            try {
                final String name = "jdk-" + adoptOpenJDKVersion().replace("_", "+") + "-jre";
                zipFile.extractFile(name + "/", "./java");
                zipFile.close();
                Logger.trInfo("display.success.uncompress", file.getAbsolutePath());
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            } catch (IOException e) {
                Logger.trInfo("display.fail.uncompress", file.getAbsolutePath());
            }
        } else if ("tar.gz".equals(suffix)) {
            try {
                GzipUtils.uncompressTGzipFile(file, new File("./java"));
                Logger.trInfo("display.success.uncompress", file.getAbsolutePath());
            } catch (IOException e) {
                Logger.trInfo("display.fail.uncompress", file.getAbsolutePath());
            }
        }
    }
}
