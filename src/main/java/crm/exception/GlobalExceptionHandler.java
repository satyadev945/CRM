package crm.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Global exception handler for proper error handling in cloud environments.
 * Provides structured error responses and logging for all application exceptions.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handle generic exceptions for REST endpoints
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        log.error("Unhandled exception occurred: {}", ex.getMessage(), ex);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", "An unexpected error occurred");
        body.put("error", ex.getClass().getSimpleName());
        body.put("path", request.getDescription(false));

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle exceptions for web page views
     */
    @ExceptionHandler(value = {Exception.class})
    public ModelAndView handleViewException(Exception ex, HttpServletRequest request) {
        log.error("Exception occurred while processing request: {} - {}",
                  request.getRequestURI(), ex.getMessage(), ex);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("timestamp", LocalDateTime.now());
        modelAndView.addObject("errorMessage", "An unexpected error occurred");
        modelAndView.addObject("path", request.getRequestURI());
        modelAndView.addObject("exception", ex.getClass().getSimpleName());
        modelAndView.setViewName("error/generic");
        return modelAndView;
    }

    /**
     * Handle storage-related exceptions
     */
    @ExceptionHandler(StorageException.class)
    public ResponseEntity<Object> handleStorageException(StorageException ex, WebRequest request) {
        log.error("Storage exception: {}", ex.getMessage(), ex);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("error", "Storage Error");
        body.put("path", request.getDescription(false));

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}