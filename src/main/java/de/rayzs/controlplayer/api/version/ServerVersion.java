package de.rayzs.controlplayer.api.version;

import org.bukkit.Server;

public class ServerVersion {

    private boolean legacy;
    private String rawVersionName;
    private Version version = Version.MODERN;
    int major, minor, release;

    public ServerVersion(Server server) {
        rawVersionName = server.getClass().getPackage().getName();
        rawVersionName = rawVersionName.substring(rawVersionName.lastIndexOf('.') + 1);
        version = getVersionEnum(rawVersionName);
        legacy = version != Version.MODERN && getAges(version)[1] <= 16;
    }

    public String getRawVersionName() { return rawVersionName; }
    public boolean isModern() { return !legacy; }
    public boolean isLegacy() { return legacy; }
    public boolean isPrimaryVersion(int primaryVersion) { return getAges(version)[1] == primaryVersion; }

    public Version getVersionEnum(String versionName) {
        versionName = versionName.replace("v", "");
        String primaryVersionName, fullVersionName = null;
        final int[] age = getAges(versionName);
        final int major = age[0], minor = age[1], release = age[2];
        StringBuilder builder = new StringBuilder("v_");
        builder.append(major).append("_").append(minor);
        primaryVersionName = builder.toString();
        if (release != -1) {
            if (builder.toString().contains("1_8"))
                builder.append(release == 3 ? "_9" : "");
            else builder.append("_").append(release);
            fullVersionName = builder.toString();
        }

        Version versionResult;
        try {
            versionResult = Version.valueOf(fullVersionName);
        } catch (IllegalArgumentException ignore) {
            try { versionResult = Version.valueOf(primaryVersionName);
            } catch (IllegalArgumentException exception) { versionResult = Version.MODERN; }
        }
        return versionResult;
    }

    public int[] getAges(String versionName) {
        String[] versionArgs = versionName.replace("v", "").replace("R", "").split("_");
        try {
            major = Integer.parseInt(versionArgs[0]);
            minor = Integer.parseInt(versionArgs[1]);
            release = versionArgs.length > 2 ? Integer.parseInt(versionArgs[2].replace("R", "")) : -1;
        }catch (NumberFormatException ignored) { }
        return new int[]{major, minor, release};
    }

    public int[] getAges(Version version) {
        String versionName = version.toString();
        String[] versionArgs = versionName.replace("v_", "").split("_");
        int major = 0, minor = 0, release = 0;
        try {
            major = Integer.parseInt(versionArgs[0]);
            minor = Integer.parseInt(versionArgs[1]);
            release = versionArgs.length > 2 ? Integer.parseInt(versionArgs[2].replace("R", "")) : -1;
        }catch (NumberFormatException ignored) { }
        return new int[]{major, minor, release};
    }

    public enum Version {
        v_1_8,
        v_1_8_9,
        v_1_9,
        v_1_9_4,
        v_1_10,
        v_1_10_2,
        v_1_11,
        v_1_11_2,
        v_1_12,
        v_1_12_2,
        v_1_13,
        v_1_13_2,
        v_1_14,
        v_1_14_4,
        v_1_15,
        v_1_15_2,
        v_1_16,
        v_1_16_4,
        v_1_16_5,
        MODERN;
        // minecraft server protocols
        // https://minecraft.fandom.com/wiki/Protocol_version
        public static int getProtocol(Version version) {
            switch (version) {
                case v_1_8:
                case v_1_8_9:
                    return 47;
                case v_1_9:
                    return 107;
                case v_1_9_4:
                    return 110;
                case v_1_10:
                case v_1_10_2:
                    return 210;
                case v_1_11:
                    return 315;
                case v_1_11_2:
                    return 316;
                case v_1_12:
                    return 335;
                case v_1_12_2:
                    return 340;
                case v_1_13:
                    return 393;
                case v_1_13_2:
                    return 404;
                case v_1_14:
                    return 477;
                case v_1_14_4:
                    return 498;
                case v_1_15:
                    return 573;
                case v_1_15_2:
                    return 578;
                case v_1_16:
                    return 735;
                case v_1_16_4:
                case v_1_16_5:
                    return 754;
                default: case MODERN:
                    return 755;
            }
        }
    }
}
