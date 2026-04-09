package cn.nukkit.network;

import lombok.experimental.UtilityClass;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.codec.v944.Bedrock_v944;

/**
 * @author Kaooot
 */
@UtilityClass
public class NetworkConstants {

    public BedrockCodec CODEC = Bedrock_v944.CODEC;
}