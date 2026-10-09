
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

 
        Path sourceFolder = Path.of("video");


        Path targetFolder = Path.of("target", "videos");

        Path finalVideo = targetFolder.resolve("runScenario_recording.avi");

        try {
            if (!Files.exists(sourceFolder)) {
                System.out.println("Source video folder not found: " + sourceFolder);
                return;
            }

            List<Path> videos;

            try (Stream<Path> files = Files.list(sourceFolder)) {
                videos = files
                        .filter(Files::isRegularFile)
                        .filter(path -> {
                            String name = path.getFileName().toString();

                            return name.startsWith("runScenario_recording_")
                                    && name.endsWith(".avi");
                        })
                        .sorted(Comparator.comparingLong(
                                path -> path.toFile().lastModified()))
                        .toList();
            }

            if (videos.isEmpty()) {
                System.out.println("No recording found in video folder.");
                return;
            }

      
            Path latestVideo = videos.get(videos.size() - 1);

            Files.createDirectories(targetFolder);

           
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

            System.out.println("Video saved successfully: " + finalVideo);

        } catch (IOException e) {
            throw new RuntimeException("Unable to manage video files", e);
        }
    }
}
