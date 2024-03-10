package me.illusion.skyblockcore.spigot.inventory.container;

import me.illusion.skyblockcore.server.inventory.ContainerMetadata;
import me.illusion.skyblockcore.server.inventory.PlayerItemContainer;
import me.illusion.skyblockcore.server.inventory.slot.EquipmentSlot;
import me.illusion.skyblockcore.server.item.stack.MinecraftItemStack;
import me.illusion.skyblockcore.spigot.utilities.adapter.SkyblockBukkitAdapter;
import org.bukkit.inventory.PlayerInventory;

public class BukkitPlayerItemContainer implements PlayerItemContainer {

    private final PlayerInventory inventory;
    private final ContainerMetadata metadata = new PlayerContainerMetadata();

    private BukkitPlayerItemContainer(PlayerInventory inventory) {
        this.inventory = inventory;
    }

    public static BukkitPlayerItemContainer wrap(PlayerInventory inventory) {
        return new BukkitPlayerItemContainer(inventory);
    }

    @Override
    public MinecraftItemStack getItem(int slot) {
        return SkyblockBukkitAdapter.adapt(inventory.getItem(slot));
    }

    @Override
    public void setItem(int slot, MinecraftItemStack item) {
        inventory.setItem(slot, SkyblockBukkitAdapter.adapt(item));
    }

    @Override
    public ContainerMetadata getMetadata() {
        return metadata;
    }

    @Override
    public void setItem(EquipmentSlot slot, MinecraftItemStack item) {
        inventory.setItem(adapt(slot), SkyblockBukkitAdapter.adapt(item));
    }

    @Override
    public MinecraftItemStack getItem(EquipmentSlot slot) {
        return SkyblockBukkitAdapter.adapt(inventory.getItem(adapt(slot)));
    }

    @Override
    public int getHeldItemSlot() {
        return inventory.getHeldItemSlot();
    }

    private org.bukkit.inventory.EquipmentSlot adapt(EquipmentSlot platformSlot) {
        return switch (platformSlot) {
            case MAIN_HAND -> org.bukkit.inventory.EquipmentSlot.HAND;
            case OFF_HAND -> org.bukkit.inventory.EquipmentSlot.OFF_HAND;
            case FEET -> org.bukkit.inventory.EquipmentSlot.FEET;
            case LEGS -> org.bukkit.inventory.EquipmentSlot.LEGS;
            case CHEST -> org.bukkit.inventory.EquipmentSlot.CHEST;
            case HEAD -> org.bukkit.inventory.EquipmentSlot.HEAD;
        };
    }

    private class PlayerContainerMetadata extends ContainerMetadata {

        protected PlayerContainerMetadata() {
            super(9, 4);
        }

        @Override
        public int getSize() {
            return inventory.getSize();
        }
    }
}
