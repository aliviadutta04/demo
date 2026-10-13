package orangehrmpoc.config;

public class MissingConfigurationException extends IllegalStateException {
    public MissingConfigurationException(String message) {
        super(message);
    }
}