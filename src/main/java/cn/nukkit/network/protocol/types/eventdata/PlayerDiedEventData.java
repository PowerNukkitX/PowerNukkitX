package cn.nukkit.network.protocol.types.eventdata;

import cn.nukkit.network.protocol.types.EventData;
import cn.nukkit.network.protocol.types.EventDataType;
import cn.nukkit.utils.BinaryStream;
import lombok.Value;

/**
 * Used to control that pop up on the player's respawn screen
 */
@Value
public class PlayerDiedEventData implements EventData {
    private final int attackerEntityId;
    private final int attackerVariant;
    private final int entityDamageCause;
    private final boolean inRaid;

    @Override
    public EventDataType getType() {
        return EventDataType.PLAYER_DIED;
    }

    @Override
    public void write(BinaryStream stream) {
        stream.putVarInt(attackerEntityId);
        stream.putVarInt(attackerVariant);
        stream.putVarInt(entityDamageCause);
        stream.putBoolean(inRaid);
    }
}