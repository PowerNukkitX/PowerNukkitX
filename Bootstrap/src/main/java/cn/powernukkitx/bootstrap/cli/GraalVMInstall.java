package cn.powernukkitx.bootstrap.cli;

import cn.powernukkitx.bootstrap.CLI;
import cn.powernukkitx.bootstrap.util.Logger;
import cn.powernukkitx.bootstrap.util.URLUtils;
import com.github.kevinsawicki.http.HttpRequest;
import net.lingala.zip4j.ZipFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

import static cn.powernukkitx.bootstrap.util.ConfigUtils.graalvmVersion;
import static cn.powernukkitx.bootstrap.util.StringUtils.displayableBytes;

@SuppressWarnings("DuplicatedCode")
public final class GraalVMInstall implements Component {
    private CLI cli;

    @Override
    public void execute(CLI cli, Object... args) {
        this.cli = cli;
        downloadGraalVM17();
        uncompressGraalVM17();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void downloadGraalVM17() {
        Logger.trInfo("display.install-java17");
        final File localJavaDir = new File("./java");
        if (!localJavaDir.exists()) {
            localJavaDir.mkdirs();
        }
        final URL downloadURL = URLUtils.graal17URL();
        if (downloadURL == null) {
            Logger.trWarn("display.fail.install-java17");
            return;
        }
        try {
            final File targetZip = new File("tmp.zip");
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

    private void uncompressGraalVM17() {
        final File file = new File("tmp.zip");
        Logger.trInfo("display.uncompressing", file.getAbsolutePath());
        final ZipFile zipFile = new ZipFile(file);
        try {
            zipFile.extractFile("graalvm-ce-java17-" + graalvmVersion() + "/", "./java");
            Logger.trInfo("display.success.uncompress", file.getAbsolutePath());
            zipFile.close();
            //noinspection ResultOfMethodCallIgnored
            file.delete();
        } catch (IOException e) {
            Logger.trInfo("display.fail.uncompress", file.getAbsolutePath());
        }
    }
}
