package crm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@Controller
@RequestMapping("/date")
public class DateTimeTestController {

    @GetMapping("/test")
    public String dateTimeTest(Model model) {
        // Using UTC for cloud-readiness to avoid server-local timezone dependencies
        ZonedDateTime nowUtc = ZonedDateTime.now(ZoneId.of("UTC"));
        
        model.addAttribute("standardDate", Date.from(nowUtc.toInstant()));
        model.addAttribute("localDateTime", nowUtc.toLocalDateTime());
        model.addAttribute("localDate", nowUtc.toLocalDate());
        model.addAttribute("timestamp", nowUtc.toInstant());
        return "date/test";
    }

}
