package me.illusion.skyblockcore.spigot.utilities;

import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Small adaptation from InventiveTalent's code.
public class MinecraftVersion {

    public static final Pattern NUMERIC_VERSION_PATTERN = Pattern.compile("v([0-9])_([0-9]*)_R([0-9])");
    public static final MinecraftVersion VERSION;

    static {
        try {
            VERSION = MinecraftVersion.getVersion();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get version", e);
        }
    }

    private final String packageName;
    private final int version;
    private final String nmsPackage;
    private final String obcPackage;
    private final boolean nmsVersionPrefix;

    MinecraftVersion(String packageName, int version, String nmsFormat, String obcFormat, boolean nmsVersionPrefix) {
        this.packageName = packageName;
        this.version = version;
        this.nmsPackage = String.format(nmsFormat, packageName);
        this.obcPackage = String.format(obcFormat, packageName);
        this.nmsVersionPrefix = nmsVersionPrefix;
    }

    MinecraftVersion(String packageName, int version) {
        this(packageName, version, "net.minecraft.server.%s", "org.bukkit.craftbukkit.%s", true);
    }

    // Used by SantiyCheck
    MinecraftVersion(Version version) {
        this(version.name(), version.version());
    }

    public static MinecraftVersion getVersion() {
        Class serverClass;
        try {
            serverClass = Bukkit.getServer().getClass();
        } catch (Exception e) {
            System.err.println("[ReflectionHelper/MinecraftVersion] Failed to get bukkit server class: " + e.getMessage());
            System.err.println("[ReflectionHelper/MinecraftVersion] Assuming we're in a test environment!");
            return null;
        }
        String name = serverClass.getPackage().getName();
        String versionPackage = name.substring(name.lastIndexOf('.') + 1);
        for (Version version : Version.values()) {
            MinecraftVersion minecraftVersion = version.minecraft();
            if (minecraftVersion.matchesPackageName(versionPackage)) {
                return minecraftVersion;
            }
        }
        System.err.println("[ReflectionHelper/MinecraftVersion] Failed to find version enum for '" + name + "'/'" + versionPackage + "'");

        System.out.println("[ReflectionHelper/MinecraftVersion] Generating dynamic constant...");
        Matcher matcher = NUMERIC_VERSION_PATTERN.matcher(versionPackage);
        while (matcher.find()) {
            if (matcher.groupCount() < 3) {
                continue;
            }

            String majorString = matcher.group(1);
            String minorString = matcher.group(2);
            if (minorString.length() == 1) {
                minorString = "0" + minorString;
            }
            String patchString = matcher.group(3);
            if (patchString.length() == 1) {
                patchString = "0" + patchString;
            }

            String numVersionString = majorString + minorString + patchString;
            int numVersion = Integer.parseInt(numVersionString);
            String packageName = "v" + versionPackage.substring(1).toUpperCase();

            boolean postOneSeventeen = numVersion > 11701;

            //dynamic register version
            System.out.println("[ReflectionHelper/MinecraftVersion] Injected dynamic version " + packageName + " (#" + numVersion + ").");
            System.out.println("[ReflectionHelper/MinecraftVersion] Please inform inventivetalent about the outdated version, as this is not guaranteed to work.");
            if (postOneSeventeen) { // new nms package format for 1.17+
                return new MinecraftVersion(packageName, numVersion, "net.minecraft", "org.bukkit.craftbukkit.%s", false);
            }
            return new MinecraftVersion(packageName, numVersion);
        }

        System.err.println("[ReflectionHelper/MinecraftVersion] Failed to create dynamic version for " + versionPackage);

        return new MinecraftVersion("UNKNOWN", -1);
    }

    /**
     * @return the version-number
     */
    public int version() {
        return version;
    }

    /**
     * @deprecated use {@link #getNmsPackage()} / {@link #getObcPackage()} instead
     */
    @Deprecated
    public String packageName() {
        return packageName;
    }

    /**
     * @return the full package name for net.minecraft....
     */
    public String getNmsPackage() {
        return nmsPackage;
    }

    /**
     * @return the full package name for org.bukkit....
     */
    public String getObcPackage() {
        return obcPackage;
    }

    /**
     * @return if the nms package name has version prefix
     */
    public boolean hasNMSVersionPrefix() {
        return nmsVersionPrefix;
    }

    /**
     * @param version the version to check
     * @return <code>true</code> if this version is older than the specified version
     */
    public boolean olderThan(Version version) {
        return version() < version.version();
    }

    /**
     * @param version the version to check
     * @return <code>true</code> if this version is equals than the specified version
     */
    public boolean equal(Version version) {
        return version() == version.version();
    }

    /**
     * @param version the version to check
     * @return <code>true</code> if this version is newer than the specified version
     */
    public boolean newerThan(Version version) {
        return version() >= version.version();
    }

    /**
     * @param oldVersion The older version to check
     * @param newVersion The newer version to check
     * @return <code>true</code> if this version is newer than the oldVersion and older that the newVersion
     */
    public boolean inRange(Version oldVersion, Version newVersion) {
        return newerThan(oldVersion) && olderThan(newVersion);
    }

    public boolean matchesPackageName(String packageName) {
        return this.packageName.toLowerCase().contains(packageName.toLowerCase());
    }

    @Override
    public String toString() {
        return packageName + " (" + version() + ")";
    }

    public enum Version {
        UNKNOWN(-1) {
            @Override
            public boolean matchesPackageName(String packageName) {
                return false;
            }
        },

        v1_7_R1(10701),
        v1_7_R2(10702),
        v1_7_R3(10703),
        v1_7_R4(10704),

        v1_8_R1(10801),
        v1_8_R2(10802),
        v1_8_R3(10803),
        //Does this even exists?
        v1_8_R4(10804),

        v1_9_R1(10901),
        v1_9_R2(10902),

        v1_10_R1(11001),

        v1_11_R1(11101),

        v1_12_R1(11201),

        v1_13_R1(11301),
        v1_13_R2(11302),

        v1_14_R1(11401),

        v1_15_R1(11501),

        v1_16_R1(11601),
        v1_16_R2(11602),
        v1_16_R3(11603),

        v1_17_R1(11701),

        v1_18_R1(11801),
        v1_18_R2(11802),

        /// (Potentially) Upcoming versions
        v1_19_R1(11901),

        v1_20_R1(12001);

        private final MinecraftVersion version;

        Version(int version, String nmsFormat, String obcFormat, boolean nmsVersionPrefix) {
            this.version = new MinecraftVersion(name(), version, nmsFormat, obcFormat, nmsVersionPrefix);
        }

        Version(int version) {
            if (version >= 11701) { // 1.17+ new class package name format
                this.version = new MinecraftVersion(name(), version, "net.minecraft", "org.bukkit.craftbukkit.%s", false);
            } else {
                this.version = new MinecraftVersion(name(), version);
            }
        }

        @Deprecated
        public static Version getVersion() {
            String name = Bukkit.getServer().getClass().getPackage().getName();
            String versionPackage = name.substring(name.lastIndexOf('.') + 1);
            for (Version version : values()) {
                if (version.matchesPackageName(versionPackage)) {
                    return version;
                }
            }

            return UNKNOWN;
        }

        /**
         * @return the version-number
         */
        public int version() {
            return version.version();
        }

        /**
         * @param version the version to check
         * @return <code>true</code> if this version is older than the specified version
         */
        @Deprecated
        public boolean olderThan(Version version) {
            return version() < version.version();
        }

        /**
         * @param version the version to check
         * @return <code>true</code> if this version is newer than the specified version
         */
        @Deprecated
        public boolean newerThan(Version version) {
            return version() >= version.version();
        }

        /**
         * @param oldVersion The older version to check
         * @param newVersion The newer version to check
         * @return <code>true</code> if this version is newer than the oldVersion and older that the newVersion
         */
        @Deprecated
        public boolean inRange(Version oldVersion, Version newVersion) {
            return newerThan(oldVersion) && olderThan(newVersion);
        }

        public boolean matchesPackageName(String packageName) {
            return packageName.toLowerCase().contains(name().toLowerCase());
        }

        /**
         * @return the minecraft version
         */
        public MinecraftVersion minecraft() {
            return version;
        }

        @Override
        public String toString() {
            return name() + " (" + version() + ")";
        }
    }
}