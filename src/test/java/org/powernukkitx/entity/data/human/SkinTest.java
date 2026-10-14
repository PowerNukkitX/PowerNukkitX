package org.powernukkitx.entity.data.human;

import org.cloudburstmc.protocol.bedrock.data.payload.skin.SerializedSkin;
import org.cloudburstmc.protocol.bedrock.data.skin.ImageData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.powernukkitx.utils.SkinConverter;

import java.util.List;

class SkinTest {

    private static org.cloudburstmc.protocol.bedrock.data.skin.Skin protocolSkin() {
        return org.cloudburstmc.protocol.bedrock.data.skin.Skin.builder()
            .skinId("Standard_Custom")
            .playFabId("")
            .geometryName("")
            .skinResourcePatch("{\"geometry\":{\"default\":\"geometry.humanoid.custom\"}}")
            .skinData(ImageData.of(64, 64, new byte[64 * 64 * 4]))
            .animations(List.of())
            .capeData(ImageData.EMPTY)
            .geometryData("")
            .animationData("")
            .capeId("")
            .fullSkinId("Standard_Custom")
            .armSize("wide")
            .skinColor("#0")
            .personaPieces(List.of())
            .tintColors(List.of())
            .geometryDataEngineVersion("")
            .build();
    }

    @Test
    void convertsOnceWhileTrustStaysTheSame() {
        Skin skin = new Skin(protocolSkin(), true);

        SerializedSkin first = skin.toSerializedSkin();

        Assertions.assertSame(first, skin.toSerializedSkin());
        Assertions.assertEquals(SkinConverter.toSerializedSkin(skin.getSkin(), true), first);
    }

    @Test
    void convertsAgainWhenTrustChanges() {
        Skin skin = new Skin(protocolSkin(), true);
        SerializedSkin trusted = skin.toSerializedSkin();

        skin.setTrusted(false);
        SerializedSkin untrusted = skin.toSerializedSkin();

        Assertions.assertNotSame(trusted, untrusted);
        Assertions.assertEquals(SkinConverter.toSerializedSkin(skin.getSkin(), false), untrusted);
    }
}
