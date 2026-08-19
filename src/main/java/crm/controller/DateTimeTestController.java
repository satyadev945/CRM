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
 * DateTimeTestController - Cloud-ready (Azure) date/time handling.
 *
 * Cloud Readiness Fix (cr-java-0111 - Clock/Time Dependencies):
 *   - Replaced java.util.Date (server-local timezone) with ZonedDateTime at UTC
 *     to ensure timezone-agnostic, distributed-safe time representation.
 *   - Replaced LocalDateTime.now() (server-local clock) with ZonedDateTime.now(ZoneOffset.UTC)
 *     so that all time values are anchored to UTC regardless of the host/container timezone.
 *   - Replaced java.util.Date import with java.time.ZonedDateTime.
 *   - For scheduled operations, use Azure Service Bus Scheduled Messages
 *     (via AzureServiceBusScheduler) instead of java.util.Timer or server-local scheduling,
 *     ensuring distributed, timezone-agnostic task execution across cloud regions.
 */
@Controller
@RequestMapping("/date")
public class DateTimeTestController {

    @GetMapping("/test")
    public String dateTimeTest(Model model) {
        // Cloud fix (cr-java-0111, line 19): replaced new Date() (server-local timezone)
        // with ZonedDateTime.now(ZoneOffset.UTC) for timezone-agnostic UTC time.
        model.addAttribute("standardDate", ZonedDateTime.now(ZoneOffset.UTC));

        // Cloud fix (cr-java-0111, line 20): replaced LocalDateTime.now() (server-local clock)
        // with ZonedDateTime.now(ZoneOffset.UTC) for distributed, timezone-agnostic time.
        model.addAttribute("localDateTime", ZonedDateTime.now(ZoneOffset.UTC));

        model.addAttribute("localDate", LocalDate.now(ZoneOffset.UTC));
        model.addAttribute("timestamp", Instant.now());
        return "date/test";
    }

}
