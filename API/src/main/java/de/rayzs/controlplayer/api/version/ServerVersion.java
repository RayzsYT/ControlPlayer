package de.rayzs.controlplayer.api.version;

import org.bukkit.Bukkit;
import org.bukkit.Server;

import java.util.Arrays;

public class ServerVersion {

    public static ServerVersion INSTANCE;

    private Version version;
    private boolean legacy, folia;
    private String versionName, versionPackageName, rawVersionName;
    private int major, minor, release;

    public ServerVersion(Server server) {
        INSTANCE = this;
        try {
            Class.forName("org.bukkit.Server");
            loadVersionName(server);
            loadAges();
            loadVersionEnum();
            legacy = minor <= 16;

            try {
                Class.forName("com.destroystokyo.paper.proxy.VelocityProxy");
                folia = versionName.toLowerCase().contains("folia");
            } catch (Throwable throwable) {
                folia = false;
            }

        } catch (Throwable ignored) {
            System.err.println("Could not read server version!");
        }

    }

    public static ServerVersion getInstance() {
        return INSTANCE;
    }

    public boolean isFolia() {
        return folia;
    }

    public boolean isModern() {
        return !legacy;
    }

    public boolean isLegacy() {
        return legacy;
    }

    public String getRawVersionName() {
        return rawVersionName;
    }

    private void loadVersionName(Object serverObject) throws Exception {
        versionName = Bukkit.getName();
        rawVersionName = (String) serverObject.getClass().getMethod("getBukkitVersion").invoke(serverObject);
        rawVersionName = rawVersionName.split("-")[0].replace(".", "_");
        versionPackageName = serverObject.getClass().getPackage().getName();
        versionPackageName = versionPackageName.substring(versionPackageName.lastIndexOf('.') + 1);
    }

    private void loadVersionEnum() {
        try {
            final String primaryVersionName, fullVersionName;
            StringBuilder builder = new StringBuilder("v_");
            builder.append(major).append("_").append(minor);
            primaryVersionName = builder.toString();
            if (release != 0) builder.append("_").append(release);
            fullVersionName = builder.toString();
            boolean couldFindOriginalVersion = Arrays.stream(Version.values()).anyMatch(searchingVersion -> searchingVersion.toString().equals(fullVersionName));
            version = Version.valueOf(couldFindOriginalVersion ? fullVersionName : primaryVersionName);
        } catch (Exception exception) {
            version = Version.UNSUPPORTED;
        }
    }

    private void loadAges() {
        String[] versionArgs = rawVersionName.split("_");
        major = Integer.parseInt(versionArgs[0]);
        minor = Integer.parseInt(versionArgs[1]);
        release = versionArgs.length > 2 ? Integer.parseInt(versionArgs[2]) : 0;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getRelease() {
        return release;
    }

    public enum Version {
        UNKNOWN, UNSUPPORTED,
        v_1_8, v_1_8_8,
        v_1_9, v_1_9_4,
        v_1_10, v_1_10_2,
        v_1_11, v_1_11_2,
        v_1_12, v_1_12_2,
        v_1_13, v_1_13_2,
        v_1_14, v_1_14_4,
        v_1_15, v_1_15_2,
        v_1_16, v_1_16_4, v_1_16_5,
        v_1_17, v_1_17_1, v_1_18,
        v_1_18_1, v_1_18_2,
        v_1_19, v_1_19_1, v_1_19_2, v_1_19_3, v_1_19_4,
        v_1_20, v_1_20_1, v_1_20_2, v_1_20_3, v_1_20_4, v_1_20_5, v_1_20_6,
        v_1_21, v_1_21_1, v_1_21_2,
    }
}