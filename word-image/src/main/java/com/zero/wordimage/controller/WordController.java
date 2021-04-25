package com.zero.wordimage.controller;

import com.zero.wordimage.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Alexander Zero
 * @version 1.0.0
 * @date 2021/4/25
 */
@Controller
@RequestMapping("/submit")
public class WordController {

    @Autowired
    private WordService wordService;

    @GetMapping
    public void generate(@RequestParam String dir,
                         @RequestParam int columnSize,
                         @RequestParam double height,
                         @RequestParam double width,
                         HttpServletResponse response) throws Exception {
        wordService.generate(dir, columnSize, height, width, response);
    }

}
