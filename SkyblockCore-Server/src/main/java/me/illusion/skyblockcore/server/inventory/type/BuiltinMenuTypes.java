package me.illusion.skyblockcore.server.inventory.type;

public final class BuiltinMenuTypes {

    public static final MenuType GENERIC_9X1 = createGeneric(9, 1);
    public static final MenuType GENERIC_9X2 = createGeneric(9, 2);
    public static final MenuType GENERIC_9X3 = createGeneric(9, 3);
    public static final MenuType GENERIC_9X4 = createGeneric(9, 4);
    public static final MenuType GENERIC_9X5 = createGeneric(9, 5);
    public static final MenuType GENERIC_9X6 = createGeneric(9, 6);
    private BuiltinMenuTypes() {

    }

    private static MenuType createGeneric(int width, int height) {
        return SimpleMenuType.create(width, height);
    }
}
