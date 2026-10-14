package orangehrmpoc.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import orangehrmpoc.config.ConfigReader;

public final class VideoConverter {
    private VideoConverter() {
    }

    public static Path toBrowserCompatibleMp4(Path source) throws IOException, InterruptedException {
        String fileName = source.getFileName().toString();
        int extensionIndex = fileName.lastIndexOf('.');
        String baseName = extensionIndex < 0 ? fileName : fileName.substring(0, extensionIndex);
        Path destination = source.resolveSibling(baseName + ConfigReader.get("video.convertedExtension"));

        List<String> command = new ArrayList<>();
        command.add(ConfigReader.get("video.ffmpeg.executable"));
        command.addAll(Arrays.asList(ConfigReader.get("video.ffmpeg.inputArguments").split(",")));
        command.add(source.toString());
        command.addAll(Arrays.asList(ConfigReader.get("video.ffmpeg.outputArguments").split(",")));
        command.add(destination.toString());

        Process process = new ProcessBuilder(command)
                .redirectErrorStream(true)
                .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                .start();

        if (!process.waitFor(Long.parseLong(ConfigReader.get("video.ffmpeg.timeoutSeconds")), TimeUnit.SECONDS)) {
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