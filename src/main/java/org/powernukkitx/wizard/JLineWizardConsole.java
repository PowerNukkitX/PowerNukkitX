package org.powernukkitx.wizard;

import lombok.extern.slf4j.Slf4j;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.ParsedLine;
import org.jline.terminal.Terminal;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * {@link WizardConsole} backed by a JLine terminal, with TAB completion of language codes.
 */
@Slf4j
final class JLineWizardConsole implements WizardConsole {
    private final Terminal terminal;
    private final boolean ownsTerminal;
    private final LineReader reader;
    private final LanguageCompleter completer;

    /**
     * @param terminal      the terminal to read from and write to
     * @param ownsTerminal  whether {@link #close()} closes the terminal; false when it is shared with the logger
     * @param languageCodes the candidates offered on TAB until {@link #disableCompletion()} is called
     */
    JLineWizardConsole(Terminal terminal, boolean ownsTerminal, Set<String> languageCodes) {
        this.terminal = terminal;
        this.ownsTerminal = ownsTerminal;
        this.completer = new LanguageCompleter(languageCodes);
        this.reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .completer(completer)
                .option(LineReader.Option.CASE_INSENSITIVE, true)
                .option(LineReader.Option.AUTO_LIST, true)
                .option(LineReader.Option.LIST_PACKED, true)
                .build();
    }

    @Override
    public String readLine(String prompt) {
        return reader.readLine(prompt);
    }

    @Override
    public void println(String text) {
        terminal.writer().println(text);
    }

    @Override
    public void flush() {
        terminal.writer().flush();
    }

    @Override
    public void disableCompletion() {
        completer.enabled = false;
    }

    @Override
    public void close() {
        if (!ownsTerminal) {
            return;
        }
        try {
            terminal.close();
        } catch (IOException e) {
            log.error("Error closing terminal", e);
        }
    }

    private static final class LanguageCompleter implements Completer {
        private final Set<String> languageCodes;
        private boolean enabled = true;

        private LanguageCompleter(Set<String> languageCodes) {
            this.languageCodes = languageCodes;
        }

        @Override
        public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
            if (!enabled) {
                return;
            }
            String wordLower = line.word().toLowerCase(Locale.ENGLISH);
            for (String langCode : languageCodes) {
                if (langCode.startsWith(wordLower)) {
                    candidates.add(new Candidate(langCode));
                }
            }
        }
    }
}
