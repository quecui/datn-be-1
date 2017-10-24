package com.uet.datn.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MainService {

    @Autowired
    private ExcelService excelService;

    public String process(){
        excelService.processFile();
        excelService.getStudentList();

        return "ok";
    }
}
