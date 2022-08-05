package me.illusion.skyblockcore.shared.sql.serializing;

import java.io.Serializable;
import java.util.function.Predicate;

public enum SqlColumnType {
    INT(object -> object instanceof Integer),
    LONG(object -> object instanceof Long),
    DOUBLE(object -> object instanceof Double),
    FLOAT(object -> object instanceof Float),
    BOOLEAN(object -> object instanceof Boolean),
    BLOB(object -> object instanceof Serializable),
    TEXT(object -> object instanceof CharSequence);

    private final Predicate<Object> shouldAccept;

    SqlColumnType(Predicate<Object> shouldAccept) {
        this.shouldAccept = shouldAccept;
    }

    public static SqlColumnType getType(Object object) {
        for (SqlColumnType type : values()) {
            if (type.shouldAccept(object)) {
                return type;
            }
        }

        return null;
    }

    public boolean shouldAccept(Object object) {
        return shouldAccept.test(object);
    }
}
