package cn.nukkit.network.protocol;

import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;


@Since("1.4.0.0-PN")
@ToString
public class ItemStackRequestPacket extends DataPacket {
    @Since("1.4.0.0-PN")
    public final List<Request> requests = new ArrayList<>();

    @Override
    public byte pid() {
        return ProtocolInfo.ITEM_STACK_REQUEST_PACKET;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {

    }

    @Since("1.4.0.0-PN")
    public record Request(int requestId, List<ItemStackAction> actions) {

    }

    @Since("1.4.0.0-PN")
    public record ItemStackAction(byte type, boolean bool0, byte byte0, int varInt0, int varInt1, byte baseByte0,
                                  byte baseByte1, byte baseByte2, int baseVarInt0, byte flagsByte0, byte flagsByte1,
                                  int flagsVarInt0, List<Item> items) {
        @Override
        public String toString() {
            StringJoiner joiner = new StringJoiner(", ");
            joiner.add("type=" + type);

            switch (type) {
                case 0, 1, 2 -> joiner.add("baseByte0=" + baseByte0)
                        .add("baseByte1=" + baseByte1)
                        .add("baseByte2=" + baseByte2)
                        .add("baseVarInt0=" + baseVarInt0)
                        .add("flagsByte0=" + flagsByte0)
                        .add("flagsByte1=" + flagsByte1)
                        .add("flagsVarInt0=" + flagsVarInt0);
                case 3 -> joiner.add("bool0=" + bool0)
                        .add("baseByte0=" + baseByte0)
                        .add("baseByte1=" + baseByte1)
                        .add("baseByte2=" + baseByte2)
                        .add("baseVarInt0=" + baseVarInt0);
                case 4, 5 -> joiner.add("baseByte0=" + baseByte0)
                        .add("baseByte1=" + baseByte1)
                        .add("baseByte2=" + baseByte2)
                        .add("baseVarInt0=" + baseVarInt0);
                case 6 -> joiner.add("byte0=" + byte0);
                case 8 -> joiner.add("varInt0=" + varInt0)
                        .add("varInt1=" + varInt1);
                case 10, 11, 12, 13, 14, 15 -> joiner.add("varInt0=" + varInt0);
                case 17 -> joiner.add("items=" + items);
            }
            return "ItemStackAction(" + joiner + ")";
        }
    }
}
