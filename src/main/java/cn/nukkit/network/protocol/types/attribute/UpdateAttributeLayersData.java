package cn.nukkit.network.protocol.types.attribute;

import cn.nukkit.network.protocol.types.attribute.data.AttributeLayerData;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Data;

import java.util.List;

/**
 * @since v944
 */
@Data
public class UpdateAttributeLayersData implements AttributeLayerSyncData {

    private final List<AttributeLayerData> attributeLayers = new ObjectArrayList<>();

    @Override
    public Type getType() {
        return Type.UPDATE_ATTRIBUTE_LAYERS;
    }
}