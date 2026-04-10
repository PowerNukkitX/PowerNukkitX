package cn.nukkit.network.process.handler;

import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
import org.cloudburstmc.protocol.bedrock.packet.SetPlayerGameTypePacket;

/**
 * @author Kaooot
 */
public class SetPlayerGameTypeHandler implements PacketHandler<SetPlayerGameTypePacket> {

    @Override
    public void handle(SetPlayerGameTypePacket packet, PlayerSessionHolder holder, Server server) {
        PlayerHandle playerHandle = holder.getPlayerHandle();
        if (packet.getPlayerGameType().ordinal() != playerHandle.player.gamemode && playerHandle.player.hasPermission("nukkit.command.gamemode")) {
            playerHandle.player.setGamemode(switch (packet.getPlayerGameType()) {
                case SURVIVAL, CREATIVE, ADVENTURE -> packet.getPlayerGameType().ordinal();
                case SPECTATOR -> 3;
                case DEFAULT -> playerHandle.player.getServer().getDefaultGamemode();
                default -> throw new IllegalStateException("Unexpected value: " + packet.getPlayerGameType());
            });
            Command.broadcastCommandMessage(playerHandle.player, new TranslationContainer("commands.gamemode.success.self", Server.getGamemodeString(playerHandle.player.gamemode)));
        }
    }
}