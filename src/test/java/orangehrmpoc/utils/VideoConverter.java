package orangehrmpoc.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public final class VideoConverter {
    private VideoConverter() {
    }

    public static Path toBrowserCompatibleMp4(Path source) throws IOException, InterruptedException {
        String fileName = source.getFileName().toString();
        int extensionIndex = fileName.lastIndexOf('.');
        String baseName = extensionIndex < 0 ? fileName : fileName.substring(0, extensionIndex);
        Path destination = source.resolveSibling(baseName + ".mp4");

        Process process = new ProcessBuilder(
                "ffmpeg", "-y", "-i", source.toString(), "-an",
                "-c:v", "libx264", "-preset", "ultrafast", "-crf", "28",
                "-pix_fmt", "yuv420p", "-movflags", "+faststart", destination.toString())
                .redirectErrorStream(true)
                .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                .start();

        if (!process.waitFor(120, TimeUnit.SECONDS)) {
            process.destroyForcibly();
            throw new IOException("FFmpeg timed out converting recording: " + source.getFileName());
        }
        if (process.exitValue() != 0 || !Files.isRegularFile(destination)
                || Files.size(destination) == 0) {
            Files.deleteIfExists(destination);
            throw new IOException("FFmpeg failed to convert recording: " + source.getFileName());
        }
        return destination;
    }
}