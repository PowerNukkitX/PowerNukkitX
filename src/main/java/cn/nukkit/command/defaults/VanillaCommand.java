package cn.nukkit.command.defaults;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

import java.util.StringJoiner;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class VanillaCommand extends Command {

    public VanillaCommand(String name) {
        super(name);
    }

    public VanillaCommand(String name, String description) {
        super(name, description);
    }

    public VanillaCommand(String name, String description, String usageMessage) {
        super(name, description, usageMessage);
    }

    public VanillaCommand(String name, String description, String usageMessage, String[] aliases) {
        super(name, description, usageMessage, aliases);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        return this.execute(sender, commandLabel, args, true);//todo sendCommandFeedback改为世界规则
    }

    public boolean execute(CommandSender sender, String commandLabel, String[] args, Boolean sendCommandFeedback) {
        return false;
    }


    @PowerNukkitXOnly
    @Since("1.19.31-r2")
    public String[] getGenericSyntaxErrors(String[] args, int errorIndex) {
        var join1 = new StringJoiner(" ", "/", " ");
        join1.add(this.getName());

        if (errorIndex == -1) {
            return new String[]{join1.toString(), " ", " "};
        }

        for (int i = 0, len = errorIndex; i < len; ++i) {
            join1.add(args[i]);
        }
        var join2 = new StringJoiner(" ", " ", "");
        for (int i = errorIndex + 1, len = args.length; i < len; ++i) {
            join2.add(args[i]);
        }
        return new String[]{join1.toString(), args[errorIndex], join2.toString()};
    }
}
