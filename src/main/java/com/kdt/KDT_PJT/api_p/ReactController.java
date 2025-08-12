package com.kdt.KDT_PJT.api_p;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ReactController {

    @RequestMapping("/rc/**")
    public String redirectToReactApp() {
        return "forward:/index.html"; // React의 index.html을 반환
    }
}
