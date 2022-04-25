package cn.powernukkitx.bootstrap.util;

import org.kamranzafar.jtar.TarEntry;
import org.kamranzafar.jtar.TarInputStream;

import java.io.*;
import java.util.zip.GZIPInputStream;

@SuppressWarnings("ResultOfMethodCallIgnored")
public final class GzipUtils {
    private static final int BUFFER = 4096;

    public static void uncompressTGzipFile(File source, File target) throws IOException {
        TarInputStream tis = new TarInputStream(new BufferedInputStream(new GZIPInputStream(new FileInputStream(source))));
        unTar(tis, target.getAbsolutePath());
        tis.close();
    }

    private static void unTar(TarInputStream tis, String destFolder) throws IOException {
        BufferedOutputStream dest;

        TarEntry entry;
        while ((entry = tis.getNextEntry()) != null) {
            int count;
            byte[] data = new byte[BUFFER];

            if (entry.isDirectory()) {
                new File(destFolder + "/" + entry.getName()).mkdirs();
                continue;
            } else {
                int di = entry.getName().lastIndexOf('/');
                if (di != -1) {
                    new File(destFolder + "/" + entry.getName().substring(0, di)).mkdirs();
                }
            }

            FileOutputStream fos = new FileOutputStream(destFolder + "/" + entry.getName());
            dest = new BufferedOutputStream(fos);

            while ((count = tis.read(data)) != -1) {
                dest.write(data, 0, count);
            }

            dest.flush();
            dest.close();
        }
    }
}
