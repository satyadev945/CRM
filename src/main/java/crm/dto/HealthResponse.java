package crm.dto;

/**
 * DTO class for health check response
 * 
 * This class represents the JSON response structure for the health check endpoint.
 * It contains a message indicating the health status and a timestamp of when the check was performed.
 */
public class HealthResponse {

    private String message;
    private String timestamp;

    /**
     * Default constructor
     */
    public HealthResponse() {
    }

    /**
     * Parameterized constructor
     * 
     * @param message   the health status message
     * @param timestamp the timestamp when the health check was performed
     */
    public HealthResponse(String message, String timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    /**
     * Gets the health status message
     * 
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the health status message
     * 
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the timestamp
     * 
     * @return the timestamp
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp
     * 
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
