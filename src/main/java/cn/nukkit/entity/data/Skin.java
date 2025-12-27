package cn.nukkit.entity.data;

import cn.nukkit.nbt.stream.FastByteArrayOutputStream;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.PersonaPiece;
import cn.nukkit.utils.PersonaPieceTint;
import cn.nukkit.utils.SerializedImage;
import cn.nukkit.utils.SkinAnimation;
import com.google.common.base.Preconditions;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
@Slf4j
public class Skin {
    public static final String GEOMETRY_CUSTOM = convertLegacyGeometryName("geometry.humanoid.custom");
    public static final String GEOMETRY_CUSTOM_SLIM = convertLegacyGeometryName("geometry.humanoid.customSlim");
    static final String GEOMETRY_HUMANOID;

    static {
        String geoData;
        try (var stream = Skin.class.getClassLoader().getResourceAsStream("gamedata/skin_geometry.json")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            geoData = reader.lines().reduce("", (acc, line) -> acc + line + "\n");
        } catch (IOException e) {
            geoData = "";
            log.error("Failed to load skin geometry data", e);
        }
        GEOMETRY_HUMANOID = geoData;
    }

    private static final int PIXEL_SIZE = 4;
    public static final int SINGLE_SKIN_SIZE = 32 * 32 * PIXEL_SIZE;
    public static final int SKIN_64_32_SIZE = 64 * 32 * PIXEL_SIZE;
    public static final int DOUBLE_SKIN_SIZE = 64 * 64 * PIXEL_SIZE;
    public static final int SKIN_128_64_SIZE = 128 * 64 * PIXEL_SIZE;
    public static final int SKIN_128_128_SIZE = 128 * 128 * PIXEL_SIZE;
    private final List<SkinAnimation> animations = new ArrayList<>();
    private final List<PersonaPiece> personaPieces = new ArrayList<>();
    private final List<PersonaPieceTint> tintColors = new ArrayList<>();
    private String skinId;
    private String fullSkinId;

    private String playFabId = "";
    private String skinResourcePatch = GEOMETRY_CUSTOM;
    private SerializedImage skinData;
    private SerializedImage capeData;
    private String geometryData = GEOMETRY_HUMANOID;
    private String animationData;
    private boolean premium;
    private boolean persona;
    private boolean capeOnClassic;
    private boolean primaryUser = true;
    private String capeId;
    private String skinColor = "#0";
    private String armSize = "wide";
    private boolean trusted = true;
    private String geometryDataEngineVersion = "0.0.0";
    private boolean overridingPlayerAppearance = true;

    private static SerializedImage parseBufferedImage(BufferedImage image) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color color = new Color(image.getRGB(x, y), true);
                outputStream.write(color.getRed());
                outputStream.write(color.getGreen());
                outputStream.write(color.getBlue());
                outputStream.write(color.getAlpha());
            }
        }
        image.flush();
        return new SerializedImage(image.getWidth(), image.getHeight(), outputStream.toByteArray());
    }

    private static String convertLegacyGeometryName(String geometryName) {
        return "{\"geometry\" : {\"default\" : \"" + geometryName + "\"}}";
    }

    public boolean isValid() {
        return isValidSkin() && isValidResourcePatch();
    }

    private boolean isValidSkin() {
        return skinId != null && !skinId.trim().isEmpty() && skinId.length() < 100 &&
                skinData != null && skinData.width >= 32 && skinData.height >= 32 &&
                skinData.data.length >= SINGLE_SKIN_SIZE &&
                (playFabId == null || playFabId.length() < 100) &&
                (capeId == null || capeId.length() < 100) &&
                (skinColor == null || skinColor.length() < 100) &&
                (armSize == null || armSize.length() < 100) &&
                (fullSkinId == null || fullSkinId.length() < 200) &&
                (geometryDataEngineVersion == null || geometryDataEngineVersion.length() < 100) &&
                (animationData == null || animationData.length() < 1000) &&
                animations.size() <= 100 &&
                personaPieces.size() <= 100 &&
                tintColors.size() <= 100;
    }

    private boolean isValidResourcePatch() {
        if (skinResourcePatch == null || skinResourcePatch.length() > 1000) {
            return false;
        }
        try {
            JSONObject object = (JSONObject) new JSONParser().parse(skinResourcePatch);
            JSONObject geometry = (JSONObject) object.get("geometry");
            return geometry != null
                    && geometry.containsKey("default")
                    && geometry.get("default") instanceof String;
        } catch (ParseException | ClassCastException | NullPointerException e) {
            return false;
        }
    }

    public SerializedImage getSkinData() {
        if (skinData == null) {
            return SerializedImage.EMPTY;
        }
        return skinData;
    }

    public void setSkinData(byte[] skinData) {
        setSkinData(SerializedImage.fromLegacy(skinData));
    }

    public void setSkinData(BufferedImage image) {
        setSkinData(parseBufferedImage(image));
    }

    public void setSkinData(SerializedImage skinData) {
        Objects.requireNonNull(skinData, "skinData");
        this.skinData = skinData;
    }

    public String getSkinId() {
        if (this.skinId == null) {
            this.generateSkinId("Custom");
        }
        return skinId;
    }

    public void setSkinId(String skinId) {
        if (skinId == null || skinId.trim().isEmpty()) {
            return;
        }
        this.skinId = skinId;
    }

    public void generateSkinId(String name) {
        byte[] data = Binary.appendBytes(getSkinData().data, getSkinResourcePatch().getBytes(StandardCharsets.UTF_8));
        this.skinId = UUID.nameUUIDFromBytes(data) + "." + name;
    }

    public void setGeometryName(String geometryName) {
        if (geometryName == null || geometryName.trim().isEmpty()) {
            skinResourcePatch = GEOMETRY_CUSTOM;
            return;
        }

        this.skinResourcePatch = "{\"geometry\" : {\"default\" : \"" + geometryName + "\"}}";
    }

    public String getSkinResourcePatch() {
        if (this.skinResourcePatch == null) {
            return "";
        }
        return skinResourcePatch;
    }

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

    public void setCapeData(byte[] capeData) {
        Objects.requireNonNull(capeData, "capeData");
        Preconditions.checkArgument(capeData.length == SINGLE_SKIN_SIZE || capeData.length == 0, "Invalid legacy cape");
        setCapeData(new SerializedImage(64, 32, capeData));
    }

    public void setCapeData(BufferedImage image) {
        setCapeData(parseBufferedImage(image));
    }

    public void setCapeData(SerializedImage capeData) {
        Objects.requireNonNull(capeData, "capeData");
        this.capeData = capeData;
    }

    public String getCapeId() {
        if (capeId == null) {
            return "";
        }
        return capeId;
    }

    public void setCapeId(String capeId) {
        if (capeId == null || capeId.trim().isEmpty()) {
            capeId = null;
        }
        this.capeId = capeId;
    }

    public String getGeometryData() {
        if (geometryData == null) {
            return "";
        }
        return geometryData;
    }

    public void setGeometryData(String geometryData) {
        Preconditions.checkNotNull(geometryData, "geometryData");
        if (!geometryData.equals(this.geometryData)) {
            this.geometryData = geometryData;
        }
    }

    public String getAnimationData() {
        if (animationData == null) {
            return "";
        }
        return animationData;
    }

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

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public boolean isPersona() {
        return persona;
    }

    public void setPersona(boolean persona) {
        this.persona = persona;
    }

    public boolean isCapeOnClassic() {
        return capeOnClassic;
    }

    public void setCapeOnClassic(boolean capeOnClassic) {
        this.capeOnClassic = capeOnClassic;
    }

    public boolean isPrimaryUser() {
        return primaryUser;
    }

    public void setPrimaryUser(boolean primaryUser) {
        this.primaryUser = primaryUser;
    }

    public String getGeometryDataEngineVersion() {
        return geometryDataEngineVersion;
    }

    public void setGeometryDataEngineVersion(String geometryDataEngineVersion) {
        this.geometryDataEngineVersion = geometryDataEngineVersion;
    }

    public boolean isTrusted() {
        return trusted;
    }

    public void setTrusted(boolean trusted) {
        this.trusted = trusted;
    }

    public String getSkinColor() {
        return skinColor;
    }

    public void setSkinColor(String skinColor) {
        this.skinColor = skinColor;
    }

    public String getArmSize() {
        return armSize;
    }

    public void setArmSize(String armSize) {
        this.armSize = armSize;
    }

    public String getFullSkinId() {
        if (fullSkinId == null) fullSkinId = skinId + (capeId != null ? capeId : "");
        return fullSkinId;
    }

    public void setFullSkinId(String fullSkinId) {
        this.fullSkinId = fullSkinId;
    }

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

    public void setPlayFabId(String playFabId) {
        this.playFabId = playFabId;
    }

    public boolean isOverridingPlayerAppearance() {
        return this.overridingPlayerAppearance;
    }

    public void setOverridingPlayerAppearance(boolean override) {
        this.overridingPlayerAppearance = override;
    }
}
