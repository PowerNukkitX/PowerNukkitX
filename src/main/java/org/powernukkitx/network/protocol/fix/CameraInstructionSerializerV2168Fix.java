package org.powernukkitx.network.protocol.fix;

import io.netty.buffer.ByteBuf;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodecHelper;
import org.cloudburstmc.protocol.bedrock.codec.v818.serializer.CameraInstructionSerializer_v818;
import org.cloudburstmc.protocol.bedrock.codec.v924.serializer.CameraInstructionSerializer_v924;
import org.cloudburstmc.protocol.bedrock.data.camera.CameraAttachToEntityInstruction;
import org.cloudburstmc.protocol.bedrock.data.camera.CameraFovInstruction;
import org.cloudburstmc.protocol.bedrock.data.camera.EasingType;
import org.cloudburstmc.protocol.bedrock.packet.CameraInstructionPacket;
import org.cloudburstmc.protocol.common.util.OptionalBoolean;

/**
 * Fixes {@link CameraFovInstruction} (de)serialization for 1.26.10+ clients (protocol v818+,
 * negotiated here via Bedrock_v2168 for MC 1.26.42).
 * <p>
 * The bundled bedrock-codec (3.0.0.Beta7-Debug-SNAPSHOT) has no CameraInstructionSerializer newer
 * than {@link CameraInstructionSerializer_v924} (v924 only adds spline support on top of v827/v859;
 * no later version touches the FOV instruction). That chain's FOV write, introduced in
 * {@code CameraInstructionSerializer_v827}, still writes {@code easeType} as a raw byte enum
 * ordinal ({@code buf.writeByte(easeType.ordinal())}). Modern clients (confirmed against PMMP's
 * BedrockProtocol {@code CameraFovInstruction.php}) read/write it as a length-prefixed string
 * ({@code easeType.getSerializeName()}, e.g. "out_quad") -- the same string encoding this codec
 * already uses for spline easing (see {@code CameraInstructionSerializer_v924.writeSplineInstruction}).
 * Sending the old byte encoding desyncs the client's packet reader and disconnects it.
 * <p>
 * There's no overridable hook for just the FOV instruction: v827/v859 inline the write/read as
 * private synthetic lambda methods inside {@code serialize()}/{@code deserialize()}. So this class
 * reimplements those two methods, delegating everything before FOV (set/clear/fade/target/removeTarget
 * instructions) to {@code CameraInstructionSerializer_v818.INSTANCE} -- the last ancestor that
 * doesn't touch FOV -- then writes/reads the FOV instruction with the corrected string encoding,
 * then replicates v859's spline/attachToEntity/detachFromEntity handling verbatim (spline itself
 * still delegates virtually to this class's inherited v924 writeSplineInstruction/readSplineInstruction).
 */
public class CameraInstructionSerializerV2168Fix extends CameraInstructionSerializer_v924 {

    public static final CameraInstructionSerializerV2168Fix INSTANCE = new CameraInstructionSerializerV2168Fix();

    @Override
    public void serialize(ByteBuf buffer, BedrockCodecHelper helper, CameraInstructionPacket packet) {
        CameraInstructionSerializer_v818.INSTANCE.serialize(buffer, helper, packet);

        helper.writeOptionalNull(buffer, packet.getFovInstruction(), (buf, fov) -> {
            buf.writeFloatLE(fov.getFov());
            buf.writeFloatLE(fov.getEaseTime());
            helper.writeString(buf, fov.getEaseType().getSerializeName());
            buf.writeBoolean(fov.isClear());
        });

        helper.writeOptionalNull(buffer, packet.getSplineInstruction(), this::writeSplineInstruction);

        helper.writeOptionalNull(buffer, packet.getAttachToEntityInstruction(), (buf, attach) ->
                buf.writeLongLE(attach.getEntityActorID()));

        helper.writeOptional(buffer, OptionalBoolean::isPresent, packet.getDetachFromEntity(),
                (buf, detach) -> buf.writeBoolean(detach.getAsBoolean()));
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockCodecHelper helper, CameraInstructionPacket packet) {
        CameraInstructionSerializer_v818.INSTANCE.deserialize(buffer, helper, packet);

        packet.setFovInstruction(helper.readOptional(buffer, null, (ByteBuf buf) -> {
            float fov = buf.readFloatLE();
            float easeTime = buf.readFloatLE();
            EasingType easeType = EasingType.fromName(helper.readString(buf));
            boolean clear = buf.readBoolean();
            return new CameraFovInstruction(fov, easeTime, easeType, clear);
        }));

        packet.setSplineInstruction(helper.readOptional(buffer, null, this::readSplineInstruction));

        packet.setAttachToEntityInstruction(helper.readOptional(buffer, null, (ByteBuf buf) -> {
            var attach = new CameraAttachToEntityInstruction();
            attach.setEntityActorID(buf.readLongLE());
            return attach;
        }));

        packet.setDetachFromEntity(helper.readOptional(buffer, OptionalBoolean.empty(),
                (ByteBuf buf) -> OptionalBoolean.of(buf.readBoolean())));
    }
}
