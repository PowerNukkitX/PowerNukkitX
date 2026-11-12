package org.powernukkitx.wizard;

import org.powernukkitx.lang.BaseLang;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Everything the {@link SetupWizard} reads from the machine it runs on, gathered so tests can replace it.
 *
 * @param interactive     whether a person can answer prompts; when false the wizard never reads input
 * @param unicodeOutput   whether the output charset can encode the decorative characters
 * @param languages       language code to display name, in the order offered to the user
 * @param langLoader      loads the translations for a language code
 * @param localAddress    resolves the address of this machine on its network, may block
 * @param externalAddress resolves the public address of this machine, may block
 */
record WizardEnvironment(boolean interactive,
                         boolean unicodeOutput,
                         Map<String, String> languages,
                         Function<String, BaseLang> langLoader,
                         Supplier<String> localAddress,
                         Supplier<String> externalAddress) {
}
