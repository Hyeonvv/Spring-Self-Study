package hello.hellospring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {

    @GetMapping("hello")
    public String hello(Model model) {
        model.addAttribute("data", "hello!"); // key: data, value: hello!
        return "hello";
    }


    @GetMapping("hello-mvc")
    public String helloMvc(@RequestParam(value = "name", required = false) String name, Model model) { // required 가 false 면 값을 꼭 넘기지 않아도 된다.
        model.addAttribute("name", name);
        return "hello-template";
    }

    @GetMapping("hello-string")
    @ResponseBody // @ResponseBody 를 사용하면 뷰 리졸버(viewResolver)를 사용하지 않음, HTTP의 BODY에 부분에 직접 데이터를 넣겠다.
    public String helloString(@RequestParam("name") String name) {
        return "hello " + name;
    }

    @GetMapping("hello-api")
    @ResponseBody
    public Hello helloApi(@RequestParam("name") String name) {
        Hello hello = new Hello();
        hello.setName(name);
        return hello; // 객체를 반환하면 json 형태로 반환된다. {"name":"~~~~"}
        // viewResolver 대신 HttpMessageConverter(객체면 JsonConverter, 문자면 StringConverter) 동작
    }

    static class Hello {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
