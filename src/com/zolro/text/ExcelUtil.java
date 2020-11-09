package com.zyd.shiro.utils;

import com.zyd.shiro.business.annotation.FieldValue;
import lombok.SneakyThrows;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings("hiding")
public class ExcelUtil<T> {

    private static SimpleDateFormat simFormat = new SimpleDateFormat("yyyy-MM-dd");

    private Workbook workbook;

    public List<T> importExcel(String sheetName, File f, Class<T> item) {
        List<T> list = null;
        try {
            if (f.getName().endsWith("xlsx")) {
                workbook = new XSSFWorkbook(new FileInputStream(f));
            } else {
                workbook = new HSSFWorkbook(new FileInputStream(f));
            }
            Sheet sheet = workbook.getSheet(sheetName);
            if (!"".equals(sheetName.trim())) {
                sheet = workbook.getSheet(sheetName);// 如果指定sheet名,则取指定sheet中的内容.
            }
            if (sheet == null) {
                sheet = workbook.getSheetAt(0); // 如果传入的sheet名不存在则默认指向第1个sheet.
            }
            // 获取数据
            list = dispatch(sheet, item);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * 生成实体类的 List<T>
     * 由于采取反射技术，所以要传入 实体类的类型 ,例如Entity.class
     */
    @SneakyThrows
    public List<T> dispatch(Sheet sheet, Class<T> clazz) {
        List<T> instances = new ArrayList<>();
        List<Map<String, String>> sheetValue = parseExcelSheet(sheet, clazz);
        for (int i = 0; i < sheetValue.size(); i++) {
            Map<String, String> map = sheetValue.get(i);
            Field[] fields = clazz.getDeclaredFields();
            try {
                T t2 = clazz.newInstance();
                for (Field field : fields) {
                    field.setAccessible(true);
                    Type type = field.getType();
                    if (!field.getName().equals("serialVersionUID")) {
                        field.set(t2, getTypeValue(map.get(field.getName()), type.getTypeName()));
                    }
                }
                instances.add(t2);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        return instances;
    }

    //赋值判断类型
    public Object getTypeValue(String value, String type) throws ParseException {
        if(null==value){
            return null;
        }
        Object obj = new Object();
        if ("int".equals(type) || type.indexOf("Integer") > -1) {
            obj = Integer.valueOf(value);
        } else if ("short".equals(type) || type.indexOf("Short") > -1) {
            obj = Short.valueOf(value);
        } else if ("long".equals(type) || type.indexOf("Long") > -1) {
            obj = Long.valueOf(value);
        } else if ("float".equals(type) || type.indexOf("Float") > -1) {
            obj = Float.valueOf(value);
        } else if ("double".equals(type) || type.indexOf("Double") > -1) {
            obj = Double.valueOf(value);
        } else if ("boolean".equals(type) || type.indexOf("Boolean") > -1) {
            obj = Boolean.valueOf(value);
        } else if ("date".equals(type) || type.indexOf("Date") > -1) {
            obj = simFormat.parse(value);
        } else {
            obj = value;
        }
        return obj;
    }


    /**
     * 解析Excel第一行，并生成以第一行表头为key，每一行的值为value的map
     * 例如: excel内容如下
     * 那么List<Map>就是的内容就是
     * List: map1( id: 1, alarm: abcd1 ),map2(( id: 2, alarm: abcd2 ).....
     */
    public List<Map<String, String>> parseExcelSheet(Sheet sheet, Class<T> cls) {
        List<Map<String, String>> result = new ArrayList<>();
        Map<String, String> rowValue = null;
        int rows = sheet.getPhysicalNumberOfRows();
        String[] headers = getHeaderValue(sheet.getRow(0), cls);

        for (int i = 1; i < rows; i++) {
            rowValue = new HashMap<>();
            Row row = sheet.getRow(i);
            for (int kk = 0; kk < headers.length; kk++) {
                rowValue.put(headers[kk], String.valueOf(getCellValue(row.getCell(kk))));
            }
            result.add(rowValue);
        }
        return result;
    }

    /**
     * 获取第一行，表头，也就是实体类的字段，支持中英文，及下划线，忽略大小写，但是绝笔不能重复，表头有重复字段则不能解析
     **/
    private String[] getHeaderValue(Row rowHeader, Class<T> cls) {
        int colNum = rowHeader.getPhysicalNumberOfCells();
        String[] headValue = new String[rowHeader.getPhysicalNumberOfCells()];
        Map<String, String> map = getFieldAnnotation(cls);
        for (int i = 0; i < colNum; i++) {
            String title = rowHeader.getCell(i).getStringCellValue();
            headValue[i] = map.get(title);
        }
        return headValue;
    }

    public Map<String, String> getFieldAnnotation(Class<T> cls) {
        Map<String, String> map = new HashMap<>();
        //当父类为null的时候说明到达了最上层的父类(Object类).
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (!field.getName().equals("serialVersionUID")) {
                FieldValue fieldValue = field.getAnnotation(FieldValue.class);
                if (fieldValue == null) {
                    continue;
                }
                map.put(fieldValue.name(), field.getName());
            }
        }
        return map;
    }

    @SuppressWarnings("deprecation")
    public Object getCellValue(Cell cell) {
        Object value = null;
        switch (cell.getCellType().getCode()) {
            case 0:// 数字
                // 如果为时间格式的内容
                if (DateUtil.isCellDateFormatted(cell)) {
                    // 注：format格式 yyyy-MM-dd hh:mm:ss
                    // 中小时为12小时制，若要24小时制，则把小h变为H即可，yyyy-MM-dd HH:mm:ss
                    value = dateFomtConv(DateUtil.getJavaDate(cell.getNumericCellValue())).toString();
                    break;
                } else {
                    value = new DecimalFormat("0").format(cell.getNumericCellValue());
                }
                break;
            case 1: // 字符串
                value = cell.getStringCellValue();
                break;
            case 4: // Boolean
                value = cell.getBooleanCellValue() + "";
                break;
            case 2: // 公式
                value = cell.getCellFormula() + "";
                break;
            case 3: // 空值
                value = "";
                break;
            case 5: // 故障
                value = "非法字符";
                break;
            default:
                value = "未知类型";
                break;
        }
        return value;
    }

    public String dateFomtConv(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return simFormat.format(calendar.getTime());

    }


}