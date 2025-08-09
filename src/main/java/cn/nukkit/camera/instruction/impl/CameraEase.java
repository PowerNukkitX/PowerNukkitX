package cn.nukkit.camera.instruction.impl;

import java.util.HashMap;
import java.util.Map;

public enum CameraEase {
    LINEAR("linear"),
    SPRING("spring"),
    EASE_IN_SINE("in_sine"),
    EASE_OUT_SINE("out_sine"),
    EASE_IN_OUT_SINE("in_out_sine"),
    EASE_IN_QUAD("in_quad"),
    EASE_OUT_QUAD("out_quad"),
    EASE_IN_OUT_QUAD("in_out_quad"),
    EASE_IN_CUBIC("in_cubic"),
    EASE_OUT_CUBIC("out_cubic"),
    EASE_IN_OUT_CUBIC("in_out_cubic"),
    EASE_IN_QUART("in_quart"),
    EASE_OUT_QUART("out_quart"),
    EASE_IN_OUT_QUART("in_out_quart"),
    EASE_IN_QUINT("in_quint"),
    EASE_OUT_QUINT("out_quint"),
    EASE_IN_OUT_QUINT("in_out_quint"),
    EASE_IN_EXPO("in_expo"),
    EASE_OUT_EXPO("out_expo"),
    EASE_IN_OUT_EXPO("in_out_expo"),
    EASE_IN_CIRC("in_circ"),
    EASE_OUT_CIRC("out_circ"),
    EASE_IN_OUT_CIRC("in_out_circ"),
    EASE_IN_BACK("in_back"),
    EASE_OUT_BACK("out_back"),
    EASE_IN_OUT_BACK("in_out_back"),
    EASE_IN_ELASTIC("in_elastic"),
    EASE_OUT_ELASTIC("out_elastic"),
    EASE_IN_OUT_ELASTIC("in_out_elastic"),
    EASE_IN_BOUNCE("in_bounce"),
    EASE_OUT_BOUNCE("out_bounce"),
    EASE_IN_OUT_BOUNCE("in_out_bounce");

    private static final Map<String, CameraEase> serializeNames = new HashMap<>();
    static {
        for (CameraEase value : values()) {
            serializeNames.put(value.getSerializeName(), value);
        }
    }

    private final String serializeName;

    CameraEase(String serializeName) {
        this.serializeName = serializeName;
    }

    public String getSerializeName() {
        return this.serializeName;
    }

    public static CameraEase fromName(String serializeName) {
        return serializeNames.get(serializeName);
    }
}
