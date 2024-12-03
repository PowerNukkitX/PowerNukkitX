package cn.nukkit.network.protocol.types;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlayerInputTick {
    private final long inputTick;
}
