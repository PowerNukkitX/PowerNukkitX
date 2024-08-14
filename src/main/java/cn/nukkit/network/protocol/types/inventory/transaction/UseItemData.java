package cn.nukkit.network.protocol.types.inventory.transaction;

import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import lombok.ToString;

/**
 * @author CreeperFace
 */
@ToString
public class UseItemData implements TransactionData {

    public int actionType;
    public BlockVector3 blockPos;
    public BlockFace face;
    public int hotbarSlot;
    public Item itemInHand;
    public Vector3 playerPos;
    public Vector3f clickPos;
    public int blockRuntimeId;
    public PredictedResult clientInteractPrediction;
    public TriggerType triggerType;

    public enum PredictedResult {
        FAILURE,
        SUCCESS
    }

    public enum TriggerType {
        UNKNOWN,
        PLAYER_INPUT,
        SIMULATION_TICK
    }
}
