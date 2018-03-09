/**
 * 
 */
package com.nokia.jira.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;






/**
 * @author bpan
 *
 * created 2018年2月27日
 */
public class CreateExcelFile {
	
	private static HSSFWorkbook hWorkbook = null; 
	private static XSSFWorkbook xssfWorkbook = null;
	
    /** 
     * 判断文件是否存在. 
     * @param fileDir  文件路径 
     * @return 
     */  
    public static boolean fileExist(String fileDir){  
         boolean flag = false;  
         File file = new File(fileDir);  
         flag = file.exists();  
         return flag;  
    }
    /** 
     * 判断文件的sheet是否存在. 
     * @param fileDir   文件路径 
     * @param sheetName  表格索引名 
     * @return boolean
     */      
    public static boolean XlsSheetExist(String fileDir, String sheetName){
    	
    	 boolean flag = false;  
         File file = new File(fileDir); 
         
         if (file.exists()) {
			 //文件存在，创建workbook        	 
        	 try {
				hWorkbook = new HSSFWorkbook(new FileInputStream(file));
				
				HSSFSheet sheet = hWorkbook.getSheet(sheetName);				
				if (sheet!=null) {					
					flag = true;
				}				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
         }else {
			//文件不存在
        	flag = false;
		}         
         return flag;
    }
    
    /**
     * 创建新excel(xls).
     * @param fileDir excel的路径 
     * @param sheetName 要创建的表格索引 
     * @param titleRow  excel的第一行即表格头
     */
    public static void createExcelXls(String fileDir, String sheetName, String titleRow[]){
    	
    	//创建workbook
    	hWorkbook = new HSSFWorkbook();
    	//添加Worksheet（不添加sheet时生成的xls文件打开时会报错)
    	hWorkbook.createSheet(sheetName);
    	
    	//新建文件
    	FileOutputStream fileOutputStream = null;
    	
    	try {
			//添加表头, 创建第一行
			HSSFRow row = hWorkbook.getSheet(sheetName).createRow(0);
			
			for (short i = 0; i < titleRow.length; i++) {
				
				HSSFCell cell = row.createCell(i, CellType.BLANK);
				
				CellStyle cellStyle = hWorkbook.createCellStyle();
				cellStyle.setAlignment(HorizontalAlignment.LEFT);
				
				cellStyle.setFillBackgroundColor(HSSFColor.ORANGE.index);
				
				cell.setCellValue(titleRow[i]);
			}
			fileOutputStream = new FileOutputStream(fileDir);
			hWorkbook.write(fileOutputStream);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}			
		}
    }
    
    /** 
     * 删除文件. 
     * @param fileDir  文件路径 
     * @return 如果文件不存在返回false, 如果文件存在删除成功之后返回true
     */  
    public static boolean deleteExcel(String fileDir) {  
        boolean flag = false;  
        File file = new File(fileDir);  
        // 判断目录或文件是否存在    
        if (!file.exists()) {  // 不存在返回 false    
            return flag;    
        } else {    
            // 判断是否为文件    
            if (file.isFile()) {  // 为文件时调用删除文件方法    
                file.delete();  
                flag = true;  
            }   
        }  
        return flag;  
     } 
    
    /** 
     * 往excel(xls)中写入(已存在的数据无法写入). 
     * @param fileDir    文件路径 
     * @param sheetName  表格索引 
     * @param object 
     * @throws Exception 
     */  
    
    public static void writeToExcelXls(String fileDir, String sheetName, List<Map<String,String>> mapList) throws Exception{
    	
    	//创建workbook
    	File file = new File(fileDir);
    	
    	try {
			hWorkbook = new HSSFWorkbook(new FileInputStream(file));			
			
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	//文件流
    	FileOutputStream fileOutputStream = null;
    	HSSFSheet sheet = hWorkbook.getSheet(sheetName);
    	// 获取表格的总行数  
        // int rowCount = sheet.getLastRowNum() + 1; // 需要加一
    	//获取表头的列数
    	int columnCount = sheet.getRow(0).getLastCellNum();
    	
    	try {  
            // 获得表头行对象  
            HSSFRow titleRow = sheet.getRow(0);
            
            if(titleRow!=null){ 
                for(int rowId=0;rowId<mapList.size();rowId++){
                    Map map = mapList.get(rowId);
                    HSSFRow newRow=sheet.createRow(rowId+1);
                    for (short columnIndex = 0; columnIndex < columnCount; columnIndex++) {  //遍历表头  
                    	//trim()的方法是删除字符串中首尾的空格
                    	String mapKey = titleRow.getCell(columnIndex).toString().trim();  
                        HSSFCell cell = newRow.createCell(columnIndex);  
                        cell.setCellValue(map.get(mapKey)==null ? null : map.get(mapKey).toString());  
                    } 
                }
            }  
  
            fileOutputStream = new FileOutputStream(fileDir);  
            hWorkbook.write(fileOutputStream);  
        } catch (Exception e) {  
            throw e;
        } finally {    
            try { 
            	if (fileOutputStream != null) {
            		fileOutputStream.close();
				}                    
            } catch (IOException e) {    
                e.printStackTrace();  
            }    
        }
    }
    
    /**
     * 创建Excel(xlsx)
     * @param fileDir  文件名称及地址
     * @param sheetName sheet的名称
     * @param titleRow  表头
     */
    public static void createExcelXlsx(String fileDir, String sheetName, String titleRow[]){
    	
    	
    	
    }
    
    
    
    public static void main(String[] args) {
    	
    	
    	
    	String fileDir = "d:\\xWorkbook.xlsx";
    	
    	xssfWorkbook = new XSSFWorkbook();
    	
    	//不穿参数的话sheet名称默认是sheet0
    	xssfWorkbook.createSheet("A");
    	xssfWorkbook.createSheet("B");
    	xssfWorkbook.createSheet("C");
    	
    	FileOutputStream fileOutputStream = null;
    	
    	try {
			fileOutputStream = new FileOutputStream(new File(fileDir));
			xssfWorkbook.write(fileOutputStream);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if (fileOutputStream != null) {
				
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
    }
	
	
}
