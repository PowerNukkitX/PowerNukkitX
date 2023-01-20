package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.ParamTree;
import cn.nukkit.command.tree.node.PlayersNode;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.network.protocol.CameraShakePacket;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class CameraShakeCommand extends VanillaCommand {

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
        this.paramTree = new ParamTree(this);
    }


    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        switch (result.getKey()) {
            case "add" -> {
                List<Player> players = list.getResult(1);
                String players_str = players.stream().map(Player::getName).collect(Collectors.joining(" "));
                float intensity = list.getResult(2);
                float second = list.getResult(3);
                String type = list.getResult(4);
                CameraShakePacket.CameraShakeType shakeType = switch (type) {
                    case "positional" -> CameraShakePacket.CameraShakeType.POSITIONAL;
                    case "rotational" -> CameraShakePacket.CameraShakeType.ROTATIONAL;
                    default -> null;
                };
                CameraShakePacket packet = new CameraShakePacket();
                packet.intensity = intensity;
                packet.duration = second;
                packet.shakeType = shakeType;
                packet.shakeAction = CameraShakePacket.CameraShakeAction.ADD;
                players.forEach(player -> player.dataPacket(packet));
                log.addSuccess("commands.screenshake.success", players_str).output();
                return 1;
            }
            case "stop" -> {
                List<Player> players = list.getResult(1);
                String players_str = players.stream().map(Player::getName).collect(Collectors.joining(" "));
                CameraShakePacket packet = new CameraShakePacket();
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
