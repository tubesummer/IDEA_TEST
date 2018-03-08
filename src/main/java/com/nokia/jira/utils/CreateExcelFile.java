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
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;




/**
 * @author bpan
 *
 * created 2018年2月27日
 */
public class CreateExcelFile {
	
	private static HSSFWorkbook workbook = null; 
	
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
    public static boolean sheetExist(String fileDir, String sheetName){
    	
    	 boolean flag = false;  
         File file = new File(fileDir); 
         
         if (file.exists()) {
			 //文件存在，创建workbook        	 
        	 try {
				workbook = new HSSFWorkbook(new FileInputStream(file));
				//添加worksheet(不添加worksheet时生成的xls文件打开时会报错)
				HSSFSheet sheet = workbook.getSheet(sheetName);				
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
     * 创建新excel.
     * @param fileDir excel的路径 
     * @param sheetName 要创建的表格索引 
     * @param titleRow  excel的第一行即表格头
     */
    public static void createExcel(String fileDir, String sheetName, String titleRow[]){
    	
    	//创建workbook
    	workbook = new HSSFWorkbook();
    	
    	//添加Worksheet（不添加sheet时生成的xls文件打开时会报错)
    	HSSFSheet sheet = workbook.createSheet(sheetName);
    	
    	//新建文件
    	FileOutputStream fileOutputStream = null;
    	
    	try {
			//添加表头, 创建第一行
			HSSFRow row = workbook.getSheet(sheetName).createRow(0);
			
			for (short i = 0; i < titleRow.length; i++) {
				HSSFCell cell = row.createCell(i,CellType.BLANK);
				cell.setCellValue(titleRow[i]);
			}
			fileOutputStream = new FileOutputStream(fileDir);
			workbook.write(fileOutputStream);
			
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
     * 往excel中写入(已存在的数据无法写入). 
     * @param fileDir    文件路径 
     * @param sheetName  表格索引 
     * @param object 
     * @throws Exception 
     */  
    
    public static void writeToExcel(String fileDir, String sheetName, List<Map<String,String>> mapList) throws Exception{
    	
    	//创建workbook
    	File file = new File(fileDir);
    	
    	try {
			workbook = new HSSFWorkbook(new FileInputStream(file));			
			
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	//文件流
    	FileOutputStream fileOutputStream = null;
    	HSSFSheet sheet = workbook.getSheet(sheetName);
    	// 获取表格的总行数  
        // int rowCount = sheet.getLastRowNum() + 1; // 需要加一
    	//获取表头的列数
    	int columnCount = sheet.getRow(0).getLastCellNum();
    	
    	try {  
            // 获得表头行对象  
            HSSFRow titleRow = sheet.getRow(0);
            
//            System.out.println(titleRow+"这是表头对象");
            
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
            workbook.write(fileOutputStream);  
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
    
    
    public static void main(String[] args) {
    	
    	String fileDir = "d:\\workbook.xls";
		
    	//判断文件是否存在
    	System.out.println("文件是否存在： "+CreateExcelFile.fileExist(fileDir));
    	
//    	if (!CreateExcelFile.fileExist(fileDir)) {
    		//创建文件
        	
        	String[] title = {"id","name","password"};
        	CreateExcelFile.createExcel(fileDir, "sheet1", title);
        	
        	List<Map> userList = new ArrayList<Map>();
        	
            Map<String,String> map=new HashMap<String,String>();
            map.put("id", "111");
            map.put("name", "张三");
            map.put("password", "111！@#");
            
            Map<String,String> map2=new HashMap<String,String>();
            map2.put("id", "222");
            map2.put("name", "李四");
            map2.put("password", "222！@#");
            userList.add(map);
            userList.add(map2);
            try {
//    			CreateExcelFile.writeToExcel(fileDir,"sheet1",userList);
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
//		}
    	
    	
	}
    
	
	
}
