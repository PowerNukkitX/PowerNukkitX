package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.process.login.AuthPayload;
import cn.nukkit.network.process.login.AuthType;
import cn.nukkit.network.process.login.CertificateChainPayload;
import cn.nukkit.network.process.login.TokenPayload;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import lombok.*;
import org.jose4j.json.JsonUtil;
import org.jose4j.lang.JoseException;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class SubClientLoginPacket extends DataPacket {

    private AuthPayload authPayload;
    private String clientJwt;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        ByteBuf jwt = byteBuf.readSlice(byteBuf.readIntLE());

        String authJwt = this.readString(jwt);
        this.setAuthPayload(readAuthJwt(authJwt));

        String value = jwt.readCharSequence(jwt.readIntLE(), StandardCharsets.UTF_8).toString();
        this.setClientJwt(value);
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        String authJwt = writeAuthJwt(this.getAuthPayload());
        String clientJwt = this.getClientJwt();
        this.writeJwts(byteBuf, authJwt, clientJwt);
    }

    @Override
    public int pid() {
        return ProtocolInfo.SUB_CLIENT_LOGIN_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    protected String writeAuthJwt(AuthPayload payload) {
        Objects.requireNonNull(payload, "AuthPayload cannot be null");
        Preconditions.checkArgument(payload.getAuthType() != null && payload.getAuthType() != AuthType.UNKNOWN,
                "Client requires non-null and non-UNKNOWN AuthType for login");

        Map<String, Object> object = new HashMap<>();
        object.put("AuthenticationType", payload.getAuthType().ordinal() - 1);

        if (payload instanceof TokenPayload) {
            object.put("Token", ((TokenPayload) payload).getToken());
            object.put("Certificate", "");
        } else if (payload instanceof CertificateChainPayload) {
            Map<String, Object> json = new HashMap<>();
            json.put("chain", ((CertificateChainPayload) payload).getChain());
            object.put("Certificate", JsonUtil.toJson(json));
            object.put("Token", "");
        } else {
            throw new IllegalArgumentException("Unsupported AuthPayload type: " + payload.getClass().getName());
        }

        return JsonUtil.toJson(object);
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

            if (payload.containsKey("Certificate") && payload.get("Certificate") instanceof String certJson && !((String) payload.get("Certificate")).isEmpty()) {
                Map<String, Object> certData = JsonUtil.parseJson(certJson);
                if (!certData.containsKey("chain") || !(certData.get("chain") instanceof List)) {
                    throw new IllegalArgumentException("Invalid Certificate chain in JWT");
                }
                List<String> chain = (List<String>) certData.get("chain");
                return new CertificateChainPayload(chain, authType);
            } else if (payload.containsKey("Token") && payload.get("Token") instanceof String token && !((String) payload.get("Token")).isEmpty()) {
                return new TokenPayload(token, authType);
            } else {
                throw new IllegalArgumentException("Invalid AuthPayload in JWT");
            }
        } catch (JoseException e) {
            throw new IllegalArgumentException("Failed to parse auth payload", e);
        }
    }

    protected void writeJwts(HandleByteBuf buf, String authJwt, String clientJwt) {
        int authLength = ByteBufUtil.utf8Bytes(authJwt);
        int clientLength = ByteBufUtil.utf8Bytes(clientJwt);

        buf.writeUnsignedVarInt(authLength + clientLength + 8);
        buf.writeIntLE(authLength);
        buf.writeCharSequence(authJwt, StandardCharsets.UTF_8);
        buf.writeIntLE(clientLength);
        buf.writeCharSequence(clientJwt, StandardCharsets.UTF_8);
    }

    protected String readString(ByteBuf buf) {
        return (String) buf.readCharSequence(buf.readIntLE(), StandardCharsets.UTF_8);
    }
}
