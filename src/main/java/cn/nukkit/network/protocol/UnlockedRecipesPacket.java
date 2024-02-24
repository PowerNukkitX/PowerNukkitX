package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;

import java.util.List;


@Getter
public class UnlockedRecipesPacket extends DataPacket {
    public boolean unlockedNotification;
    public final List<String> unlockedRecipes = new ObjectArrayList<>();

    @Override
    public int pid() {
        return ProtocolInfo.UNLOCKED_RECIPES_PACKET;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.unlockedNotification = byteBuf.readBoolean();
        int count = (int) byteBuf.readUnsignedVarInt();
        for (int i = 0; i < count; i++) {
            this.unlockedRecipes.add(byteBuf.readString());
        }
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeBoolean(this.unlockedNotification);
        byteBuf.writeUnsignedVarInt(this.unlockedRecipes.size());
        for (String recipe : this.unlockedRecipes) {
            byteBuf.writeString(recipe);
        }
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
