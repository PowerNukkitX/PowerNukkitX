package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.process.login.AuthPayload;
import cn.nukkit.network.process.login.AuthType;
import cn.nukkit.network.process.login.CertificateChainPayload;
import cn.nukkit.network.process.login.TokenPayload;
import cn.nukkit.utils.*;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import lombok.*;
import org.jose4j.json.JsonUtil;
import org.jose4j.lang.JoseException;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @since on 15-10-13
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LoginPacket extends DataPacket {
    public int protocol;

    public AuthPayload authPayload;
    public String clientPayload;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        protocol = byteBuf.readInt();

        ByteBuf jwt = byteBuf.readSlice(ByteBufVarInt.readUnsignedInt(byteBuf)); // Get the JWT.
        String authJwtR = (String) jwt.readCharSequence(jwt.readIntLE(), StandardCharsets.UTF_8);

        authPayload = readAuthJwt(authJwtR);
        clientPayload = (String) jwt.readCharSequence(jwt.readIntLE(), StandardCharsets.UTF_8);
    }

    protected AuthPayload readAuthJwt(String authJwt) {
        try {
            Map<String, Object> payload = JsonUtil.parseJson(authJwt);
            Preconditions.checkArgument(payload.containsKey("AuthenticationType"), "Missing AuthenticationType in JWT");
            int authTypeOrdinal = ((Number) payload.get("AuthenticationType")).intValue();
            if (authTypeOrdinal < 0 || authTypeOrdinal >= AuthType.values().length - 1) {
                throw new IllegalArgumentException("Invalid AuthenticationType ordinal: " + authTypeOrdinal);
            }
            AuthType authType = AuthType.values()[authTypeOrdinal + 1];

            if (payload.containsKey("Token") && payload.get("Token") instanceof String && !((String) payload.get("Token")).isEmpty()) {
                String token = (String) payload.get("Token");
                return new TokenPayload(token, authType);
            } else if (payload.containsKey("Certificate") && payload.get("Certificate") instanceof String && !((String) payload.get("Certificate")).isEmpty()) {
                String certJson = (String) payload.get("Certificate");
                Map<String, Object> certData = JsonUtil.parseJson(certJson);
                if (!certData.containsKey("chain") || !(certData.get("chain") instanceof List<?>)) {
                    throw new IllegalArgumentException("Invalid Certificate chain in JWT");
                }
                List<?> chainRaw = (List<?>) certData.get("chain");
                List<String> chain = new ArrayList<>(chainRaw.size());
                for (Object entry : chainRaw) {
                    if (!(entry instanceof String)) {
                        throw new IllegalArgumentException("Invalid Certificate chain entry in JWT");
                    }
                    chain.add((String) entry);
                }
                return new CertificateChainPayload(chain, authType);
            } else {
                throw new IllegalArgumentException("Invalid AuthPayload in JWT");
            }
        } catch (JoseException e) {
            throw new IllegalArgumentException("Failed to parse auth payload", e);
        }
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

    }

    @Override
    public int pid() {
        return ProtocolInfo.LOGIN_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

}
