package crm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * Controller demonstrating cloud-safe date/time handling.
 * Migrated from java.util.Date (server-local timezone dependent) to java.time API
 * with UTC standardization for consistent behavior across distributed cloud environments.
 *
 * Blockers fixed:
 *   - cr-java-0111 (line 19): Replaced new Date() with ZonedDateTime.now(ZoneOffset.UTC)
 *   - cr-java-0111 (line 20): Replaced LocalDateTime.now() with ZonedDateTime.now(ZoneOffset.UTC)
 */
@Controller
@RequestMapping("/date")
public class DateTimeTestController {

    @GetMapping("/test")
    public String dateTimeTest(Model model) {
        // UTC-standardized current timestamp — replaces new Date() (server-local timezone)
        ZonedDateTime utcNow = ZonedDateTime.now(ZoneOffset.UTC);
        // UTC-standardized date/time — replaces LocalDateTime.now() (no timezone context)
        ZonedDateTime utcDateTime = ZonedDateTime.now(ZoneOffset.UTC);
        // UTC date — replaces LocalDate.now() (server-local timezone dependent)
        LocalDate utcDate = LocalDate.now(ZoneOffset.UTC);
        // UTC instant for inter-service communication and database storage
        Instant utcInstant = Instant.now();

        model.addAttribute("standardDate", utcNow);
        model.addAttribute("localDateTime", utcDateTime);
        model.addAttribute("localDate", utcDate);
        model.addAttribute("timestamp", utcInstant);
        return "date/test";
    }

}
