package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.node.PlayersNode;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.network.protocol.PlayerFogPacket;
import cn.nukkit.utils.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


public class FogCommand extends VanillaCommand {
    public FogCommand(String name) {
        super(name, "commands.fog.description", "commands.fog.usage");
        this.setPermission("nukkit.command.fog");
        this.commandParameters.clear();
        this.commandParameters.put("push", new CommandParameter[]{
                CommandParameter.newType("victim", CommandParamType.TARGET, new PlayersNode()),
                CommandParameter.newEnum("push", new String[]{"push"}),
                CommandParameter.newType("fogId", CommandParamType.STRING),
                CommandParameter.newType("userProvidedId", CommandParamType.STRING)
        });
        this.commandParameters.put("delete", new CommandParameter[]{
                CommandParameter.newType("victim", CommandParamType.TARGET, new PlayersNode()),
                CommandParameter.newEnum("mode", new CommandEnum("delete", "pop", "remove")),
                CommandParameter.newType("userProvidedId", CommandParamType.STRING)
        });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        List<Player> targets = list.getResult(0);
        if (targets.isEmpty()) {
            log.addNoTargetMatch().output();
            return 0;
        }
        switch (result.getKey()) {
            case "push" -> {
                String fogIdStr = list.getResult(2);
                var fogId = Identifier.tryParse(fogIdStr);
                if (fogId == null) {
                    log.addError("commands.fog.invalidFogId", fogIdStr).output();
                    return 0;
                }
                String userProvidedId = list.getResult(3);
                var fog = new PlayerFogPacket.Fog(fogId, userProvidedId);
                targets.forEach(player -> {
                    player.getFogStack().add(fog);
                    player.sendFogStack();//刷新到客户端
                });
                log.addSuccess("commands.fog.success.push", userProvidedId, fogIdStr).output();
                return 1;
            }
            case "delete" -> {
                String mode = list.getResult(1);
                String userProvidedId = list.getResult(2);
                AtomicInteger success = new AtomicInteger(1);
                switch (mode) {
                    case "pop" -> {
                        targets.forEach(player -> {
                            var fogStack = player.getFogStack();
                            for (int i = fogStack.size() - 1; i >= 0; i--) {
                                var fog = fogStack.get(i);
                                if (fog.userProvidedId().equals(userProvidedId)) {
                                    fogStack.remove(i);
                                    player.sendFogStack();//刷新到客户端
                                    log.addSuccess("commands.fog.success.pop", userProvidedId, fog.identifier().toString()).output();
                                    return;
                                }
                            }
                            log.addError("commands.fog.invalidUserId", userProvidedId).output();
                            success.set(0);
                        });
                        return success.get();
                    }
                    case "remove" -> {
                        targets.forEach(player -> {
                            var fogStack = player.getFogStack();
                            List<PlayerFogPacket.Fog> shouldRemoved = new ArrayList<>();
                            for (int i = 0; i < fogStack.size(); i++) {
                                var fog = fogStack.get(i);
                                if (fog.userProvidedId().equals(userProvidedId)) {
                                    shouldRemoved.add(fog);
                                    log.addSuccess("commands.fog.success.remove", userProvidedId, fog.identifier().toString()).output();
                                }
                            }
                            fogStack.removeAll(shouldRemoved);
                            player.sendFogStack();//刷新到客户端
                            if (shouldRemoved.size() == 0) {
                                log.addError("commands.fog.invalidUserId", userProvidedId).output();
                                success.set(0);
                            }
                        });
                        return success.get();
                    }
                    default -> {
                        return 0;
                    }
                }
            }
            default -> {
                return 0;
            }
        }
    }
}
