package cn.nukkit.network.connection.netty.codec.encryption;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.RequiredArgsConstructor;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RequiredArgsConstructor
public class BedrockEncryptionDecoder extends MessageToMessageDecoder<ByteBuf> {

    public static final String NAME = "bedrock-encryption-decoder";

    private static final boolean VALIDATE = Boolean.getBoolean("cloudburst.validateEncryption");

    private final AtomicLong packetCounter = new AtomicLong();
    private final SecretKey key;
    private final Cipher cipher;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        ByteBuffer inBuffer = in.nioBuffer();
        ByteBuffer outBuffer = inBuffer.duplicate();

        // Copy-safe so we can use the same buffer.
        this.cipher.update(inBuffer, outBuffer);

        ByteBuf output = in.readSlice(in.readableBytes() - 8);

        if (VALIDATE) {
            ByteBuf trailer = in.readSlice(8);

            byte[] actual = new byte[8];
            trailer.readBytes(actual);

            byte[] expected = BedrockEncryptionEncoder.generateTrailer(output, this.key, this.packetCounter);

            if (!Arrays.equals(expected, actual)) {
                throw new CorruptedFrameException("Invalid encryption trailer");
            }
        }
        out.add(output.retain());
    }
}
