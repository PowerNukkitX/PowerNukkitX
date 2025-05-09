package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.node.PlayersNode;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.network.protocol.StopSoundPacket;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


public class StopSoundCommand extends VanillaCommand {

    public StopSoundCommand(String name) {
        super(name, "commands.stopsound.description");
        this.setPermission("nukkit.command.stopsound");
        this.getCommandParameters().clear();
        this.addCommandParameters("default", new CommandParameter[]{
                CommandParameter.newType("player", false, CommandParamType.TARGET, new PlayersNode()),
                CommandParameter.newType("sound", true, CommandParamType.STRING)
        });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        List<Player> targets = list.getResult(0);
        targets = targets.stream().filter(Objects::nonNull).toList();
        if (targets.isEmpty()) {
            log.addNoTargetMatch().output();
            return 0;
        }
        String sound = "";

        if (list.hasResult(1)) {
            sound = list.getResult(1);
        }
        StopSoundPacket packet = new StopSoundPacket();
        packet.name = sound;
        if (sound.isEmpty()) {
            packet.stopAll = true;
        }
        Server.broadcastPacket(targets, packet);
        String players_str = targets.stream().map(Player::getName).collect(Collectors.joining(" "));
        if (packet.stopAll) {
            log.addSuccess("commands.stopsound.success.all", players_str).output();
        } else {
            log.addSuccess("commands.stopsound.success", sound, players_str).output();
        }
        return 1;
    }
}
