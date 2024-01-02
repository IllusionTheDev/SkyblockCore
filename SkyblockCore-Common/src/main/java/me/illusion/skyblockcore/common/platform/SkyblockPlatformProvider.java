package me.illusion.skyblockcore.common.platform;

public final class SkyblockPlatformProvider {

    private static SkyblockPlatform platform;

    private SkyblockPlatformProvider() {
    }

    public static SkyblockPlatform getPlatform() {
        return platform;
    }

    public static void setPlatform(SkyblockPlatform platform) {
        if (SkyblockPlatformProvider.platform != null) {
            throw new IllegalStateException("Platform already set!");
        }

        SkyblockPlatformProvider.platform = platform;
    }

}
