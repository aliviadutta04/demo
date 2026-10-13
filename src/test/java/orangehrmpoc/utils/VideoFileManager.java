
package orangehrmpoc.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import java.util.logging.Logger;

import orangehrmpoc.config.ConfigReader;

public class VideoFileManager {
    private static final Logger LOGGER = Logger.getLogger(VideoFileManager.class.getName());

    public static void keepLatestVideo() {
        Path videoFolder = Path.of(ConfigReader.get("video.folder"));
        try {
            Files.createDirectories(videoFolder);
            long recordingCount;
            try (Stream<Path> files = Files.list(videoFolder)) {
                recordingCount = files
                        .filter(Files::isRegularFile)
                        .filter(path -> path.getFileName().toString().endsWith(".avi"))
                        .count();
            }
            if (recordingCount == 0) {
                LOGGER.warning("No video recordings were created in " + videoFolder);
            } else {
                LOGGER.info("Preserved " + recordingCount + " video recording(s) in " + videoFolder);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to inspect video recordings", e);
        }
    }
}
