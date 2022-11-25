package cn.nukkit.command.defaults;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.data.CommandData;

/**
 * 测试命令基类
 */
@PowerNukkitXOnly
@Since("1.19.40-r4")
public abstract class TestCommand extends VanillaCommand {

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
        //标记为测试命令（客户端显示为蓝色）
        this.commandData.flags.add(CommandData.Flag.USAGE);
    }
}
