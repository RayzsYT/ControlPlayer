package de.rayzs.controlplayer.api.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public interface ConfigProvider {

    String DEFAULT_PLUGIN_FOLDER_PATH = "./plugins/ControlPlayer";

    /**
     * Gets an already existing Config instance or creates a new one.
     *
     * @param fileName Name of the config file.
     * @return The existing or newly created Config instance.
     */
    Config getOrCreate(final String fileName);

    /**
     * Gets an already existing Config instance or creates a new one.
     *
     * @param filePath Path to the config file.
     * @param fileName Name of the config file.
     * @return The existing or newly created Config instance.
     */
    Config getOrCreate(final String filePath, final String fileName);

    /**
     * Reads a resource file from inside the jar
     * and exports it to the out file directory.
     *
     * @param clazz Class of the project whose local resource folder should be chosen. NULL uses this class here instead.
     * @param inFilePath Resource file path.
     * @param outFileDirPath Export file directory path. Write {@code null} if file should just be inside default export directoy.
     */
    static File exportResourceFileFile(
            final Class<?> clazz,
            final String inFilePath,
            final String outFileDirPath
    ) {

        final URL url = (clazz != null ? clazz : ConfigProvider.class).getClassLoader().getResource(
                inFilePath
        );

        if (url == null) {
            throw new NullPointerException("Resource not found: " + inFilePath);
        }


        try {

            final URLConnection connection = url.openConnection();
            connection.setUseCaches(false);

            final InputStream inputStream = connection.getInputStream();
            final File outputFile = new File(
                    DEFAULT_PLUGIN_FOLDER_PATH + (outFileDirPath != null ? "/" + outFileDirPath : ""),
                    inFilePath.substring(inFilePath.lastIndexOf("/") + 1)
            );

            if (outputFile.exists()) {
                return outputFile;
            }

            // Create directory if they don't exist yet
            outputFile.getParentFile().mkdirs();

            final OutputStream outputStream = new FileOutputStream(outputFile);
            final byte[] buffer = new byte[1024];

            int length;
            while ((length = inputStream.read(buffer)) > 0)
                outputStream.write(buffer, 0, length);

            outputStream.close();
            inputStream.close();

            return outputFile;

        } catch (Exception exception) {
            throw new NullPointerException("Failed to export resource file: " + inFilePath);
        }
    }

}