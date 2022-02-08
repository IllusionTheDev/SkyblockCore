package me.illusion.skyblockcore.spigot.sql.serialized;

import me.illusion.skyblockcore.shared.environment.Core;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

public class SerializedItemStackArray implements Serializable {

    private String base64;

    /**
     * Serializes an ItemStack[] into base64
     *
     * @param items - The ItemStack[] to serialize
     * @return the base64 encoded String
     */
    private static String itemStackArrayToBase64(ItemStack... items) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Write the size of the inventory
            dataOutput.writeInt(items.length);

            // Save every element in the list
            for (ItemStack item : items) {
                dataOutput.writeObject(item);
            }

            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    /**
     * Deserializes a base64 encoded String into an ItemStack
     *
     * @param data - The base64 encoded String
     * @return deserialized ItemStack[]
     * @throws IOException if unable to deserialize (invalid class)
     */
    private static ItemStack[] itemStackArrayFromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];

            // Read the serialized inventory
            for (int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

            dataInput.close();
            return items;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }

    /**
     * Updates the array internally
     *
     * @param items - The new array
     */
    public void updateArray(ItemStack... items) {
        base64 = itemStackArrayToBase64(items);
        Core.info("Updated internal base64 to " + base64);
    }

    /**
     * Obtains the array
     *
     * @return the array
     */
    public ItemStack[] getArray() {
        try {
            return itemStackArrayFromBase64(base64);
        } catch (IOException ignored) {
            return new ItemStack[]{};
        }
    }
}
