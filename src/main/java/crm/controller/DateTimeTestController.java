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
 * Cloud-native date/time controller using java.time API.
 * All timestamps are standardized to UTC to ensure consistency across distributed cloud environments.
 */
@Controller
@RequestMapping("/date")
public class DateTimeTestController {

    // Use UTC clock for all time operations to ensure consistency across cloud regions
    private final Clock utcClock = Clock.systemUTC();

    @GetMapping("/test")
    public String dateTimeTest(Model model) {
        // Use java.time API with UTC standardization for cloud environments
        Instant currentInstant = Instant.now(utcClock);
        ZonedDateTime utcDateTime = ZonedDateTime.now(utcClock);
        LocalDateTime localDateTime = LocalDateTime.now(utcClock);
        LocalDate localDate = LocalDate.now(utcClock);
        
        // Add UTC-based timestamps to model
        model.addAttribute("instant", currentInstant);
        model.addAttribute("utcDateTime", utcDateTime);
        model.addAttribute("localDateTime", localDateTime);
        model.addAttribute("localDate", localDate);
        model.addAttribute("timezone", "UTC");
        model.addAttribute("epochMilli", currentInstant.toEpochMilli());
        
        // For display purposes, also provide formatted strings
        model.addAttribute("isoInstant", currentInstant.toString());
        model.addAttribute("isoDateTime", utcDateTime.toString());
        
        return "date/test";
    }
    
    /**
     * Example method showing how to convert to specific timezone for display purposes only.
     * Storage and inter-service communication should always use UTC.
     */
    public ZonedDateTime convertToDisplayTimezone(Instant instant, String timezoneId) {
        ZoneId zoneId = ZoneId.of(timezoneId);
        return instant.atZone(zoneId);
    }

}
