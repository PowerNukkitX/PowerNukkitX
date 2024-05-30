package cn.nukkit.network.protocol.types;

import cn.nukkit.utils.JSONUtils;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrimData {
    @UnmodifiableView
    public static List<TrimPattern> trimPatterns;
    @UnmodifiableView
    public static List<TrimMaterial> trimMaterials;

    static {
        try (var stream = TrimData.class.getClassLoader().getResourceAsStream("trim_data.json")) {
            JsonObject obj = JSONUtils.from(stream, JsonObject.class);
            ArrayList<TrimPattern> l1 = new ArrayList<>();
            ArrayList<TrimMaterial> l2 = new ArrayList<>();
            for (var e : obj.getAsJsonArray("patterns").asList()) {
                JsonObject asJsonObject = e.getAsJsonObject();
                l1.add(new TrimPattern(asJsonObject.get("itemName").getAsString(), asJsonObject.get("patternId").getAsString()));
            }
            for (var e : obj.getAsJsonArray("materials").asList()) {
                JsonObject asJsonObject = e.getAsJsonObject();
                l2.add(new TrimMaterial(asJsonObject.get("materialId").getAsString(), asJsonObject.get("color").getAsString(), asJsonObject.get("itemName").getAsString()));
            }
            trimPatterns = Collections.unmodifiableList(l1);
            trimMaterials = Collections.unmodifiableList(l2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
