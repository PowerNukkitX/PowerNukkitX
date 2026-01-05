package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.node.PlayersNode;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.network.protocol.types.ClientInputLocksFlag;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
    @author xRookieFight
    @since 2026/5/1
*/
public class InputPermissionCommand extends VanillaCommand {

    public InputPermissionCommand(String name) {
        super(name, "commands.inputpermission.description");
        this.setPermission("nukkit.command.inputpermission");

        this.commandParameters.clear();

        String[] permissions = {
                "camera",
                "movement",
                "jump",
                "sneak",
                "mount",
                "dismount",
                "lateral_movement",
                "move_forward",
                "move_backward",
                "move_left",
                "move_right"
        };

        this.commandParameters.put("query", new CommandParameter[]{
                CommandParameter.newType("targets", false, CommandParamType.TARGET, new PlayersNode()),
                CommandParameter.newEnum("query", false, new String[]{"query"}),
                CommandParameter.newEnum("permission", false, permissions)
        });

        this.commandParameters.put("set", new CommandParameter[]{
                CommandParameter.newType("targets", false, CommandParamType.TARGET, new PlayersNode()),
                CommandParameter.newEnum("set", false, new String[]{"set"}),
                CommandParameter.newEnum("permission", false, permissions),
                CommandParameter.newEnum("state", false, new String[]{"enabled", "disabled"})
        });

        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        ParamList list = result.getValue();
        List<Player> players = list.getResult(0);
        players = players.stream().filter(Objects::nonNull).toList();

        if (players.isEmpty()) {
            log.addNoTargetMatch().output();
            return 0;
        }

        String permission = list.get(2).get();

        ClientInputLocksFlag flag = switch (permission) {
            case "camera" -> ClientInputLocksFlag.CAMERA;
            case "movement" -> ClientInputLocksFlag.MOVEMENT;
            case "jump" -> ClientInputLocksFlag.JUMP;
            case "sneak" -> ClientInputLocksFlag.SNEAK;
            case "mount" -> ClientInputLocksFlag.MOUNT;
            case "dismount" -> ClientInputLocksFlag.DISMOUNT;
            case "lateral_movement" -> ClientInputLocksFlag.LATERAL_MOVEMENT;
            case "move_forward" -> ClientInputLocksFlag.MOVE_FORWARD;
            case "move_backward" -> ClientInputLocksFlag.MOVE_BACKWARD;
            case "move_left" -> ClientInputLocksFlag.MOVE_LEFT;
            case "move_right" -> ClientInputLocksFlag.MOVE_RIGHT;
            default -> null;
        };

        if (flag == null) {
            log.addError("commands.inputpermission.set.invalidfilter", permission).output();
            return 0;
        }

        switch (result.getKey()) {
            case "query" -> {
                log.addSuccess(
                        "commands.inputpermission.query",
                        upper(permission),
                        String.valueOf(players.size()),
                        permission
                ).output();
                return 1;
            }

            case "set" -> {
                String state = list.get(3).get();
                boolean enable;

                if ("enabled".equalsIgnoreCase(state)) {
                    enable = true;
                } else if ("disabled".equalsIgnoreCase(state)) {
                    enable = false;
                } else {
                    log.addError("commands.inputpermission.set.missingstate").output();
                    return 0;
                }

                for (Player player : players) {
                    if (enable) {
                        player.removeClientInputLock(flag);
                    } else {
                        player.addClientInputLock(flag);
                    }
                }

                String stateLang = enable ? "enabled" : "disabled";

                if (players.size() == 1) {
                    log.addSuccess(
                            "commands.inputpermission.set.outputoneplayer",
                            upper(permission),
                            stateLang,
                            players.getFirst().getName()
                    ).output();
                } else {
                    log.addSuccess(
                            "commands.inputpermission.set.outputmultipleplayers",
                            upper(permission),
                            stateLang,
                            String.valueOf(players.size())
                    ).output();
                }
                return 1;
            }
        }

        return 0;
    }

    private String upper(String value) {
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }
}
