package org.powernukkitx.command.defaults;

import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.data.CommandParameter;
import org.powernukkitx.command.tree.ParamList;
import org.powernukkitx.command.utils.CommandLogger;
import org.powernukkitx.lang.TranslationContainer;
import org.powernukkitx.utils.TextFormat;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;

import java.util.Map;

/**
 * @author xtypr
 * @since 2015/11/12
 */
public class MeCommand extends VanillaCommand {

    public MeCommand(String name) {
        super(name, "commands.me.description", "nukkit.command.me.usage");
        this.setPermission("nukkit.command.me");
        this.commandParameters.clear();
        this.commandParameters.put("message", new CommandParameter[]{
                CommandParameter.newType("message", CommandParamType.MESSAGE)
        });
        this.commandParameters.put("default", CommandParameter.EMPTY_ARRAY);
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        String name = sender.getViewableName(null);
        String message = "";
        if (result.getKey().equals("message")) {
            message = result.getValue().getResult(0);
        }

        broadcastCommandMessage(sender, new TranslationContainer("chat.type.emote", name, TextFormat.WHITE + message), true);
        return 1;
    }
}
