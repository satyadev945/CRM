package crm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * BookResponse DTO
 * Response object for book-related API endpoints
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {

    private String message;
    private String timestamp;

    /**
     * Create a success response with current timestamp
     *
     * @param message success message
     * @return BookResponse object
     */
    public static BookResponse success(String message) {
        return BookResponse.builder()
                .message(message)
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                .build();
    }

    /**
     * Create an error response with current timestamp
     *
     * @param message error message
     * @return BookResponse object
     */
    public static BookResponse error(String message) {
        return BookResponse.builder()
                .message(message)
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                .build();
    }

}
