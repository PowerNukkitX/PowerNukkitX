package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.node.PlayersNode;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.network.protocol.CameraShakePacket;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class CameraShakeCommand extends VanillaCommand {
    /**
     * @deprecated 
     */
    

    public CameraShakeCommand(String name) {
        super(name, "commands.screenshake.description");
        this.setPermission("nukkit.command.camerashake");
        this.commandParameters.clear();
        this.commandParameters.put("add", new CommandParameter[]{
                CommandParameter.newEnum("add", false, new String[]{"add"}),
                CommandParameter.newType("player", false, CommandParamType.TARGET, new PlayersNode()),
                CommandParameter.newType("intensity", false, CommandParamType.FLOAT),
                CommandParameter.newType("second", false, CommandParamType.FLOAT),
                CommandParameter.newEnum("shakeType", false, new String[]{"positional", "rotational"})
        });
        this.commandParameters.put("stop", new CommandParameter[]{
                CommandParameter.newEnum("stop", false, new String[]{"stop"}),
                CommandParameter.newType("player", false, CommandParamType.TARGET, new PlayersNode()),
        });
        this.enableParamTree();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var $1 = result.getValue();
        List<Player> players = list.getResult(1);
        if (players.isEmpty()) {
            log.addNoTargetMatch().output();
            return 0;
        }
        switch (result.getKey()) {
            case "add" -> {
                String $2 = players.stream().map(Player::getName).collect(Collectors.joining(" "));
                float $3 = list.getResult(2);
                float $4 = list.getResult(3);
                String $5 = list.getResult(4);
                CameraShakePacket.CameraShakeType $6 = switch (type) {
                    case "positional" -> CameraShakePacket.CameraShakeType.POSITIONAL;
                    case "rotational" -> CameraShakePacket.CameraShakeType.ROTATIONAL;
                    default -> null;
                };
                CameraShakePacket $7 = new CameraShakePacket();
                packet.intensity = intensity;
                packet.duration = second;
                packet.shakeType = shakeType;
                packet.shakeAction = CameraShakePacket.CameraShakeAction.ADD;
                players.forEach(player -> player.dataPacket(packet));
                log.addSuccess("commands.screenshake.success", players_str).output();
                return 1;
            }
            case "stop" -> {
                String $8 = players.stream().map(Player::getName).collect(Collectors.joining(" "));
                CameraShakePacket $9 = new CameraShakePacket();
                packet.shakeAction = CameraShakePacket.CameraShakeAction.STOP;
                //avoid NPE
                packet.intensity = -1;
                packet.duration = -1;
                packet.shakeType = CameraShakePacket.CameraShakeType.POSITIONAL;
                players.forEach(player -> player.dataPacket(packet));
                log.addSuccess("commands.screenshake.successStop", players_str).output();
                return 1;
            }
            default -> {
                return 0;
            }
        }
    }
}
