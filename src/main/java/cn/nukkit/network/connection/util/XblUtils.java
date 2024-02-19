package cn.nukkit.network.connection.util;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import lombok.experimental.UtilityClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

@UtilityClass
public class XblUtils {
    private static final InternalLogger log = InternalLoggerFactory.getInstance(XblUtils.class);

    private static final String TOKEN_URL_STRING = "https://login.live.com/oauth20.srf";
    private static final String REQUEST_URL_STRING = "https://login.live.com/oauth20_authorize.srf?client_id=00000000441cc96b&redirect_uri=https://login.live.com/oauth20_desktop.srf&response_type=token&display=touch&scope=service::user.auth.xboxlive.com::MBI_SSL&locale=en";
    private static final URL TOKEN_URL;
    private static final URL REQUEST_URL;

    static {
        try {
            TOKEN_URL = new URL(TOKEN_URL_STRING);
            REQUEST_URL = new URL(REQUEST_URL_STRING);
        } catch (MalformedURLException e) {
            throw new AssertionError("Unable to create XBL URLs", e);
        }
    }

    public static CompletableFuture<Object> test() throws IOException {
        CompletableFuture<?> future = new CompletableFuture<>();
        ForkJoinPool.commonPool().execute(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) TOKEN_URL.openConnection();
                connection.setRequestMethod("GET");

                String response;
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder buffer = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        buffer.append(line);
                    }
                    response = buffer.toString();
                }

                log.debug("RESPONSE\n{}", response);
            } catch (Throwable e) {
                future.completeExceptionally(e);
            }
        });

        return CompletableFuture.supplyAsync(() -> {
            return null;
        });
    }

}
