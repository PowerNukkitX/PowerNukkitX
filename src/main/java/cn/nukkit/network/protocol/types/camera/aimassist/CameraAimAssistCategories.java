package cn.nukkit.network.protocol.types.camera.aimassist;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Data;
import lombok.Value;

import java.util.List;

@Data
public class CameraAimAssistCategories {
    public String identifier;
    public List<CameraAimAssistCategory> categories = new ObjectArrayList<>();
}
