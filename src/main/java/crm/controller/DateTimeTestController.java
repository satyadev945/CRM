package crm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Cloud-ready DateTime controller using java.time API standardized on UTC.
 * Eliminates timezone inconsistencies and clock synchronization issues in distributed cloud environments.
 */
@Controller
@RequestMapping("/date")
public class DateTimeTestController {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_INSTANT;

    @GetMapping("/test")
    public String dateTimeTest(Model model) {
        // Use Instant for UTC timestamps - cloud-native best practice
        Instant currentInstant = Instant.now();
        
        // Use ZonedDateTime with explicit UTC timezone for cloud consistency
        ZonedDateTime utcDateTime = ZonedDateTime.now(ZoneOffset.UTC);
        
        // LocalDateTime in UTC context for display purposes
        LocalDateTime utcLocalDateTime = LocalDateTime.now(ZoneOffset.UTC);
        
        // LocalDate in UTC context
        LocalDate utcLocalDate = LocalDate.now(ZoneOffset.UTC);
        
        // Add cloud-ready datetime attributes standardized on UTC
        model.addAttribute("timestamp", currentInstant);
        model.addAttribute("utcDateTime", utcDateTime);
        model.addAttribute("localDateTime", utcLocalDateTime);
        model.addAttribute("localDate", utcLocalDate);
        
        // Add formatted ISO-8601 strings for API/logging compatibility
        model.addAttribute("timestampIso", currentInstant.toString());
        model.addAttribute("utcDateTimeIso", utcDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        
        // Add timezone information for debugging
        model.addAttribute("timezone", "UTC");
        model.addAttribute("zoneOffset", ZoneOffset.UTC.toString());
        
        return "date/test";
    }

}
