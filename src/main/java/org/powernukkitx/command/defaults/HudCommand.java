package org.powernukkitx.command.defaults;

import org.powernukkitx.Player;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.data.CommandParameter;
import org.powernukkitx.command.tree.ParamList;
import org.powernukkitx.command.tree.node.PlayersNode;
import org.powernukkitx.command.utils.CommandLogger;
import org.cloudburstmc.protocol.bedrock.data.HudElement;
import org.cloudburstmc.protocol.bedrock.data.HudVisibility;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;
import org.cloudburstmc.protocol.bedrock.packet.SetHudPacket;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HudCommand extends VanillaCommand {

    public HudCommand(String name) {
        super(name, "commands.hud.description", "%commands.hud.usage");
        this.setPermission("nukkit.command.hud");
        this.getCommandParameters().clear();
        this.addCommandParameters("default", new CommandParameter[]{
                CommandParameter.newType("player", false, CommandParamType.SELECTION, new PlayersNode()),
                CommandParameter.newEnum("visible", false, new String[]{"hide", "reset"}),
                CommandParameter.newEnum("hud_element", false, new String[]{"armor", "air_bubbles_bar", "crosshair", "food_bar", "health", "hotbar", "paper_doll", "tool_tips", "progress_bar", "touch_controls", "vehicle_health"})
        });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();

        List<Player> players = list.getResult(0);
        players = players.stream().filter(Objects::nonNull).toList();
        if (players.isEmpty()) {
            log.addNoTargetMatch().output();
            return 0;
        }

        HudVisibility visibility = switch ((String) list.getResult(1)) {
            case "hide" -> HudVisibility.HIDE;
            case "reset" -> HudVisibility.RESET;
            default -> null;
        };

        HudElement element = switch ((String) list.getResult(2)) {
            case "armor" -> HudElement.ARMOR;
            case "air_bubbles_bar" -> HudElement.AIR_BUBBLES;
            case "crosshair" -> HudElement.CROSSHAIR;
            case "food_bar" -> HudElement.HUNGER;
            case "health" -> HudElement.HEALTH;
            case "hotbar" -> HudElement.HOT_BAR;
            case "paper_doll" -> HudElement.PAPER_DOLL;
            case "tool_tips" -> HudElement.TOOL_TIPS;
            case "progress_bar" -> HudElement.PROGRESS_BAR;
            case "touch_controls" -> HudElement.TOUCH_CONTROLS;
            case "vehicle_health" -> HudElement.HORSE_HEALTH;

            default -> null;
        };

        if (visibility == null || element == null) {
            return 0;
        }


        for (Player player : players) {
            SetHudPacket packet = new SetHudPacket();
            packet.getHudElementList().add(element);
            packet.setHudVisible(visibility);
            player.sendPacket(packet);

            return 1;
        }

        return 0;
    }
}
