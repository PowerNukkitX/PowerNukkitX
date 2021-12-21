package cn.nukkit.command;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author joserobjr
 * @since 2021-12-20
 */
@PowerNukkitOnly
@Since("FUTURE")
@ExtendWith(PowerNukkitExtension.class)
class CapturingCommandSenderTest {
    CapturingCommandSender commandSender;

    @BeforeEach
    void setUp() {
        commandSender = new CapturingCommandSender();
    }

    @Test
    void getterSetter() {
        assertEquals("System", commandSender.getName());
        assertFalse(commandSender.isOp());

        commandSender.setName("Test");
        assertEquals("Test", commandSender.getName());
        assertFalse(commandSender.isOp());

        commandSender.setOp(true);
        assertEquals("Test", commandSender.getName());
        assertTrue(commandSender.isOp());

        commandSender.setOp(false);
        assertEquals("Test", commandSender.getName());
        assertFalse(commandSender.isOp());
    }
}
