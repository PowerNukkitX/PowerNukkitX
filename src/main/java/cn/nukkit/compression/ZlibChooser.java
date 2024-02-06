package cn.nukkit.compression;

import cn.nukkit.Server;
import cn.nukkit.lang.BaseLang;
import cn.nukkit.utils.TextFormat;
import cn.powernukkitx.libdeflate.Libdeflate;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.zip.Deflater;

@Slf4j
public final class ZlibChooser {
    private static final int MAX_INFLATE_LEN = 1024 * 1024 * 10;
    private static final ZlibProvider[] providers;
    private static ZlibProvider provider;

    static {
        providers = new ZlibProvider[4];
        providers[2] = new ZlibThreadLocal();
        provider = providers[2];
    }

    public static void setProvider(int providerIndex) {
        var lang = Server.getInstance() == null ? new BaseLang("eng") : Server.getInstance().getLanguage();
        switch (providerIndex) {
            case 0:
                if (providers[providerIndex] == null)
                    providers[providerIndex] = new ZlibOriginal();
                break;
            case 1:
                if (providers[providerIndex] == null)
                    providers[providerIndex] = new ZlibSingleThreadLowMem();
                break;
            case 2:
                if (providers[providerIndex] == null)
                    providers[providerIndex] = new ZlibThreadLocal();
                if (Libdeflate.isAvailable())
                    log.info(TextFormat.WHITE + lang.tr("nukkit.zlib.acceleration-can-enable"));
                break;
            case 3:
                if (Libdeflate.isAvailable()) {
                    if (providers[providerIndex] == null) {
                        providers[providerIndex] = new LibDeflateThreadLocal();
                    }
                } else {
                    log.warn(lang.tr("nukkit.zlib.unavailable"));
                    providerIndex = 2;
                    if (providers[providerIndex] == null)
                        providers[providerIndex] = new ZlibThreadLocal();
                }
                break;
            default:
                throw new UnsupportedOperationException("Invalid provider: " + providerIndex);
        }
        if (providerIndex < 2) {
            log.warn(lang.tr("nukkit.zlib.affect-performance"));
        }
        provider = providers[providerIndex];
        log.info(lang.tr("nukkit.zlib.selected") + ": {} ({})", providerIndex, provider.getClass().getCanonicalName());
    }


    public static byte[] deflate(byte[] data, boolean raw) throws IOException {
        return deflate(data, Deflater.DEFAULT_COMPRESSION, raw);
    }


    public static byte[] deflate(byte[] data, int level, boolean raw) throws IOException {
        return provider.deflate(data, level, raw);
    }

    public static byte[] inflate(byte[] data, boolean raw) throws IOException {
        return inflate(data, MAX_INFLATE_LEN, raw);
    }

    public static byte[] inflate(byte[] data, int maxSize, boolean raw) throws IOException {
        return provider.inflate(data, maxSize, raw);
    }
}
