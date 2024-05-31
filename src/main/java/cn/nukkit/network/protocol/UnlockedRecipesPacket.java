package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;


@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UnlockedRecipesPacket extends DataPacket {
    public boolean unlockedNotification;
    public final List<String> unlockedRecipes = new ObjectArrayList<>();

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return ProtocolInfo.UNLOCKED_RECIPES_PACKET;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {
        this.unlockedNotification = byteBuf.readBoolean();
        int $1 = (int) byteBuf.readUnsignedVarInt();
        for ($2nt $1 = 0; i < count; i++) {
            this.unlockedRecipes.add(byteBuf.readString());
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeBoolean(this.unlockedNotification);
        byteBuf.writeUnsignedVarInt(this.unlockedRecipes.size());
        for (String recipe : this.unlockedRecipes) {
            byteBuf.writeString(recipe);
        }
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
