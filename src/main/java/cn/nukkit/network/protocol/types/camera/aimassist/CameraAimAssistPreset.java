package cn.nukkit.network.protocol.types.camera.aimassist;

import cn.nukkit.utils.OptionalValue;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CameraAimAssistPreset {
    public String identifier;
    public final List<String> blockExclusionList = new ObjectArrayList<>();
    public final List<String> entityExclusionList = new ObjectArrayList<>();
    public final List<String> blockTagExclusionList = new ObjectArrayList<>();
    public final List<String> liquidTargetingList = new ObjectArrayList<>();
    public final Map<String, String> itemSettings = new Object2ObjectArrayMap<>();
    public OptionalValue<String> defaultItemSettings;
    public OptionalValue<String> handSettings;
}
