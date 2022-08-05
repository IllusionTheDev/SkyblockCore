package me.illusion.skyblockcore.shared.serialization;

import java.io.Serializable;
import java.util.Map;

public interface SkyblockSerializable extends Serializable {

    void load(Map<String, Object> map);

    void save(Map<String, Object> map);
}
