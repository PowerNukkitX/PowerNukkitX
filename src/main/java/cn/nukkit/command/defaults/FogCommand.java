package cn.nukkit.command.defaults;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.command.utils.CommandParser;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.network.protocol.PlayerFogPacket;
import cn.nukkit.utils.Identifier;

import java.util.ArrayList;
import java.util.List;

@PowerNukkitXOnly
@Since("1.19.50-r3")
public class FogCommand extends VanillaCommand {
    public FogCommand(String name) {
        super(name, "commands.fog.description", "commands.fog.usage");
        this.setPermission("nukkit.command.fog");
        this.commandParameters.clear();
        this.commandParameters.put("push", new CommandParameter[]{
                CommandParameter.newType("victim", CommandParamType.TARGET),
                CommandParameter.newEnum("push", new String[]{"push"}),
                CommandParameter.newType("fogId", CommandParamType.STRING),
                CommandParameter.newType("userProvidedId", CommandParamType.STRING)
        });
        this.commandParameters.put("delete", new CommandParameter[]{
                CommandParameter.newType("victim", CommandParamType.TARGET),
                CommandParameter.newEnum("delete", new String[]{"pop", "remove"}),
                CommandParameter.newType("userProvidedId", CommandParamType.STRING)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        CommandParser parser = new CommandParser(this, sender, args);
        var form = parser.matchCommandForm();
        if (form == null) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return false;
        }

        try {
            var targets = parser.parseTargetPlayers();
            switch (form) {
                case "push" -> {
                    parser.parseString();//skip "push"
                    var fogIdStr = parser.parseString();
                    var fogId = Identifier.tryParse(fogIdStr);
                    if (fogId == null) {
                        sender.sendMessage(new TranslationContainer("§c%commands.fog.invalidFogId", fogIdStr));
                        return false;
                    }
                    var userProvidedId = parser.parseString();
                    var fog = new PlayerFogPacket.Fog(fogId, userProvidedId);
                    targets.forEach(player -> {
                        player.getFogStack().add(fog);
                        player.sendFogStack();//刷新到客户端
                    });
                    sender.sendMessage(new TranslationContainer("commands.fog.success.push", userProvidedId, fogIdStr));
                    return true;
                }
                case "delete" -> {
                    var mode = parser.parseString();
                    var userProvidedId = parser.parseString();
                    switch (mode) {
                        case "pop" -> {
                            targets.forEach(player -> {
                                var fogStack = player.getFogStack();
                                for (int i = 0; i < fogStack.size(); i++) {
                                    var fog = fogStack.get(i);
                                    if (fog.userProvidedId().equals(userProvidedId)) {
                                        fogStack.remove(i);
                                        player.sendFogStack();//刷新到客户端
                                        sender.sendMessage(new TranslationContainer("commands.fog.success.pop", userProvidedId, fog.identifier().toString()));
                                        return;
                                    }
                                }
                                sender.sendMessage(new TranslationContainer("commands.fog.invalidUserId", userProvidedId));
                            });
                            return true;
                        }
                        case "remove" -> {
                            targets.forEach(player -> {
                                var fogStack = player.getFogStack();
                                List<PlayerFogPacket.Fog> shouldRemoved = new ArrayList<>();
                                for (int i = 0; i < fogStack.size(); i++) {
                                    var fog = fogStack.get(i);
                                    if (fog.userProvidedId().equals(userProvidedId)) {
                                        shouldRemoved.add(fog);
                                        sender.sendMessage(new TranslationContainer("commands.fog.success.remove", userProvidedId, fog.identifier().toString()));
                                    }
                                }
                                fogStack.removeAll(shouldRemoved);
                                player.sendFogStack();//刷新到客户端
                                if (shouldRemoved.size() == 0) sender.sendMessage(new TranslationContainer("commands.fog.invalidUserId", userProvidedId));
                            });
                            return true;
                        }
                    }
                }
            }
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }

        return false;
    }
}
