package crm.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class MyErrorController {

    @RequestMapping("/error")
    @ResponseBody
    public String error() {
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error handling");
    }
}
