package cn.nukkit.blockproperty;

import cn.nukkit.Server;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

@SuppressWarnings("unchecked")
public final class PropertyTypes {
    private static final Map<String, String> propertyTypeMap;

    static {
        Gson gson = new GsonBuilder().create();
        try (InputStream resourceAsStream = Server.class.getModule().getResourceAsStream("block_property_types.json")) {
            assert resourceAsStream != null;
            propertyTypeMap = new Object2ObjectOpenHashMap<String, String>(gson.fromJson(new InputStreamReader(resourceAsStream), Map.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public static String getPropertyTypeString(String propertyName) {
        return propertyTypeMap.get(propertyName);
    }
}
