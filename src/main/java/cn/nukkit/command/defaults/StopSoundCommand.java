package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.command.utils.CommandParser;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.network.protocol.StopSoundPacket;
import cn.nukkit.utils.TextFormat;

import java.util.List;
import java.util.stream.Collectors;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class StopSoundCommand extends VanillaCommand {

    public StopSoundCommand(String name) {
        super(name, "commands.stopsound.description");
        this.setPermission("nukkit.command.stopsound");
        this.getCommandParameters().clear();
        this.addCommandParameters("default", new CommandParameter[]{
                CommandParameter.newType("player", false, CommandParamType.TARGET),
                CommandParameter.newType("sound", true, CommandParamType.STRING)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        CommandParser parser = new CommandParser(this, sender, args);
        try {
            List<Player> targets = parser.parseTargetPlayers();
            String sound = "";

            if (args.length > 1) {
                sound = parser.parseString();
            }

            if (targets.size() == 0) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.noTargetMatch"));
                return false;
            }

            StopSoundPacket packet = new StopSoundPacket();
            packet.name = sound;
            if (sound.isEmpty()) {
                packet.stopAll = true;
            }

            Server.broadcastPacket(targets, packet);

            String players_str = targets.stream().map(Player::getName).collect(Collectors.joining(" "));

            if (packet.stopAll) {
                sender.sendMessage(new TranslationContainer("commands.stopsound.success.all", players_str));
            } else {
                sender.sendMessage(new TranslationContainer("commands.stopsound.success", sound, players_str));
            }
        } catch (CommandSyntaxException e) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return false;
        }

        return true;
    }
}
