package org.powernukkitx.wizard;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Pure POJO coverage for {@link WizardConfig}. The interactive {@link SetupWizard}
 * itself is skipped - it opens a JLine system terminal and reads from stdin, which
 * needs a live console.
 */
public class WizardConfigTest {

    @Test
    void defaultsAndSetters() {
        WizardConfig config = new WizardConfig();

        Assertions.assertEquals("eng", config.getLanguage());
        Assertions.assertFalse(config.isLicenseAccepted());
        Assertions.assertEquals(19132, config.getPort());
        Assertions.assertEquals("PowerNukkitX Server", config.getMotd());
        Assertions.assertEquals(0, config.getGamemode());
        Assertions.assertEquals(20, config.getMaxPlayers());
        Assertions.assertFalse(config.isWhitelistEnabled());
        Assertions.assertTrue(config.getWhitelistedPlayers().isEmpty());
        Assertions.assertTrue(config.getOperators().isEmpty());
        Assertions.assertTrue(config.isQueryEnabled());

        config.setLanguage("tur");
        config.setLicenseAccepted(true);
        config.setPort(25565);
        config.setMotd("Custom");
        config.setGamemode(1);
        config.setMaxPlayers(50);
        config.setWhitelistEnabled(true);
        config.setWhitelistedPlayers(List.of("alice"));
        config.setOperators(List.of("bob"));
        config.setQueryEnabled(false);

        Assertions.assertEquals("tur", config.getLanguage());
        Assertions.assertTrue(config.isLicenseAccepted());
        Assertions.assertEquals(25565, config.getPort());
        Assertions.assertEquals("Custom", config.getMotd());
        Assertions.assertEquals(1, config.getGamemode());
        Assertions.assertEquals(50, config.getMaxPlayers());
        Assertions.assertTrue(config.isWhitelistEnabled());
        Assertions.assertEquals(List.of("alice"), config.getWhitelistedPlayers());
        Assertions.assertEquals(List.of("bob"), config.getOperators());
        Assertions.assertFalse(config.isQueryEnabled());
    }
}
