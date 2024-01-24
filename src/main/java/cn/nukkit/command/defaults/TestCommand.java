package cn.nukkit.command.defaults;

import cn.nukkit.command.Command;
import cn.nukkit.command.data.CommandData;

/**
 * 测试命令基类
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
        //标记为测试命令（客户端显示为蓝色）
        this.commandData.flags.add(CommandData.Flag.TEST_USAGE);
    }
}
