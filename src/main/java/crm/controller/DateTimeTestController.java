package crm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Cloud-ready date/time controller using java.time API with UTC standardization.
 * Eliminates timezone and clock synchronization issues in distributed cloud environments.
 */
@Controller
@RequestMapping("/date")
public class DateTimeTestController {

    // Use UTC as the standard timezone for all cloud operations
    private static final ZoneId UTC_ZONE = ZoneId.of("UTC");
    private static final Clock UTC_CLOCK = Clock.systemUTC();

    @GetMapping("/test")
    public String dateTimeTest(Model model) {
        // Use java.time API with UTC for cloud-native time handling
        Instant currentInstant = Instant.now(UTC_CLOCK);
        ZonedDateTime utcDateTime = ZonedDateTime.now(UTC_CLOCK);
        LocalDateTime localDateTime = LocalDateTime.now(UTC_CLOCK);
        LocalDate localDate = LocalDate.now(UTC_CLOCK);
        
        // Add attributes with UTC-based timestamps
        model.addAttribute("timestamp", currentInstant);
        model.addAttribute("utcDateTime", utcDateTime);
        model.addAttribute("localDateTime", localDateTime);
        model.addAttribute("localDate", localDate);
        model.addAttribute("timezone", "UTC");
        
        return "date/test";
    }

}
