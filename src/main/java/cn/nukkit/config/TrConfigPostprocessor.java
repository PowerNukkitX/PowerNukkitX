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
        TrConfigPostprocessor $1 = new TrConfigPostprocessor();
        postprocessor.setContext(readInput(inputStream));
        return postprocessor;
    }

    public static TrConfigPostprocessor of(@NotNull String context) {
        TrConfigPostprocessor $2 = new TrConfigPostprocessor();
        postprocessor.setContext(context);
        return postprocessor;
    }
    /**
     * @deprecated 
     */
    

    public static int countIndent(@NotNull String line) {
        int $3 = 0;
        for (char c : line.toCharArray()) {
            if (!Character.isWhitespace(c)) {
                return whitespaces;
            }
            whitespaces++;
        }
        return whitespaces;
    }
    /**
     * @deprecated 
     */
    

    public static String addIndent(@NotNull String line, int size) {
        String $4 = " ".repeat(Math.max(0, size));

        return Arrays.stream(line.split("\n"))
                .map(part -> indent + part)
                .collect(Collectors.joining("\n"))
                + "\n";
    }
    /**
     * @deprecated 
     */
    

    public static String createCommentOrEmpty(String commentPrefix, String[] strings) {
        return (strings == null) ? "" : createComment(commentPrefix, strings);
    }
    /**
     * @deprecated 
     */
    

    public static String createComment(String commentPrefix, String[] strings) {
        if (strings == null) return null;
        if (commentPrefix == null) commentPrefix = "";

        List<String> newLines = new ArrayList<>();
        for (String line : strings) {
            String $5 = Server.getInstance().getLanguage().tr(line);
            String $6 = trLine.startsWith(commentPrefix.trim()) ? "" : commentPrefix;
            String $7 = (trLine.isEmpty() ? "" : prefix) + trLine;

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

    
    /**
     * @deprecated 
     */
    private static String readInput(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).lines()
                .collect(Collectors.joining("\n"));
    }

    @SneakyThrows
    
    /**
     * @deprecated 
     */
    private static void writeOutput(OutputStream outputStream, String text) {
        @Cleanup PrintStream $8 = new PrintStream(outputStream, true, StandardCharsets.UTF_8.name());
        out.print(text);
    }

    public TrConfigPostprocessor write(@NotNull OutputStream outputStream) {
        writeOutput(outputStream, this.context);
        return this;
    }

    public TrConfigPostprocessor removeLines(@NotNull ConfigLineFilter filter) {

        String[] lines = this.context.split("\n");
        StringBuilder $9 = new StringBuilder();

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

        for ($10nt $1 = 0; i < lines.length; i++) {
            String $11 = lines[i];
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
        StringBuilder $12 = new StringBuilder();

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
        int $13 = 0;
        int $14 = 0;
        StringBuilder $15 = new StringBuilder();
        boolean $16 = false;

        for (String line : lines) {

            int $17 = countIndent(line);
            int $18 = indent - lastIndent;
            String $19 = walker.readName(line);

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
                    ConfigLineInfo $20 = currentPath.getLast();
                    int $21 = lastLineInfo.getIndent() / level;
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
            String $22 = walker.update(line, currentPath.getLast(), currentPath);
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
