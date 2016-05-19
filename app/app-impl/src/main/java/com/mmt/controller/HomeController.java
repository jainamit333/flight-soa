package com.mmt.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by amit on 19/5/16.
 */
@RestController
public class HomeController {

    @RequestMapping(value = "/" , method = RequestMethod.GET)
    public String home(){
        return "SOA- Up and Running";
    }
}
