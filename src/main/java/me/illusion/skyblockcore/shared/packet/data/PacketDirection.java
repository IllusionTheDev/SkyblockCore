package me.illusion.skyblockcore.shared.packet.data;

public enum PacketDirection {
    PROXY_TO_INSTANCE(0x00),
    INSTANCE_TO_PROXY(0x01),
    PROXY_TO_PROXY(0x02);

    public static final PacketDirection[] VALUES = values();

    private final byte index;

    PacketDirection(int index) {
        this.index = (byte) index;
    }

    public static PacketDirection fromIndex(byte index) {
        for (PacketDirection direction : VALUES)
            if (direction.index == index)
                return direction;

        throw new UnsupportedOperationException("Unexpected Packet Direction " + index);
    }

    public byte getIndex() {
        return index;
    }
}
