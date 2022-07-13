package me.illusion.skyblockcore.shared.sql.serialized;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
public class SerializedLocation implements Serializable {

    @Setter
    private String format;

    @Override
    public String toString() {
        return format;
    }

    public String getWorldName() {
        return format.split(" ")[5];
    }
}
