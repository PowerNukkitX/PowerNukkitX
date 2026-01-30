package cn.nukkit.network.protocol.types;

import lombok.Value;

@Value
public class LegacySetItemSlotData {
    int containerId;
    byte[] slots;
}
