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
 * created 2018��2��27��
 */
public class CreateExcelFile {
	
	private static HSSFWorkbook workbook = null; 
	
    /** 
     * �ж��ļ��Ƿ����. 
     * @param fileDir  �ļ�·�� 
     * @return 
     */  
    public static boolean fileExist(String fileDir){  
         boolean flag = false;  
         File file = new File(fileDir);  
         flag = file.exists();  
         return flag;  
    }
    /** 
     * �ж��ļ���sheet�Ƿ����. 
     * @param fileDir   �ļ�·�� 
     * @param sheetName  ��������� 
     * @return boolean
     */      
    public static boolean sheetExist(String fileDir, String sheetName){
    	
    	 boolean flag = false;  
         File file = new File(fileDir); 
         
         if (file.exists()) {
			 //�ļ����ڣ�����workbook        	 
        	 try {
				workbook = new HSSFWorkbook(new FileInputStream(file));
				//���worksheet(�����worksheetʱ���ɵ�xls�ļ���ʱ�ᱨ��)
				HSSFSheet sheet = workbook.getSheet(sheetName);				
				if (sheet!=null) {					
					flag = true;
				}				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
         }else {
			//�ļ�������
        	flag = false;
		}         
         return flag;
    }
    
    /**
     * ������excel.
     * @param fileDir excel��·�� 
     * @param sheetName Ҫ�����ı������ 
     * @param titleRow  excel�ĵ�һ�м����ͷ
     */
    public static void createExcel(String fileDir, String sheetName, String titleRow[]){
    	
    	//����workbook
    	workbook = new HSSFWorkbook();
    	
    	//���Worksheet�������sheetʱ���ɵ�xls�ļ���ʱ�ᱨ��)
    	HSSFSheet sheet = workbook.createSheet(sheetName);
    	
    	//�½��ļ�
    	FileOutputStream fileOutputStream = null;
    	
    	try {
			//��ӱ�ͷ, ������һ��
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
     * ɾ���ļ�. 
     * @param fileDir  �ļ�·�� 
     * @return ����ļ������ڷ���false, ����ļ�����ɾ���ɹ�֮�󷵻�true
     */  
    public static boolean deleteExcel(String fileDir) {  
        boolean flag = false;  
        File file = new File(fileDir);  
        // �ж�Ŀ¼���ļ��Ƿ����    
        if (!file.exists()) {  // �����ڷ��� false    
            return flag;    
        } else {    
            // �ж��Ƿ�Ϊ�ļ�    
            if (file.isFile()) {  // Ϊ�ļ�ʱ����ɾ���ļ�����    
                file.delete();  
                flag = true;  
            }   
        }  
        return flag;  
     } 
    
    /** 
     * ��excel��д��(�Ѵ��ڵ������޷�д��). 
     * @param fileDir    �ļ�·�� 
     * @param sheetName  ������� 
     * @param object 
     * @throws Exception 
     */  
    
    public static void writeToExcel(String fileDir, String sheetName, List<Map<String,String>> mapList) throws Exception{
    	
    	//����workbook
    	File file = new File(fileDir);
    	
    	try {
			workbook = new HSSFWorkbook(new FileInputStream(file));			
			
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	//�ļ���
    	FileOutputStream fileOutputStream = null;
    	HSSFSheet sheet = workbook.getSheet(sheetName);
    	// ��ȡ����������  
        // int rowCount = sheet.getLastRowNum() + 1; // ��Ҫ��һ
    	//��ȡ��ͷ������
    	int columnCount = sheet.getRow(0).getLastCellNum();
    	
    	try {  
            // ��ñ�ͷ�ж���  
            HSSFRow titleRow = sheet.getRow(0);
            
//            System.out.println(titleRow+"���Ǳ�ͷ����");
            
            if(titleRow!=null){ 
                for(int rowId=0;rowId<mapList.size();rowId++){
                    Map map = mapList.get(rowId);
                    HSSFRow newRow=sheet.createRow(rowId+1);
                    for (short columnIndex = 0; columnIndex < columnCount; columnIndex++) {  //������ͷ  
                    	//trim()�ķ�����ɾ���ַ�������β�Ŀո�
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
		
    	//�ж��ļ��Ƿ����
    	System.out.println("�ļ��Ƿ���ڣ� "+CreateExcelFile.fileExist(fileDir));
    	
//    	if (!CreateExcelFile.fileExist(fileDir)) {
    		//�����ļ�
        	
        	String[] title = {"id","name","password"};
        	CreateExcelFile.createExcel(fileDir, "sheet1", title);
        	
        	List<Map> userList = new ArrayList<Map>();
        	
            Map<String,String> map=new HashMap<String,String>();
            map.put("id", "111");
            map.put("name", "����");
            map.put("password", "111��@#");
            
            Map<String,String> map2=new HashMap<String,String>();
            map2.put("id", "222");
            map2.put("name", "����");
            map2.put("password", "222��@#");
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
