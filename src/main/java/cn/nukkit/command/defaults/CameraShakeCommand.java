package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.command.utils.CommandParser;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.network.protocol.CameraShakePacket;

import java.util.List;
import java.util.stream.Collectors;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class CameraShakeCommand extends VanillaCommand {

    public CameraShakeCommand(String name) {
        super(name, "commands.screenshake.description");
        this.setPermission("nukkit.command.camerashake");
        this.commandParameters.clear();
        this.commandParameters.put("add", new CommandParameter[]{
                CommandParameter.newEnum("add",false,new String[]{"add"}),
                CommandParameter.newType("player", false, CommandParamType.TARGET),
                CommandParameter.newType("intensity",false,CommandParamType.FLOAT),
                CommandParameter.newType("second",false,CommandParamType.FLOAT),
                CommandParameter.newEnum("shakeType",false,new String[]{"positional","rotational"})
        });
        this.commandParameters.put("stop", new CommandParameter[]{
                CommandParameter.newEnum("stop",false,new String[]{"stop"}),
                CommandParameter.newType("player", false, CommandParamType.TARGET),
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        CommandParser parser = new CommandParser(this,sender,args);

        String form = parser.matchCommandForm();
        if(form == null){
            sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return false;
        }


        try {
            parser.parseString();
            List<Player> players = parser.parseTargetPlayers();
            String players_str = players.stream().map(p -> p.getName()).collect(Collectors.joining(" "));
            switch (form) {
                case "add" -> {
                    float intensity = (float) parser.parseDouble();
                    float second = (float) parser.parseDouble();
                    CameraShakePacket.CameraShakeType shakeType = switch(parser.parseString()){
                        case "positional" -> CameraShakePacket.CameraShakeType.POSITIONAL;
                        case "rotational" -> CameraShakePacket.CameraShakeType.ROTATIONAL;
                        default -> throw new CommandSyntaxException();
                    };
                    CameraShakePacket packet = new CameraShakePacket();
                    packet.intensity = intensity;
                    packet.duration = second;
                    packet.shakeType = shakeType;
                    packet.shakeAction = CameraShakePacket.CameraShakeAction.ADD;
                    players.forEach(player -> player.dataPacket(packet));
                    sender.sendMessage(new TranslationContainer("commands.screenshake.success", players_str));
                    return true;
                }
                case "stop" -> {
                    CameraShakePacket packet = new CameraShakePacket();
                    packet.shakeAction = CameraShakePacket.CameraShakeAction.STOP;
                    //avoid NPE
                    packet.intensity = -1;
                    packet.duration = -1;
                    packet.shakeType = CameraShakePacket.CameraShakeType.POSITIONAL;
                    players.forEach(player -> player.dataPacket(packet));
                    sender.sendMessage(new TranslationContainer("commands.screenshake.successStop", players_str));
                    return true;
                }
                default -> {
                    return false;
                }
            }
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
        return false;
    }
}
