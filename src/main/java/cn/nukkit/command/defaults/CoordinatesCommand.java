package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.GameRules;
import cn.nukkit.network.protocol.GameRulesChangedPacket;
import cn.nukkit.utils.TextFormat;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CoordinatesCommand extends VanillaCommand {

    private static final Set<UUID> ENABLED_PLAYERS = ConcurrentHashMap.newKeySet();

    public CoordinatesCommand(String name) {
        super(name, "nukkit.command.coordinates.description", "", new String[]{"coords"});
        this.setPermission("nukkit.command.coordinates");
        this.getCommandParameters().clear();
        this.addCommandParameters("default", new CommandParameter[]{
                CommandParameter.newEnum("mode", true, new CommandEnum("CoordinatesMode", "show", "hide", "toggle"))
        });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        if (!sender.isPlayer()) {
            log.addError("nukkit.command.generic.ingame").output();
            return 0;
        }
        Player player = sender.asPlayer();
        ParamList list = result.getValue();
        UUID uuid = player.getUniqueId();

        boolean enable;
        if (list.hasResult(0)) {
            String value = list.getResult(0);
            enable = switch (value.toLowerCase()) {
                case "show" -> true;
                case "hide" -> false;
                default -> !ENABLED_PLAYERS.contains(uuid);
            };
        } else {
            enable = !ENABLED_PLAYERS.contains(uuid);
        }

        if (enable) {
            ENABLED_PLAYERS.add(uuid);
        } else {
            ENABLED_PLAYERS.remove(uuid);
        }
        sendShowCoordinates(player, enable);

        log.addMessage(TextFormat.WHITE + "%" + (enable ? "nukkit.command.coordinates.enabled" : "nukkit.command.coordinates.disabled")).output();
        return 1;
    }

    private static void sendShowCoordinates(Player player, boolean show) {
        GameRules levelRules = player.getLevel().getGameRules();
        GameRules overlay = cloneWithShowCoordinates(levelRules, show);
        GameRulesChangedPacket packet = new GameRulesChangedPacket();
        packet.gameRules = overlay;
        player.dataPacket(packet);
    }

    private static GameRules cloneWithShowCoordinates(GameRules source, boolean show) {
        GameRules clone = GameRules.getDefault();
        source.getGameRules().forEach((rule, value) -> {
            if (rule == GameRule.SHOW_COORDINATES || !clone.hasRule(rule) || rule.isDeprecated()) {
                return;
            }
            switch (value.getType()) {
                case BOOLEAN -> clone.setGameRule(rule, source.getBoolean(rule));
                case INTEGER -> clone.setGameRule(rule, source.getInteger(rule));
                case FLOAT -> clone.setGameRule(rule, source.getFloat(rule));
                default -> {
                }
            }
        });
        clone.setGameRule(GameRule.SHOW_COORDINATES, show);
        return clone;
    }
}
