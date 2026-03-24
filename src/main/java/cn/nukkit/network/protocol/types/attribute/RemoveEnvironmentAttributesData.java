package cn.nukkit.network.protocol.types.attribute;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Data;

import java.util.List;

/**
 * @since v944
 */
@Data
public class RemoveEnvironmentAttributesData implements AttributeLayerSyncData{

    private String attributeLayerName;
    private int attributeLayerDimensionId;
    private final List<String> attributes = new ObjectArrayList<>();

    @Override
    public Type getType() {
        return Type.REMOVE_ENVIRONMENT_ATTRIBUTES;
    }
}