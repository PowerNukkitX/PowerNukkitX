package cn.nukkit.entity.data.human;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.cloudburstmc.protocol.bedrock.data.skin.SerializedSkin;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class Skin {

    private final SerializedSkin skin;

    @Setter
    private boolean trusted;


}
