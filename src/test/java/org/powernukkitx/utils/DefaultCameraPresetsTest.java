package org.powernukkitx.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodecHelper;
import org.cloudburstmc.protocol.bedrock.data.camera.CameraPreset;
import org.cloudburstmc.protocol.bedrock.data.camera.CameraSetInstruction;
import org.cloudburstmc.protocol.bedrock.packet.CameraInstructionPacket;
import org.cloudburstmc.protocol.common.NamedDefinition;
import org.cloudburstmc.protocol.common.util.OptionalBoolean;
import org.junit.jupiter.api.Test;
import org.powernukkitx.network.NetworkConstants;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultCameraPresetsTest {

    @Test
    void everyPresetHasARegisteredDefinition() {
        for (CameraPreset preset : DefaultCameraPresets.getAll()) {
            NamedDefinition definition = DefaultCameraPresets.getDefinition(preset.getName());

            assertNotNull(definition, preset.getName());
            // the serializer checks this by reference, so the shared instance has to come back
            assertTrue(DefaultCameraPresets.getDefinitions().isRegistered(definition), preset.getName());
        }
    }

    @Test
    void runtimeIdMatchesTheOrderPresetsAreSentIn() {
        List<CameraPreset> presets = DefaultCameraPresets.getAll();

        for (int runtimeId = 0; runtimeId < presets.size(); runtimeId++) {
            assertEquals(runtimeId, DefaultCameraPresets.getDefinition(presets.get(runtimeId).getName()).getRuntimeId());
        }
    }

    @Test
    void returnsNullForAnUnknownPreset() {
        assertNull(DefaultCameraPresets.getDefinition("minecraft:not_a_preset"));
    }

    @Test
    void encodesACameraInstructionOncePresetDefinitionsAreSet() {
        BedrockCodecHelper helper = NetworkConstants.CODEC.createHelper();
        helper.setCameraPresetDefinitions(DefaultCameraPresets.getDefinitions());

        CameraInstructionPacket packet = new CameraInstructionPacket();
        packet.setSetInstruction(CameraSetInstruction.builder()
                .preset(DefaultCameraPresets.getDefinition(DefaultCameraPresets.FREE.getName()))
                .defaultPreset(OptionalBoolean.of(true))
                .build());

        ByteBuf buf = Unpooled.buffer();
        try {
            assertDoesNotThrow(() -> NetworkConstants.CODEC.tryEncode(helper, buf, packet));
            assertTrue(buf.readableBytes() > 0);
        } finally {
            buf.release();
        }
    }
}
