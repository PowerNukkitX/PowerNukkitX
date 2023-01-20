package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.ParamTree;
import cn.nukkit.command.tree.node.PlayersNode;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.network.protocol.PlaySoundPacket;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class PlaySoundCommand extends VanillaCommand {

    public PlaySoundCommand(String name) {
        super(name, "commands.playsound.description");
        this.setPermission("nukkit.command.playsound");
        this.getCommandParameters().clear();
        this.addCommandParameters("default", new CommandParameter[]{
                CommandParameter.newEnum("sound", false, new CommandEnum("sound", Arrays.stream(Sound.values()).map(Sound::getSound).toList(), true)),
                CommandParameter.newType("player", true, CommandParamType.TARGET, new PlayersNode()),
                CommandParameter.newType("position", true, CommandParamType.POSITION),
                CommandParameter.newType("volume", true, CommandParamType.FLOAT),
                CommandParameter.newType("pitch", true, CommandParamType.FLOAT),
                CommandParameter.newType("minimumVolume", true, CommandParamType.FLOAT)
        });
        this.paramTree = new ParamTree(this);
    }

    @Since("1.19.50-r4")
    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        String sound = list.getResult(0);
        List<Player> targets = null;
        Position position = null;
        float volume = 1;
        float pitch = 1;
        float minimumVolume = 0;
        if (list.hasResult(1)) targets = list.getResult(1);
        if (list.hasResult(2)) position = list.getResult(2);
        if (list.hasResult(3)) volume = list.getResult(3);
        if (list.hasResult(4)) pitch = list.getResult(4);
        if (list.hasResult(5)) minimumVolume = list.getResult(5);
        if (minimumVolume < 0) {
            log.addNumTooSmall(5, 0).output();
            return 0;
        }
        if (targets == null) {
            if (sender.isPlayer()) {
                targets = Lists.newArrayList(sender.asPlayer());
            } else {
                log.addError("commands.generic.noTargetMatch").output();
                return 0;
            }
        }
        double maxDistance = volume > 1 ? volume * 16 : 16;
        List<String> successes = Lists.newArrayList();
        for (Player player : targets) {
            String name = player.getName();
            PlaySoundPacket packet = new PlaySoundPacket();
            if (position.distance(player) > maxDistance) {
                if (minimumVolume <= 0) {
                    log.addError("commands.playsound.playerTooFar", name);
                    continue;
                }

                packet.volume = minimumVolume;
                packet.x = player.getFloorX();
                packet.y = player.getFloorY();
                packet.z = player.getFloorZ();
            } else {
                packet.volume = volume;
                packet.x = position.getFloorX();
                packet.y = position.getFloorY();
                packet.z = position.getFloorZ();
            }

            packet.name = sound;
            packet.pitch = pitch;
            player.dataPacket(packet);

            successes.add(name);
        }
        log.addSuccess("commands.playsound.success", sound, String.join(", ", successes)).successCount(successes.size()).output();
        return successes.size();
    }
}
