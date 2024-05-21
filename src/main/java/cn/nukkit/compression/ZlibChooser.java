package cn.nukkit.compression;

import cn.nukkit.Server;
import cn.nukkit.lang.BaseLang;
import cn.nukkit.utils.TextFormat;
import cn.powernukkitx.libdeflate.Libdeflate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ZlibChooser {
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
                if (Libdeflate.isAvailable()) {
                    log.info("{}{}", TextFormat.WHITE, lang.tr("nukkit.zlib.acceleration-can-enable"));
                }
                break;
            case 3:
                if (Libdeflate.isAvailable()) {
                    if (providers[providerIndex] == null) {
                        LibDeflateThreadLocal libDeflateThreadLocal = new LibDeflateThreadLocal((ZlibThreadLocal) providers[2]);
                        providers[providerIndex] = libDeflateThreadLocal;
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
        log.info("{}: {} ({})", lang.tr("nukkit.zlib.selected"), providerIndex, provider.getClass().getCanonicalName());
    }

    public static ZlibProvider getCurrentProvider() {
        return provider;
    }
}
