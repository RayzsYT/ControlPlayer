package de.rayzs.controlplayer.api.utils;

import de.rayzs.controlplayer.api.ControlPlayer;
import de.rayzs.controlplayer.api.player.CPSender;

import java.util.function.Consumer;

public class VersionComparer {

    public enum VersionState { NEWER, UPDATED, OUTDATED }

    private static final VersionComparer instance = new VersionComparer();

    public static VersionComparer get() {
        return instance;
    }

    private VersionComparer() {}

    private final String versionUrl = "https://www.rayzs.de/products/controlplayer/version/version.txt";
    private final int versionLength = 3;

    private Version currentVersion, newestVersion;
    private VersionState versionState = VersionState.UPDATED;

    private boolean shouldAnnounce = true;


    public boolean computeComparison(
            final CPSender sender,
            final Consumer<CPSender> updatedConsumer,
            final Consumer<CPSender> outdatedConsumer
    ) {
        String result = new ConnectionBuilder().setUrl(versionUrl)
                .setProperties("ControlPlayer", "4454")
                .connect()
                .getResponse();

        if (result == null)
            result = "/";

        if (result.equals("/")) {
            ControlPlayer.get().warning("Failed to connect to plugin page! Version comparison cannot be made. (No internet?)");
            return false;
        }


        VersionComparer.get().setNewestVersion(result);

        if (VersionComparer.get().isNewer()) {
            if (shouldAnnounce) {
                shouldAnnounce = false;

                ControlPlayer.get().info("Please be aware that you are currently using a developer version of ControlPlayer. Bugs, errors and a lot of debug messages might be included.");
            }

            return true;
        }

        if (VersionComparer.get().isUpdated()) {
            if (shouldAnnounce) {
                shouldAnnounce = false;

                updatedConsumer.accept(sender);
            }

            return false;
        }

        if (VersionComparer.get().isOutdated()) {

            if (shouldAnnounce) {
                shouldAnnounce = false;

                outdatedConsumer.accept(sender);
            }

            return true;
        }

        return false;
    }

    public void setCurrentVersion(String version) {
        if (version == null)
            return;

        currentVersion = new Version(version);
    }

    public void setNewestVersion(String version) {
        if (version == null)
            return;

        newestVersion = new Version(version);
        compute();
    }

    public VersionState getVersionState() {
        return versionState;
    }

    public boolean isUpdated() {
        return versionState == VersionState.UPDATED;
    }

    public boolean isOutdated() {
        return versionState == VersionState.OUTDATED;
    }

    public boolean isNewer() {
        return versionState == VersionState.NEWER;
    }

    private void compute() {
        if (currentVersion == null || newestVersion == null)
            return;

        final int[] crntNum = currentVersion.getVersionNums();
        final int[] newNum = newestVersion.getVersionNums();

        for (int i = 0; i < versionLength; i++) {

            if (crntNum[i] > newNum[i]) {
                versionState = VersionState.NEWER;
                return;
            }

            if (crntNum[i] < newNum[i]) {
                versionState = VersionState.OUTDATED;
                return;
            }

        }
    }

    private class Version {

        private final int[] versionNums = new int[versionLength];
        private final String versionString;


        public Version(String versionString) {
            this.versionString = versionString;
            setVersionNums();
        }

        public String getVersionName() {
            return versionString;
        }

        public int[] getVersionNums() {
            return versionNums;
        }

        private void setVersionNums() {

            if (!versionString.contains("."))
                return;

            String[] details = versionString.split("\\.");
            for (int i = 0; i < details.length; i++) {
                if (i >= versionLength)
                    break;

                String detailString = details[i];

                if (!NumberUtils.isDigit(detailString))
                    continue;

                int detail = Integer.parseInt(detailString);
                versionNums[i] = detail;
            }
        }
    }
}