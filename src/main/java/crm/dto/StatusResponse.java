package crm.dto;

/**
 * Data Transfer Object for the /getStatus endpoint response.
 * Contains status message and timestamp information.
 */
public class StatusResponse {

    private String message;
    private String timestamp;

    /**
     * Default no-argument constructor.
     */
    public StatusResponse() {
    }

    /**
     * All-arguments constructor.
     * 
     * @param message   the status message
     * @param timestamp the timestamp of the status check
     */
    public StatusResponse(String message, String timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    /**
     * Gets the status message.
     * 
     * @return the status message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the status message.
     * 
     * @param message the status message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the timestamp.
     * 
     * @return the timestamp
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp.
     * 
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
