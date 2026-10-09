package orangehrmpoc.utils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
public class VideoFileManager {
    public static void keepLatestVideo() {
        Path folder = Path.of("target", "videos");
        Path finalVideo = folder.resolve("runScenario_recording.avi");

        try {
            if (!Files.exists(folder)) {
                System.out.println("Video folder not found.");
                return;
            }

            List<Path> videos;

            try (Stream<Path> files = Files.list(folder)) {
                videos = files
                        .filter(Files::isRegularFile)
                        .filter(path -> path.getFileName().toString()
                                .startsWith("runScenario_recording_"))
                        .filter(path -> path.getFileName().toString()
                                .endsWith(".avi"))
                        .sorted(Comparator.comparingLong(
                                path -> path.toFile().lastModified()))
                        .toList();
            }

            if (videos.isEmpty()) {
                System.out.println("No new recording found.");
                return;
            }

            Path latestVideo = videos.get(videos.size() - 1);

            Files.move(
                    latestVideo,
                    finalVideo,
                    StandardCopyOption.REPLACE_EXISTING
            );

            for (Path video : videos) {
                if (!video.equals(latestVideo)) {
                    Files.deleteIfExists(video);
                }
            }

            System.out.println("Latest video saved as: " + finalVideo);

        } catch (IOException e) {
            throw new RuntimeException("Unable to manage video files", e);
        }
    }
    
}
