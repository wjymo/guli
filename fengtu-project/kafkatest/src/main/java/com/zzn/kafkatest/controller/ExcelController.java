package com.zzn.kafkatest.controller;

import com.zzn.kafkatest.util.ExcelField;
import com.zzn.kafkatest.vo.DeviceOverhaulMessageDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/export")
public class ExcelController {

    @PostMapping()
    public String export(@RequestParam("file") MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        Workbook wb = new XSSFWorkbook(inputStream);
        Field[] declaredFields = DeviceOverhaulMessageDTO.class.getDeclaredFields();
        List<DeviceOverhaulMessageDTO> deviceOverhaulMessageDTOList=new ArrayList<>();
        Map<Integer,Field> indexNameMap=new HashMap<>();
        if(wb != null) {
            //获取第一个sheet
            Sheet sheet = wb.getSheetAt(0);
            //获取最大行数
//            int rownum = sheet.getPhysicalNumberOfRows();
            int rownum = sheet.getLastRowNum();
            for (int i = 1; i <=rownum; i++) {
                DeviceOverhaulMessageDTO deviceOverhaulMessageDTO=null;
                if(i>1){
                    deviceOverhaulMessageDTO=new DeviceOverhaulMessageDTO();
                }
                Row row = sheet.getRow(i);
                short lastCellNum = row.getLastCellNum();
                for (int j = 0; j < lastCellNum; j++) {
                    Cell cell = row.getCell(j);
                    String cellValue = cell.getStringCellValue();
                    //当i为1，是模板excel文件第二行，此时cell为列名
                    if(i==1){
                        for (Field declaredField : declaredFields) {
//                            declaredField.setAccessible(true);
                            ExcelField annotation = declaredField.getAnnotation(ExcelField.class);
                            if(annotation!=null){
                                String title = annotation.title();
                                if(StringUtils.equals(title,cellValue)){
                                    indexNameMap.put(j,declaredField);
                                    break;
                                }
                            }
                        }
                    }else {
                        Field field = indexNameMap.get(j);
                        field.setAccessible(true);
                        try {
                            if(deviceOverhaulMessageDTO!=null){
                                field.set(deviceOverhaulMessageDTO,cellValue);
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if(deviceOverhaulMessageDTO!=null){
                    deviceOverhaulMessageDTOList.add(deviceOverhaulMessageDTO);
                }
            }
        }

        System.out.println(1);
        return null;
    }

}