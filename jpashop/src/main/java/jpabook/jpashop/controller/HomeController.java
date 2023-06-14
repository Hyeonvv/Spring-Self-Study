package jpabook.jpashop.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class HomeController {

/*
    // @Slf4j 어노테이션을 통해서 간편하게 처리할 수 있다.
    Logger log = LoggerFactory.getLogger(getClass());
*/

    /**
     * home 화면
     */
    @RequestMapping("/")
    public String home() {
        log.info("home controller");
        return "home";
    }


}
