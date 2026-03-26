package cn.nukkit.network.protocol.types.attribute;

import cn.nukkit.network.protocol.types.attribute.data.EnvironmentAttributeData;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Data;

import java.util.List;

/**
 * @since v944
 */
@Data
public class UpdateEnvironmentAttributesData implements AttributeLayerSyncData {

    private String attributeLayerName;
    private int attributeLayerDimensionId;
    private final List<EnvironmentAttributeData> attributes = new ObjectArrayList<>();

    @Override
    public Type getType() {
        return Type.UPDATE_ENVIRONMENT_ATTRIBUTES;
    }
}