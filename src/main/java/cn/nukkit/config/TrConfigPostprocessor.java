package cn.nukkit.config;

import cn.nukkit.Server;
import eu.okaeri.configs.postprocessor.ConfigContextManipulator;
import eu.okaeri.configs.postprocessor.ConfigLineFilter;
import eu.okaeri.configs.postprocessor.ConfigLineInfo;
import eu.okaeri.configs.postprocessor.ConfigSectionWalker;
import lombok.Cleanup;
import lombok.Data;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@Data
public class TrConfigPostprocessor {

    private String context;

    public static TrConfigPostprocessor of(@NotNull InputStream inputStream) {
        TrConfigPostprocessor postprocessor = new TrConfigPostprocessor();
        postprocessor.setContext(readInput(inputStream));
        return postprocessor;
    }

    public static TrConfigPostprocessor of(@NotNull String context) {
        TrConfigPostprocessor postprocessor = new TrConfigPostprocessor();
        postprocessor.setContext(context);
        return postprocessor;
    }

    public static int countIndent(@NotNull String line) {
        int whitespaces = 0;
        for (char c : line.toCharArray()) {
            if (!Character.isWhitespace(c)) {
                return whitespaces;
            }
            whitespaces++;
        }
        return whitespaces;
    }

    public static String addIndent(@NotNull String line, int size) {
        String indent = " ".repeat(Math.max(0, size));

        return Arrays.stream(line.split("\n"))
                .map(part -> indent + part)
                .collect(Collectors.joining("\n"))
                + "\n";
    }

    public static String createCommentOrEmpty(String commentPrefix, String[] strings) {
        return (strings == null) ? "" : createComment(commentPrefix, strings);
    }

    public static String createComment(String commentPrefix, String[] strings) {
        if (strings == null) return null;
        if (commentPrefix == null) commentPrefix = "";

        List<String> newLines = new ArrayList<>();
        for (String line : strings) {
            String trLine = Server.getInstance().getLanguage().tr(line);
            String prefix = trLine.startsWith(commentPrefix.trim()) ? "" : commentPrefix;
            String result = (trLine.isEmpty() ? "" : prefix) + trLine;

            String[] parts = result.split("\n");
            if (parts.length != 0) {
                for (var p : parts) {
                    prefix = p.startsWith(commentPrefix.trim()) ? "" : commentPrefix;
                    newLines.add((p.isEmpty() ? "" : prefix) + p);
                }
            } else {
                newLines.add(result);
            }
        }

        return String.join("\n", newLines) + "\n";
    }

    private static String readInput(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).lines()
                .collect(Collectors.joining("\n"));
    }

    @SneakyThrows
    private static void writeOutput(OutputStream outputStream, String text) {
        @Cleanup PrintStream out = new PrintStream(outputStream, true, StandardCharsets.UTF_8.name());
        out.print(text);
    }

    public TrConfigPostprocessor write(@NotNull OutputStream outputStream) {
        writeOutput(outputStream, this.context);
        return this;
    }

    public TrConfigPostprocessor removeLines(@NotNull ConfigLineFilter filter) {

        String[] lines = this.context.split("\n");
        StringBuilder buf = new StringBuilder();

        for (String line : lines) {
            if (filter.remove(line)) {
                continue;
            }
            buf.append(line).append("\n");
        }

        this.context = buf.toString();
        return this;
    }

    public TrConfigPostprocessor removeLinesUntil(@NotNull Predicate<String> shouldStop) {

        String[] lines = this.context.split("\n");

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (!shouldStop.test(line)) {
                continue;
            }
            String[] remaining = Arrays.copyOfRange(lines, i, lines.length);
            this.context = String.join("\n", remaining);
            break;
        }

        return this;
    }

    public TrConfigPostprocessor updateLines(@NotNull ConfigContextManipulator manipulator) {

        String[] lines = this.context.split("\n");
        StringBuilder buf = new StringBuilder();

        for (String line : lines) {
            buf.append(manipulator.convert(line)).append("\n");
        }

        this.context = buf.toString();
        return this;
    }

    public TrConfigPostprocessor updateLinesKeys(@NotNull ConfigSectionWalker walker) {
        try {
            return this.updateLinesKeys0(walker);
        } catch (Exception exception) {
            throw new RuntimeException("failed to #updateLinesKeys for context:\n" + this.context, exception);
        }
    }

    private TrConfigPostprocessor updateLinesKeys0(@NotNull ConfigSectionWalker walker) {

        String[] lines = this.context.split("\n");
        List<ConfigLineInfo> currentPath = new ArrayList<>();
        int lastIndent = 0;
        int level = 0;
        StringBuilder newContext = new StringBuilder();
        boolean multilineSkip = false;

        for (String line : lines) {

            int indent = countIndent(line);
            int change = indent - lastIndent;
            String key = walker.readName(line);

            // skip non-keys
            if (!walker.isKey(line)) {
                newContext.append(line).append("\n");
                multilineSkip = false;
                continue;
            }

            if (currentPath.isEmpty()) {
                currentPath.add(ConfigLineInfo.of(indent, change, key));
            }

            if (change > 0) {
                if (!multilineSkip) {
                    level++;
                    currentPath.add(ConfigLineInfo.of(indent, change, key));
                }
            } else {
                if (change != 0) {
                    ConfigLineInfo lastLineInfo = currentPath.getLast();
                    int step = lastLineInfo.getIndent() / level;
                    level -= ((change * -1) / step);
                    currentPath = currentPath.subList(0, level + 1);
                    multilineSkip = false;
                }
                if (!multilineSkip) {
                    currentPath.set(currentPath.size() - 1, ConfigLineInfo.of(indent, change, key));
                }
            }

            if (multilineSkip) {
                newContext.append(line).append("\n");
                continue;
            } else if (walker.isKeyMultilineStart(line)) {
                multilineSkip = true;
            }

            lastIndent = indent;
            String updatedLine = walker.update(line, currentPath.getLast(), currentPath);
            newContext.append(updatedLine).append("\n");
        }

        this.context = newContext.toString();
        return this;
    }

    public TrConfigPostprocessor updateContext(@NotNull ConfigContextManipulator manipulator) {
        this.context = manipulator.convert(this.context);
        return this;
    }

    public TrConfigPostprocessor prependContextComment(String prefix, String[] strings) {
        if (strings != null) this.context = createComment(prefix, strings) + this.context;
        return this;
    }

    public TrConfigPostprocessor appendContextComment(String prefix, String[] strings) {
        return this.appendContextComment(prefix, "", strings);
    }

    public TrConfigPostprocessor appendContextComment(String prefix, String separator, String[] strings) {
        if (strings != null) this.context += separator + createComment(prefix, strings);
        return this;
    }
}
