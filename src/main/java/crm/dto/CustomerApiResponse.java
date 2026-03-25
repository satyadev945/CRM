package crm.dto;

/**
 * Response DTO for Customer API endpoints
 * 
 * This class represents the JSON response structure for customer-related API calls.
 */
public class CustomerApiResponse {

    private String message;
    private String timestamp;

    /**
     * Default constructor
     */
    public CustomerApiResponse() {
    }

    /**
     * Parameterized constructor
     * 
     * @param message   the response message
     * @param timestamp the timestamp of the response
     */
    public CustomerApiResponse(String message, String timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    /**
     * Gets the response message
     * 
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the response message
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
