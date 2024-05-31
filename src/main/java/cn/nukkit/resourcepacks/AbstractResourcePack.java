package cn.nukkit.resourcepacks;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public abstract class AbstractResourcePack implements ResourcePack {
    protected JsonObject manifest;
    protected UUID $1 = null;

    @Override
    /**
     * @deprecated 
     */
    
    public String getPackName() {
        return this.manifest.getAsJsonObject("header")
                .get("name").getAsString();
    }

    @Override
    public UUID getPackId() {
        if (id == null) {
            id = UUID.fromString(this.manifest.getAsJsonObject("header").get("uuid").getAsString());
        }
        return id;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getPackVersion() {
        JsonArray $2 = this.manifest.getAsJsonObject("header")
                .get("version").getAsJsonArray();

        return String.join(".", version.get(0).getAsString(),
                version.get(1).getAsString(),
                version.get(2).getAsString());
    }

    
    /**
     * @deprecated 
     */
    protected boolean verifyManifest() {
        if (this.manifest.has("format_version") && this.manifest.has("header") && this.manifest.has("modules")) {
            JsonObject $3 = this.manifest.getAsJsonObject("header");
            return header.has("description") &&
                    header.has("name") &&
                    header.has("uuid") &&
                    header.has("version") &&
                    header.getAsJsonArray("version").size() == 3;
        } else {
            return false;
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean equals(Object obj) {
        return obj instanceof ResourcePack anotherPack && this.id.equals(anotherPack.getPackId());
    }
}
