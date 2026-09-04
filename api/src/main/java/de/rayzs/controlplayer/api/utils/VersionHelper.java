package de.rayzs.controlplayer.api.utils;

public class  VersionHelper {

    private static int major, minor, release;
    private static String versionPackage;

    private static Software software;
    private static boolean legacy;

    public static void initialize(final String versionPackage) {

        if (doesClassExist("org.bukkit.Server")) {
            try {

                software = doesClassExist("io.papermc.paper.threadedregions.RegionizedServer")
                        ? Software.FOLIA : doesClassExist("com.destroystokyo.paper.Metrics")
                        ? Software.PAPER : doesClassExist("org.spigotmc.SpigotConfig")
                        ? Software.SPIGOT : Software.BUKKIT;


                VersionHelper.versionPackage = versionPackage.split("-")[0].replace(".", "_");
                loadAges();

                legacy = isAtMost(1, 16);

            } catch (Exception exception) {
                exception.printStackTrace();
            }

            return;
        }

        software = doesClassExist("com.velocitypowered.api.proxy.ProxyServer")
                ? Software.VELOCITY : doesClassExist("io.github.waterfallmc.waterfall.QueryResult")
                ? Software.WATERFALL : Software.BUNGEECORD;
    }

    public static String getVersionPackage() {
        return versionPackage;
    }

    public static boolean isModern() { return !legacy; }
    public static boolean isLegacy() { return legacy; }

    public static Software getSoftware() {
        return software;
    }

    public static boolean isFoliaServer() {
        return software == Software.FOLIA;
    }

    public static boolean isCraftbukkit() {
        return software == Software.BUKKIT;
    }

    public static boolean isProxyServer() {
        return software.isProxySoftware();
    }
    public static boolean isVelocityServer() {
        return software == Software.VELOCITY;
    }
    public static boolean isPaper() {
        return software == Software.PAPER;
    }

    private static boolean doesClassExist(String className) {
        try {
            Class.forName(className);
            return true;
        }catch (ClassNotFoundException classNotFoundException) {
            return false;
        }
    }

    private static void loadAges() {
        final String[] versionArgs = versionPackage.split("_");

        major = Integer.parseInt(versionArgs[0]);
        minor = Integer.parseInt(versionArgs[1]);

        if (versionArgs.length >= 3) {

            try {
                release = Integer.parseInt(versionArgs[2]);
                return;
            } catch (NumberFormatException ignored) {}

        }

        release = 0;
    }

    public static int getMajor() { return major; }
    public static int getMinor() { return minor; }
    public static int getRelease() { return release; }

    public static boolean isAtLeast(int major, int minor) {
        return VersionHelper.getMajor() > major || (VersionHelper.getMajor() == major && VersionHelper.getMinor() >= minor);
    }

    public static boolean isAtLeast(int major, int minor, int release) {
        return VersionHelper.getMajor() > major
                || (VersionHelper.getMajor() == major && VersionHelper.getMinor() > minor)
                || (VersionHelper.getMajor() == major && VersionHelper.getMinor() == minor && VersionHelper.getRelease() >= release);
    }

    public static boolean isAtMost(int major, int minor) {
        return VersionHelper.getMajor() < major || (VersionHelper.getMajor() == major && VersionHelper.getMinor() <= minor);
    }

    public static boolean isAtMost(int major, int minor, int release) {
        return VersionHelper.getMajor() < major
                || (VersionHelper.getMajor() == major && VersionHelper.getMinor() < minor)
                || (VersionHelper.getMajor() == major && VersionHelper.getMinor() == minor && VersionHelper.getRelease() <= release);
    }

    public static boolean isAfter(int major, int minor) {
        return VersionHelper.getMajor() > major || (VersionHelper.getMajor() == major && VersionHelper.getMinor() > minor);
    }

    public static boolean isAfter(int major, int minor, int release) {
        return VersionHelper.getMajor() > major
                || (VersionHelper.getMajor() == major && VersionHelper.getMinor() > minor)
                || (VersionHelper.getMajor() == major && VersionHelper.getMinor() == minor && VersionHelper.getRelease() > release);
    }

    public static boolean isBefore(int major, int minor) {
        return VersionHelper.getMajor() < major || (VersionHelper.getMajor() == major && VersionHelper.getMinor() < minor);
    }

    public static boolean isBefore(int major, int minor, int release) {
        return VersionHelper.getMajor() < major
                || (VersionHelper.getMajor() == major && VersionHelper.getMinor() < minor)
                || (VersionHelper.getMajor() == major && VersionHelper.getMinor() == minor && VersionHelper.getRelease() < release);
    }

    public enum Software {
        BUKKIT(false, false),
        SPIGOT(false, false),
        PAPER(true, false),
        FOLIA(true, false),

        BUNGEECORD(false, true),
        WATERFALL(true, true),
        VELOCITY(true, true);

        private final String name;
        private final boolean paperBased, proxySoftware;

        Software(final boolean paperBased, final boolean proxySoftware) {
            this.paperBased = paperBased;
            this.proxySoftware = proxySoftware;
            this.name = Character.toUpperCase(this.name().charAt(0)) + this.name().substring(1).toLowerCase();
        }

        @Override
        public String toString() {
            return name;
        }

        public boolean isPaperBased() {
            return paperBased;
        }

        public boolean isProxySoftware() {
            return proxySoftware;
        }
    }

    public enum SearchOption { CONTAINS, EQUALS, ENDS, STARTS }
}