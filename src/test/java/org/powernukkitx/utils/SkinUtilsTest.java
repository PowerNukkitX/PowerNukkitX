package org.powernukkitx.utils;

import org.cloudburstmc.protocol.bedrock.data.skin.AnimatedTextureType;
import org.cloudburstmc.protocol.bedrock.data.skin.AnimationData;
import org.cloudburstmc.protocol.bedrock.data.skin.ImageData;
import org.cloudburstmc.protocol.bedrock.data.skin.Skin;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class SkinUtilsTest {

    private static Skin.Builder baseSkin() {
        return Skin.builder()
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
                .geometryDataEngineVersion("");
    }

    @Test
    void acceptsStandardSkin() {
        Assertions.assertTrue(SkinUtils.isValid(baseSkin().build()));
    }

    @Test
    void rejectsOversizedSkinImage() {
        Skin skin = baseSkin()
                .skinData(ImageData.of(512, 512, new byte[1024 * 1024 + 1]))
                .build();

        Assertions.assertFalse(SkinUtils.isValid(skin));
    }

    @Test
    void rejectsOversizedCapeImage() {
        Skin skin = baseSkin()
                .capeData(ImageData.of(512, 512, new byte[1024 * 1024 + 1]))
                .build();

        Assertions.assertFalse(SkinUtils.isValid(skin));
    }

    @Test
    void rejectsOversizedAnimationFrame() {
        List<AnimationData> animations = List.of(new AnimationData(
                ImageData.of(512, 512, new byte[1024 * 1024 + 1]),
                AnimatedTextureType.FACE,
                1f
        ));

        Assertions.assertFalse(SkinUtils.isValid(baseSkin().animations(animations).build()));
    }

    @Test
    void rejectsTooManyAnimationBytes() {
        List<AnimationData> animations = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            animations.add(new AnimationData(
                    ImageData.of(512, 512, new byte[1024 * 1024]),
                    AnimatedTextureType.FACE,
                    1f
            ));
        }

        Assertions.assertFalse(SkinUtils.isValid(baseSkin().animations(animations).build()));
    }
}
