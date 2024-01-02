package me.illusion.skyblockcore.server.item.stack.meta.impl.data;

import java.util.function.Function;
import me.illusion.skyblockcore.server.item.stack.meta.impl.ItemMetaView;

public interface ItemMetaViewProvider extends Function<ItemMetaDataContainer, ItemMetaView> {

}
