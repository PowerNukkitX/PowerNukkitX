package cn.nukkit.event.level;


import cn.nukkit.event.HandlerList;
import lombok.Getter;


public class VibrationOccurEvent extends VibrationEvent{

    @Getter
    private static final HandlerList handlers = new HandlerList();

    public VibrationOccurEvent(cn.nukkit.level.vibration.VibrationEvent vibrationEvent) {
        super(vibrationEvent);
    }
}
