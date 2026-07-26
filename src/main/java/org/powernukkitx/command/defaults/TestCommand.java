package org.powernukkitx.command.defaults;

import org.powernukkitx.command.Command;
import org.powernukkitx.command.data.NukkitCommandData;

/**
 * Base class for test commands
 */


public abstract class TestCommand extends Command {

    public TestCommand(String name) {
        this(name, "");
    }

    public TestCommand(String name, String description) {
        this(name, description, "");
    }

    public TestCommand(String name, String description, String usageMessage) {
        this(name, description, usageMessage, new String[]{});
    }

    public TestCommand(String name, String description, String usageMessage, String[] aliases) {
        super(name, description, usageMessage, aliases);
        //mark as a test command (shown in blue on the client)
        this.commandData.flags.add(NukkitCommandData.Flag.TEST_USAGE);
    }
}
