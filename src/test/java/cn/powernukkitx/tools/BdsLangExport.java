package cn.powernukkitx.tools;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

public class BdsLangExport {
    static final String TARGET = "D:/Minecraft/bedrock-server-1.19.50.02/resource_packs/vanilla/texts";

    public static void main(String[] args) throws IOException, URISyntaxException {
        var langs = new File("src/main/resources/language").listFiles();
        for (var file : Objects.requireNonNull(langs)) {
            if (file.getName().length() == 3 && pathMapping(file.getName(), TARGET) != null) {
                var path = pathMapping(file.getName(), TARGET);
                if (!path.toFile().exists()) continue;
                for (var line : Files.readAllLines(path)) {
                    if (line.startsWith("commands")) {
                        line = line.replaceAll("\\$.", "").transform(s -> {
                            StringBuilder builder = new StringBuilder();
                            var array = s.toCharArray();
                            int ds = 0;
                            for (int i = 0; i < array.length; ) {
                                if (array[i] == '%') {
                                    try {
                                        var index = Integer.parseInt(String.valueOf(array[i + 1]));
                                        builder.append('{').append(array[i]).append(index - 1).append('}');
                                        ds++;
                                    } catch (NumberFormatException e) {
                                        if (array[i + 1] == 's' || array[i + 1] == 'd') {
                                            builder.append('{').append(array[i]).append(ds).append('}');
                                            ds++;
                                        } else builder.append('{').append(array[i]).append(array[i + 1]).append('}');
                                    }
                                    i += 2;
                                } else {
                                    builder.append(array[i]);
                                    i++;
                                }
                            }
                            var str = builder.toString();
                            var last = str.indexOf('#');
                            return str.substring(0, last == -1 ? str.length() : last);
                        });
                        var save = file.toPath().resolve("add.ini").toFile();
                        if (!save.exists()) save.createNewFile();
                        Files.writeString(save.toPath(), line + "\n", StandardCharsets.UTF_8, StandardOpenOption.APPEND);
                    }
                }
            }
        }
    }

    public static Path pathMapping(String fileName, String targetFile) {
        return switch (fileName) {
            case "bra" -> Path.of(targetFile).resolve("pt_BR.lang");
            case "chs" -> Path.of(targetFile).resolve("zh_CN.lang");
            case "cht" -> Path.of(targetFile).resolve("zh_TW.lang");
            case "cze" -> Path.of(targetFile).resolve("cs_CZ.lang");
            case "deu" -> Path.of(targetFile).resolve("de_DE.lang");
            case "fin" -> Path.of(targetFile).resolve("fi_FI.lang");
            case "eng" -> Path.of(targetFile).resolve("en_US.lang");
            case "fra" -> Path.of(targetFile).resolve("en_US.lang");
            case "idn" -> Path.of(targetFile).resolve("id_ID.lang");
            case "jpn" -> Path.of(targetFile).resolve("ja_JP.lang");
            case "kor" -> Path.of(targetFile).resolve("ko_KR.lang");
            case "ltu" -> Path.of(targetFile).resolve("en_US.lang");
            case "pol" -> Path.of(targetFile).resolve("pl_PL.lang");
            case "rus" -> Path.of(targetFile).resolve("ru_RU.lang");
            case "spa" -> Path.of(targetFile).resolve("es_ES.lang");
            case "tur" -> Path.of(targetFile).resolve("tr_TR.lang");
            case "ukr" -> Path.of(targetFile).resolve("uk_UA.lang");
            case "vie" -> Path.of(targetFile).resolve("en_US.lang");
            default -> null;
        };
    }
}
