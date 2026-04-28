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

/**
 * Cloud-native date/time controller using java.time API.
 * All timestamps are standardized to UTC to ensure consistency across distributed cloud environments.
 */
@Controller
@RequestMapping("/date")
public class DateTimeTestController {

    @GetMapping("/test")
    public String dateTimeTest(Model model) {
        // Use Instant for UTC timestamps - cloud-native best practice
        Instant utcTimestamp = Instant.now();
        
        // Use ZonedDateTime with explicit UTC timezone for cloud consistency
        ZonedDateTime utcDateTime = ZonedDateTime.now(ZoneOffset.UTC);
        
        // LocalDateTime and LocalDate for date-only operations
        LocalDateTime localDateTime = LocalDateTime.now(ZoneOffset.UTC);
        LocalDate localDate = LocalDate.now(ZoneOffset.UTC);
        
        // Add UTC-based timestamps to model
        model.addAttribute("utcTimestamp", utcTimestamp);
        model.addAttribute("utcDateTime", utcDateTime);
        model.addAttribute("localDateTime", localDateTime);
        model.addAttribute("localDate", localDate);
        model.addAttribute("timestamp", utcTimestamp);
        
        return "date/test";
    }

}
