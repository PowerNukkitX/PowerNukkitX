package cn.powernukkitx.updater;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class BdsLangExport {
    static final String TARGET = "D:/Minecraft/bedrock-server-1.20.0.01/resource_packs/vanilla/texts";
    static Pattern pattern = Pattern.compile("%[0-9]");

    public static void main(String[] args) throws IOException, URISyntaxException {
        var langs = new File("src/main/resources/language").listFiles();
        for (var file : Objects.requireNonNull(langs)) {
            if (file.getName().length() == 3 && pathMapping(file.getName(), TARGET) != null) {
                var path = pathMapping(file.getName(), TARGET);
                if (!path.toFile().exists()) continue;
                Path output = file.toPath().resolve("lang.ini");
                List<String> olds = Files.readAllLines(output);
                List<String> news = new ArrayList<>();
                for (var line : Files.readAllLines(path)) {
                    if (line.startsWith("commands")) {
                        line = line.replaceAll("\\$.", "").transform(s -> {
                            AtomicInteger ds = new AtomicInteger();
                            s = pattern.matcher(s).replaceAll(matchResult -> {
                                int number = Integer.parseInt(String.valueOf(matchResult.group().charAt(1)));
                                ds.getAndIncrement();
                                return "{%" + (number - 1) + "}";
                            });
                            var array = s.toCharArray();
                            StringBuilder builder = new StringBuilder(String.valueOf(array[0]));
                            for (int i = 1; i < array.length; ) {
                                if (array[i] == '%' && array[i - 1] != '{') {
                                    char c = array[i + 1];
                                    if (c == 's' || c == 'd') {
                                        builder.append('{').append('%').append(ds.getAndIncrement()).append('}');
                                        i += 2;
                                    } else if (c == '.') {
                                        builder.append('{').append('%').append(ds.getAndIncrement()).append('}');
                                        i += 4;
                                    } else {
                                        i++;
                                    }
                                } else {
                                    builder.append(array[i]);
                                    i++;
                                }
                            }
                            var str = builder.toString();
                            var last = str.indexOf('#');
                            return str.substring(0, last == -1 ? str.length() : last);
                        });
                        news.add(line);
                    }
                }
                int startIndex = -1;
                int endIndex = -1;
                for (int i = 0; i < olds.size(); i++) {
                    if (olds.get(i).contains("#VanillaCommandsStart")) {
                        startIndex = i;
                    }
                    if (olds.get(i).contains("#VanillaCommandsEnd")) {
                        endIndex = i;
                    }
                }
                if (startIndex == -1 || endIndex == -1) {
                    System.out.println("error lang.ini: " + file.getName());
                    return;
                }
                var result = new ArrayList<String>();
                result.addAll(olds.subList(0, startIndex + 1));
                result.addAll(news);
                result.addAll(olds.subList(endIndex, olds.size()));
                Files.write(output, result, StandardCharsets.UTF_8);
            }
        }
        System.out.println("success update lang.ini");
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
