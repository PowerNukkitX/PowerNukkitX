package cn.nukkit.network.protocol.types;

import lombok.Value;

@Value
public class LegacySetItemSlotData {
    private final int containerId;
    private final byte[] slots;
}
