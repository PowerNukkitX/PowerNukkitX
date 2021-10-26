package org.powernukkit.updater;

import lombok.SneakyThrows;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author joserobjr
 * @since 2021-10-24
 */
public class AllResourcesDownloader {
    public static void main(String[] args) {
        /*
        Pre-requisites:
        - Make sure that ProxyPass is updated and working with the last Minecraft Bedrock Edition client
        - Make sure PocketMine has released their exports: https://github.com/pmmp/BedrockData
        - Run ProxyPass with export-data in config.yml set to true, the proxy pass must be
            pointing to a vanilla BDS server from https://www.minecraft.net/en-us/download/server/bedrock
        - Connect to the ProxyPass server with the last Minecraft Bedrock Edition client at least once
        - Adjust the path bellow if necessary for your machine
         */
        new AllResourcesDownloader().execute("../Bedrock-ProxyPass/run/data");
        System.out.println("OK");
    }

    private void execute(@SuppressWarnings("SameParameterValue") String pathProxyPassData) {
        downloadResources();
        copyProxyPassResources(pathProxyPassData);
    }

    private void downloadResources() {
        download("https://github.com/pmmp/BedrockData/raw/master/canonical_block_states.nbt",
                "src/main/resources/canonical_block_states.nbt");
        download("https://github.com/pmmp/BedrockData/raw/master/required_item_list.json",
                "src/test/resources/org/powernukkit/updater/dumps/pmmp/required_item_list.json");
    }

    private void copyProxyPassResources(String pathProxyPassData) {
        copy(pathProxyPassData, "biome_definitions.dat", "src/main/resources/biome_definitions.dat");
        copy(pathProxyPassData, "entity_identifiers.dat", "src/main/resources/entity_identifiers.dat");
        copy(pathProxyPassData, "creativeitems.json", "src/main/resources/creativeitems.json");
        copy(pathProxyPassData, "runtime_item_states.json", "src/test/resources/org/powernukkit/updater/dumps/proxypass/runtime_item_states.json");
        copy(pathProxyPassData, "recipes.json", "src/test/resources/org/powernukkit/updater/dumps/proxypass/runtime_item_states.json");
    }

    private void copy(String path, String file, String into) {

    }

    @SneakyThrows
    private void download(String url, String into) {
        URLConnection connection = new URL(url).openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        try(InputStream input = connection.getInputStream();
            OutputStream fos = new FileOutputStream(into);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
        ) {
            byte[] buffer = new byte[8*1024];
            int read;
            while ((read = input.read(buffer)) != -1) {
                bos.write(buffer, 0, read);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
