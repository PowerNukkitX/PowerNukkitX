package cn.nukkit.network.serializer;

import io.netty.buffer.ByteBuf;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodecHelper;
import org.cloudburstmc.protocol.bedrock.codec.v898.serializer.ClientboundDataStoreSerializer_v898;
import org.cloudburstmc.protocol.bedrock.data.ddui.DataStoreChange;
import org.cloudburstmc.protocol.bedrock.data.ddui.DataStorePropertyValue;
import org.cloudburstmc.protocol.common.util.VarInts;

import java.util.Map;

/**
 * Fixes the ordinal() vs getId() bug in {@link ClientboundDataStoreSerializer_v898}.
 * The base class writes {@code DataStorePropertyValueType.ordinal()} (sequential 0,1,2,3,4)
 * but the Bedrock protocol uses the actual id values (0,1,2,4,6).
 * STRING has ordinal=3 but id=4; TYPE has ordinal=4 but id=6.
 */
public class ClientboundDataStoreSerializerFixed extends ClientboundDataStoreSerializer_v898 {

    public static final ClientboundDataStoreSerializerFixed INSTANCE = new ClientboundDataStoreSerializerFixed();

    @Override
    protected void writeDataStoreChange(ByteBuf buffer, BedrockCodecHelper helper, DataStoreChange change) {
        helper.writeString(buffer, change.getDataStoreName());
        helper.writeString(buffer, change.getProperty());
        buffer.writeIntLE(change.getUpdateCount());
        buffer.writeIntLE(change.getTheNewPropertyValue().getType().getId());
        writeTheNewPropertyValue(buffer, helper, change.getTheNewPropertyValue());
    }

    @Override
    protected void writeTheNewPropertyValue(ByteBuf buffer, BedrockCodecHelper helper, DataStorePropertyValue value) {
        switch (value.getType()) {
            case NONE:
                break;
            case BOOL:
                buffer.writeBoolean((boolean) value.getValue());
                break;
            case INT64:
                buffer.writeLongLE((long) value.getValue());
                break;
            case STRING:
                helper.writeString(buffer, (String) value.getValue());
                break;
            case TYPE:
                @SuppressWarnings("unchecked")
                final Map<String, DataStorePropertyValue> map = (Map<String, DataStorePropertyValue>) value.getValue();
                VarInts.writeUnsignedInt(buffer, map.size());
                for (Map.Entry<String, DataStorePropertyValue> entry : map.entrySet()) {
                    helper.writeString(buffer, entry.getKey());
                    buffer.writeIntLE(entry.getValue().getType().getId());
                    writeTheNewPropertyValue(buffer, helper, entry.getValue());
                }
                break;
        }
    }
}
