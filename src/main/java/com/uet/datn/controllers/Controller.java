package com.uet.datn.controllers;

import com.uet.datn.services.ExcelService;
import com.uet.datn.services.MainService;
import com.uet.datn.services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class Controller {
    @Autowired
    private StorageService storageService;

    @Autowired
    private MainService mainService;

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        return storageService.store(file);
    }

    @RequestMapping(value = "/process", method = RequestMethod.GET)
    public String processFile(){
        return mainService.process();
    }

}
