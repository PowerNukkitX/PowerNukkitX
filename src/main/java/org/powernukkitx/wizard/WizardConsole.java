package org.powernukkitx.wizard;

/**
 * Line-based input and output of the {@link SetupWizard}.
 * <p>
 * Keeping the wizard behind this interface lets its flow run against a scripted console in unit tests,
 * without a JLine terminal.
 */
interface WizardConsole extends AutoCloseable {

    /**
     * Shows the prompt and blocks until a line is entered.
     *
     * @param prompt the text displayed before the cursor
     * @return the line entered, without its line terminator
     * @throws RuntimeException if the input is closed or the read is interrupted
     */
    String readLine(String prompt);

    void println(String text);

    void flush();

    /**
     * Stops offering TAB completion. Called once the language is chosen, the only prompt that has candidates.
     */
    default void disableCompletion() {
    }

    @Override
    default void close() {
    }
}
