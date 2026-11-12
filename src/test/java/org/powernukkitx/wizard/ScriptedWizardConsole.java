package org.powernukkitx.wizard;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * {@link WizardConsole} fed from a fixed list of answers. Prompts and printed lines are recorded in order.
 * Running out of answers raises an {@link AssertionError}, which the wizard's own {@code catch (Exception)}
 * blocks cannot swallow, so a wizard that asks more than the test scripted always fails the test.
 */
class ScriptedWizardConsole implements WizardConsole {
    private final Deque<String> answers;
    private final List<String> output = new ArrayList<>();
    private int reads;

    ScriptedWizardConsole(String... answers) {
        this.answers = new ArrayDeque<>(List.of(answers));
    }

    @Override
    public String readLine(String prompt) {
        output.add(prompt);
        if (answers.isEmpty()) {
            throw new AssertionError("No scripted answer left for prompt: " + prompt);
        }
        reads++;
        return answers.poll();
    }

    @Override
    public void println(String text) {
        output.add(text);
    }

    @Override
    public void flush() {
    }

    List<String> output() {
        return output;
    }

    String transcript() {
        return String.join("\n", output);
    }

    int reads() {
        return reads;
    }

    int remaining() {
        return answers.size();
    }
}
