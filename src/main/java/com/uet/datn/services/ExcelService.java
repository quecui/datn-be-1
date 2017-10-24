package com.uet.datn.services;

import com.uet.datn.models.Student;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class ExcelService {

    private final String GITHUB_LINK = "https://github.com/";
    private final String PROJECT_NAME = "/TestCI.git";
    private List<Student> studentList = new ArrayList<Student>();

    public void processFile()  {
        try {
            File excel = new File("upload-dir/data.xlsx");
            FileInputStream fis = new FileInputStream(excel);
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            Row row;

            boolean flag = false;

            for (int i = 1; i <= datatypeSheet.getLastRowNum(); i++) {
                row = datatypeSheet.getRow(i);
                if (row == null)
                    continue;

                if (flag){
                    setData(row);
                    continue;
                }

                String signal = (String) row.getCell(0).getStringCellValue();
                if (signal.equals("STT")) {
                    flag = true;
                }
            }
        }catch (Exception e){
            System.out.println("Error While Reading File !!");
        }

    }

    public  void setData(Row row){
        Student student = new Student();
        String name = row.getCell(2).getStringCellValue();
        name = formatName(name);

        student.setStudentName(name);
        student.setLinkGithub(processGitHubLink(name));

        studentList.add(student);
    }

    public String formatName(String str) {
        try {
            String temp = Normalizer.normalize(str, Normalizer.Form.NFD);
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            return pattern.matcher(temp).replaceAll("").toLowerCase().replaceAll(" ", " ").replaceAll("đ", "d");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    private  String processGitHubLink(String name){
        String[] str = name.split(" ");
        if (str.length == 2){
            name = str[1] + "_" + str[0].charAt(0) + str[0].charAt(0);
        }else {
            name = str[2] + "_" + str[0].charAt(0) + str[1].charAt(0);
        }

        return GITHUB_LINK + name + PROJECT_NAME;
    }

    public List<Student> getStudentList() {
        return studentList;
    }
}
