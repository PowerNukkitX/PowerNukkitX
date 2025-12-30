package cn.nukkit.network.protocol.types.inventory;

import lombok.Value;

/**
 * @author Kaooot
 */
@Value
public class ArmorSlotAndDamagePair {
    ArmorSlot slot;
    short damage;
}