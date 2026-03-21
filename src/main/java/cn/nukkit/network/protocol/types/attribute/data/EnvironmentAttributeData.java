package cn.nukkit.network.protocol.types.attribute.data;

import cn.nukkit.camera.data.EaseType;
import lombok.Data;

@Data
public class EnvironmentAttributeData {

    private String attributeName;
    private AttributeData fromAttribute;
    private AttributeData attribute;
    private AttributeData toAttribute;
    private int currentTransitionTicks;
    private int totalTransitionTicks;
    private EaseType easeType;
}