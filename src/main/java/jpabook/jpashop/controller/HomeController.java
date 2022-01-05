package jpabook.jpashop.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class HomeController {

    //Logger log = ILoggerFactory.getLogger(getClass()); 어노테이션으로 대체

    @RequestMapping("/")
    public String home() {
        log.info("home controller");
        return "home";
    }
}
