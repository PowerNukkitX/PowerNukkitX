package cn.nukkit.network.protocol.types.attribute;

import cn.nukkit.network.protocol.types.attribute.data.AttributeLayerSettings;
import lombok.Data;

/**
 * @since v944
 */
@Data
public class UpdateAttributeLayerSettingsData implements AttributeLayerSyncData{

    private String attributeLayerName;
    private int attributeLayerDimensionId;
    private AttributeLayerSettings attributesLayerSettings;

    @Override
    public Type getType() {
        return Type.UPDATE_ATTRIBUTE_LAYER_SETTINGS;
    }
}