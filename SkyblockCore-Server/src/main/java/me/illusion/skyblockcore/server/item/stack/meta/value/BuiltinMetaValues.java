package me.illusion.skyblockcore.server.item.stack.meta.value;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import me.illusion.skyblockcore.server.item.stack.meta.data.ItemFlag;

public final class BuiltinMetaValues {

    public static final MetaValue<String> DISPLAY_NAME = MetaValue.create();
    public static final MetaValue<String> LOCALIZED_NAME = MetaValue.create();
    public static final MetaValue<List<String>> LORE = MetaValue.create(ArrayList::new);
    public static final MetaValue<Boolean> UNBREAKABLE = MetaValue.create(false);
    public static final MetaValue<Set<ItemFlag>> ITEM_FLAGS = MetaValue.create(HashSet::new);
    public static final MetaValue<Integer> MODEL_DATA = MetaValue.create();
    public static final MetaValue<Color> LEATHER_COLOR = MetaValue.create();

    private BuiltinMetaValues() {
    }

}
