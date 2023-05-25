package hello.hellospring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /**
     * home 화면, 제일 기본인 localhost:8080 으로 접속했을 때
     * 요청이 오면 Spring Container 안에서 관련 파일이 있나 확인 -> 없으면 index.html
     * 현재는 home 이라는 html 파일이 있기 때문에 index.html 보다 우선순위가 높은 home.html 로 페이지가 로드된다.
     */
    @GetMapping("/")
    public String home() {
        return "home";
    }
}
