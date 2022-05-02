package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.network.protocol.StopSoundPacket;
import cn.nukkit.command.CommandParser;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.utils.TextFormat;

import java.util.List;
import java.util.stream.Collectors;

public class StopSoundCommand extends VanillaCommand {

    public StopSoundCommand(String name) {
        super(name, "commands.stopsound.description", "commands.stopsound.usage");
        this.setPermission("nukkit.command.stopsound");
        this.getCommandParameters().clear();
        this.addCommandParameters("default", new CommandParameter[]{
                CommandParameter.newType("player",false, CommandParamType.TARGET),
                CommandParameter.newType("sound",true, CommandParamType.STRING)
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
                sender.sendMessage(TextFormat.RED + "No targets matched selector");
                return false;
            }

            StopSoundPacket packet = new StopSoundPacket();
            packet.name = sound;
            if (sound.isEmpty()) {
                packet.stopAll = true;
            }

            Server.broadcastPacket(targets, packet);

            sender.sendMessage(String.format(packet.stopAll ? "Stopped all sounds for %2$s" : "Stopped sound '%1$s' for %2$s", sound, targets.stream().map(Player::getName).collect(Collectors.joining(", "))));
        } catch (CommandSyntaxException e) {
             sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return false;
        }

        return true;
    }
}
