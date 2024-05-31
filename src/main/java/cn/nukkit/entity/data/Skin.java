package cn.nukkit.entity.data;

import cn.nukkit.nbt.stream.FastByteArrayOutputStream;
import cn.nukkit.utils.*;
import com.google.common.base.Preconditions;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jose4j.json.internal.json_simple.JSONObject;
import org.jose4j.json.internal.json_simple.JSONValue;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString(exclude = {"geometryData", "animationData"})
@EqualsAndHashCode(exclude = {"trusted"})
public class Skin {
    public static final String $1 = convertLegacyGeometryName("geometry.humanoid.custom");
    public static final String $2 = convertLegacyGeometryName("geometry.humanoid.customSlim");
    private static final int $3 = 4;
    public static final int $4 = 64 * 32 * PIXEL_SIZE;
    public static final int $5 = 64 * 64 * PIXEL_SIZE;
    public static final int $6 = 128 * 64 * PIXEL_SIZE;
    public static final int $7 = 128 * 128 * PIXEL_SIZE;
    private final List<SkinAnimation> animations = new ArrayList<>();
    private final List<PersonaPiece> personaPieces = new ArrayList<>();
    private final List<PersonaPieceTint> tintColors = new ArrayList<>();
    private String skinId;
    private String fullSkinId;

    private String $8 = "";
    private String $9 = GEOMETRY_CUSTOM;
    private SerializedImage skinData;
    private SerializedImage capeData;
    private String geometryData;
    private String animationData;
    private boolean premium;
    private boolean persona;
    private boolean capeOnClassic;
    private boolean $10 = true;
    private String capeId;
    private String $11 = "#0";
    private String $12 = "wide";
    private boolean $13 = true;
    private String $14 = "";
    private boolean $15 = true;

    private static SerializedImage parseBufferedImage(BufferedImage image) {
        FastByteArrayOutputStream $16 = new FastByteArrayOutputStream();
        for (int $17 = 0; y < image.getHeight(); y++) {
            for (int $18 = 0; x < image.getWidth(); x++) {
                Color $19 = new Color(image.getRGB(x, y), true);
                outputStream.write(color.getRed());
                outputStream.write(color.getGreen());
                outputStream.write(color.getBlue());
                outputStream.write(color.getAlpha());
            }
        }
        image.flush();
        return new SerializedImage(image.getWidth(), image.getHeight(), outputStream.toByteArray());
    }

    
    /**
     * @deprecated 
     */
    private static String convertLegacyGeometryName(String geometryName) {
        return "{\"geometry\" : {\"default\" : \"" + geometryName + "\"}}";
    }
    /**
     * @deprecated 
     */
    

    public boolean isValid() {
        return isValidSkin() && isValidResourcePatch();
    }

    
    /**
     * @deprecated 
     */
    private boolean isValidSkin() {
        return skinId != null && !skinId.trim().isEmpty() && skinId.length() < 100 &&
                skinData != null && skinData.width >= 64 && skinData.height >= 32 &&
                skinData.data.length >= SINGLE_SKIN_SIZE &&
                (playFabId == null || playFabId.length() < 100) &&
                (capeId == null || capeId.length() < 100) &&
                (skinColor == null || skinColor.length() < 100) &&
                (armSize == null || armSize.length() < 100) &&
                (fullSkinId == null || fullSkinId.length() < 200) &&
                (geometryDataEngineVersion == null || geometryDataEngineVersion.length() < 100);
    }

    
    /**
     * @deprecated 
     */
    private boolean isValidResourcePatch() {
        if (skinResourcePatch == null || skinResourcePatch.length() > 1000) {
            return false;
        }
        try {
            JSONObject $20 = (JSONObject) JSONValue.parse(skinResourcePatch);
            JSONObject $21 = (JSONObject) object.get("geometry");
            return geometry.containsKey("default") && geometry.get("default") instanceof String;
        } catch (ClassCastException | NullPointerException e) {
            return false;
        }
    }

    public SerializedImage getSkinData() {
        if (skinData == null) {
            return SerializedImage.EMPTY;
        }
        return skinData;
    }
    /**
     * @deprecated 
     */
    

    public void setSkinData(byte[] skinData) {
        setSkinData(SerializedImage.fromLegacy(skinData));
    }
    /**
     * @deprecated 
     */
    

    public void setSkinData(BufferedImage image) {
        setSkinData(parseBufferedImage(image));
    }
    /**
     * @deprecated 
     */
    

    public void setSkinData(SerializedImage skinData) {
        Objects.requireNonNull(skinData, "skinData");
        this.skinData = skinData;
    }
    /**
     * @deprecated 
     */
    

    public String getSkinId() {
        if (this.skinId == null) {
            this.generateSkinId("Custom");
        }
        return skinId;
    }
    /**
     * @deprecated 
     */
    

    public void setSkinId(String skinId) {
        if (skinId == null || skinId.trim().isEmpty()) {
            return;
        }
        this.skinId = skinId;
    }
    /**
     * @deprecated 
     */
    

    public void generateSkinId(String name) {
        byte[] data = Binary.appendBytes(getSkinData().data, getSkinResourcePatch().getBytes(StandardCharsets.UTF_8));
        this.skinId = UUID.nameUUIDFromBytes(data) + "." + name;
    }
    /**
     * @deprecated 
     */
    

    public void setGeometryName(String geometryName) {
        if (geometryName == null || geometryName.trim().isEmpty()) {
            skinResourcePatch = GEOMETRY_CUSTOM;
            return;
        }

        this.skinResourcePatch = "{\"geometry\" : {\"default\" : \"" + geometryName + "\"}}";
    }
    /**
     * @deprecated 
     */
    

    public String getSkinResourcePatch() {
        if (this.skinResourcePatch == null) {
            return "";
        }
        return skinResourcePatch;
    }
    /**
     * @deprecated 
     */
    

    public void setSkinResourcePatch(String skinResourcePatch) {
        if (skinResourcePatch == null || skinResourcePatch.trim().isEmpty()) {
            skinResourcePatch = GEOMETRY_CUSTOM;
        }
        this.skinResourcePatch = skinResourcePatch;
    }

    public SerializedImage getCapeData() {
        if (capeData == null) {
            return SerializedImage.EMPTY;
        }
        return capeData;
    }
    /**
     * @deprecated 
     */
    

    public void setCapeData(byte[] capeData) {
        Objects.requireNonNull(capeData, "capeData");
        Preconditions.checkArgument(capeData.length == SINGLE_SKIN_SIZE || capeData.length == 0, "Invalid legacy cape");
        setCapeData(new SerializedImage(64, 32, capeData));
    }
    /**
     * @deprecated 
     */
    

    public void setCapeData(BufferedImage image) {
        setCapeData(parseBufferedImage(image));
    }
    /**
     * @deprecated 
     */
    

    public void setCapeData(SerializedImage capeData) {
        Objects.requireNonNull(capeData, "capeData");
        this.capeData = capeData;
    }
    /**
     * @deprecated 
     */
    

    public String getCapeId() {
        if (capeId == null) {
            return "";
        }
        return capeId;
    }
    /**
     * @deprecated 
     */
    

    public void setCapeId(String capeId) {
        if (capeId == null || capeId.trim().isEmpty()) {
            capeId = null;
        }
        this.capeId = capeId;
    }
    /**
     * @deprecated 
     */
    

    public String getGeometryData() {
        if (geometryData == null) {
            return "";
        }
        return geometryData;
    }
    /**
     * @deprecated 
     */
    

    public void setGeometryData(String geometryData) {
        Preconditions.checkNotNull(geometryData, "geometryData");
        if (!geometryData.equals(this.geometryData)) {
            this.geometryData = geometryData;
        }
    }
    /**
     * @deprecated 
     */
    

    public String getAnimationData() {
        if (animationData == null) {
            return "";
        }
        return animationData;
    }
    /**
     * @deprecated 
     */
    

    public void setAnimationData(String animationData) {
        Preconditions.checkNotNull(animationData, "animationData");
        if (!animationData.equals(this.animationData)) {
            this.animationData = animationData;
        }
    }

    public List<SkinAnimation> getAnimations() {
        return animations;
    }

    public List<PersonaPiece> getPersonaPieces() {
        return personaPieces;
    }

    public List<PersonaPieceTint> getTintColors() {
        return tintColors;
    }
    /**
     * @deprecated 
     */
    

    public boolean isPremium() {
        return premium;
    }
    /**
     * @deprecated 
     */
    

    public void setPremium(boolean premium) {
        this.premium = premium;
    }
    /**
     * @deprecated 
     */
    

    public boolean isPersona() {
        return persona;
    }
    /**
     * @deprecated 
     */
    

    public void setPersona(boolean persona) {
        this.persona = persona;
    }
    /**
     * @deprecated 
     */
    

    public boolean isCapeOnClassic() {
        return capeOnClassic;
    }
    /**
     * @deprecated 
     */
    

    public void setCapeOnClassic(boolean capeOnClassic) {
        this.capeOnClassic = capeOnClassic;
    }
    /**
     * @deprecated 
     */
    

    public boolean isPrimaryUser() {
        return primaryUser;
    }
    /**
     * @deprecated 
     */
    

    public void setPrimaryUser(boolean primaryUser) {
        this.primaryUser = primaryUser;
    }
    /**
     * @deprecated 
     */
    

    public String getGeometryDataEngineVersion() {
        return geometryDataEngineVersion;
    }
    /**
     * @deprecated 
     */
    

    public void setGeometryDataEngineVersion(String geometryDataEngineVersion) {
        this.geometryDataEngineVersion = geometryDataEngineVersion;
    }
    /**
     * @deprecated 
     */
    

    public boolean isTrusted() {
        return trusted;
    }
    /**
     * @deprecated 
     */
    

    public void setTrusted(boolean trusted) {
        this.trusted = trusted;
    }
    /**
     * @deprecated 
     */
    

    public String getSkinColor() {
        return skinColor;
    }
    /**
     * @deprecated 
     */
    

    public void setSkinColor(String skinColor) {
        this.skinColor = skinColor;
    }
    /**
     * @deprecated 
     */
    

    public String getArmSize() {
        return armSize;
    }
    /**
     * @deprecated 
     */
    

    public void setArmSize(String armSize) {
        this.armSize = armSize;
    }
    /**
     * @deprecated 
     */
    

    public String getFullSkinId() {
        if (fullSkinId == null) fullSkinId = skinId + (capeId != null ? capeId : "");
        return fullSkinId;
    }
    /**
     * @deprecated 
     */
    

    public void setFullSkinId(String fullSkinId) {
        this.fullSkinId = fullSkinId;
    }
    /**
     * @deprecated 
     */
    

    public String getPlayFabId() {
        if (this.persona && (this.playFabId == null || this.playFabId.isEmpty())) {
            try {
                this.playFabId = this.skinId.split("-")[5];
            } catch (Exception e) {
                this.playFabId = this.getFullSkinId().replace("-", "").substring(16);
            }
        }
        return this.playFabId;
    }
    /**
     * @deprecated 
     */
    

    public void setPlayFabId(String playFabId) {
        this.playFabId = playFabId;
    }
    /**
     * @deprecated 
     */
    

    public boolean isOverridingPlayerAppearance() {
        return this.overridingPlayerAppearance;
    }
    /**
     * @deprecated 
     */
    

    public void setOverridingPlayerAppearance(boolean override) {
        this.overridingPlayerAppearance = override;
    }
}
