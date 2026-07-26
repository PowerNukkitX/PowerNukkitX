package org.powernukkitx.entity.data.human;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class Skin {

    private final org.cloudburstmc.protocol.bedrock.data.skin.Skin skin;

    @Setter
    private boolean trusted;


}
