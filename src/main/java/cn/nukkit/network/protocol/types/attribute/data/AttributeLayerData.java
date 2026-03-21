package cn.nukkit.network.protocol.types.attribute.data;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Data;

import java.util.List;

@Data
public class AttributeLayerData {

    private String name;
    private int dimensionId;
    private AttributeLayerSettings settings;
    private final List<EnvironmentAttributeData> attributes = new ObjectArrayList<>();
}