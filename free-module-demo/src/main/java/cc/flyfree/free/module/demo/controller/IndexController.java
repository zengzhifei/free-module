package cc.flyfree.free.module.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import cc.flyfree.free.module.core.common.domain.Result;

/**
 * @author zengzhifei
 * @date 2026/2/28
 */
@RestController
public class IndexController {
    @GetMapping("/")
    public Result<String> index() {
        return Result.ok("hello world");
    }
}
