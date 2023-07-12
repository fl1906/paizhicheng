package top.flya.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.flya.entity.Activity;

@RestController
@RequestMapping("/activity")
@Slf4j
public class ActivityController {

    @PostMapping("/createActivity")
    public void createActivity(@RequestBody Activity activity){
        log.info("createActivity init");
    }
}
