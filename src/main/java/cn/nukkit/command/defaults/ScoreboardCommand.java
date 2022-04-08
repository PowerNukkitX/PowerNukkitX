package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.network.protocol.RemoveObjectivePacket;
import cn.nukkit.network.protocol.SetObjectivePacket;
import cn.nukkit.network.protocol.SetScorePacket;
import cn.nukkit.network.protocol.SetScoreboardIdentityPacket;

import java.util.Collections;

public class ScoreboardCommand extends VanillaCommand {

    public ScoreboardCommand(String name) {
        super(name,"commands.scoreboard.description", "commands.scoreboard.usage");
        this.setPermission("nukkit.command.scoreboard");
        this.commandParameters.clear();
        //todo: add parameters
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        //todo: implement it
        //debug
        if(sender.isPlayer()){
            Player player = sender.asPlayer();
            SetObjectivePacket packet = new SetObjectivePacket();
            packet.displaySlot = SetObjectivePacket.DISPLAY_SLOT_SIDEBAR;
            packet.objectiveName = "test";
            packet.displayName = "test";
            packet.criteriaName = "dummy";
            player.dataPacket(packet);

            SetScorePacket packet2 = new SetScorePacket();
            SetScorePacket.ScoreInfo info = new SetScorePacket.ScoreInfo(17, "test",0, SetScorePacket.ScoreInfo.ScorerType.PLAYER,player.getId());
            packet2.action = SetScorePacket.Action.SET;
            packet2.infos = Collections.singletonList(info);
            player.dataPacket(packet2);

            if (args[0].equals("remove")) {
                RemoveObjectivePacket packet3 = new RemoveObjectivePacket();
                packet3.objectiveName = "test";
                player.dataPacket(packet3);
            }

            if (args[0].equals("change")){
                SetScoreboardIdentityPacket packet4 = new SetScoreboardIdentityPacket();

            }
        }
        return true;
    }
}
