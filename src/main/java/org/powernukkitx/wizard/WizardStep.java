package org.powernukkitx.wizard;

import java.util.function.Supplier;

/**
 * One question of the {@link SetupWizard}. The same list of steps drives the first walk through the
 * questions and the recap, so a setting is asked and displayed the same way in both.
 *
 * @param labelKey     translation key of the setting's name
 * @param presetByArg  whether the value came from the command line, in which case the walk does not ask it
 * @param displayValue the current value as shown in the recap
 * @param ask          prompts for the setting and stores the answer
 * @param confirm      prints the confirmation line for the current value
 */
record WizardStep(String labelKey,
                  boolean presetByArg,
                  Supplier<String> displayValue,
                  Runnable ask,
                  Runnable confirm) {
}
