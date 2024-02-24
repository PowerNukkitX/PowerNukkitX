package cn.nukkit.network.protocol;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.PlayerAbility;
import cn.nukkit.network.protocol.types.PlayerPermission;

import java.util.HashSet;
import java.util.Set;


public class RequestPermissionsPacket extends DataPacket {
    //权限列表中可控制的能力
    public static final PlayerAbility[] CONTROLLABLE_ABILITIES = new PlayerAbility[]{
            PlayerAbility.BUILD,
            PlayerAbility.MINE,
            PlayerAbility.DOORS_AND_SWITCHES,
            PlayerAbility.OPEN_CONTAINERS,
            PlayerAbility.ATTACK_PLAYERS,
            PlayerAbility.ATTACK_MOBS,
            PlayerAbility.OPERATOR_COMMANDS,
            PlayerAbility.TELEPORT
    };
    public long uniqueEntityId;
    public PlayerPermission permissions;
    //序列化后的能力列表
    //为一个8位的二进制数，每个位对应一种能力
    public int customPermissions;

    @Override
    public int pid() {
        return ProtocolInfo.REQUEST_PERMISSIONS_PACKET;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.uniqueEntityId = byteBuf.readLongLE();
        this.permissions = PlayerPermission.values()[byteBuf.readByte() / 2];
        this.customPermissions = byteBuf.readShortLE();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        throw new UnsupportedOperationException();
    }

    public Set<PlayerAbility> parseCustomPermissions() {
        var abilities = new HashSet<PlayerAbility>();
        for (PlayerAbility controllableAbility : CONTROLLABLE_ABILITIES) {
            if ((this.customPermissions & controllableAbility.bit) != 0)
                abilities.add(controllableAbility);
        }
        return abilities;
    }

    public Player getTargetPlayer() {
        for (Player player : Server.getInstance().getOnlinePlayers().values()) {
            if (player.getId() == this.uniqueEntityId)
                return player;
        }
        return null;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
