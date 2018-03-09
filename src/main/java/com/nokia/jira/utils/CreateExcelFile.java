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
 * created 2018��2��27��
 */
public class CreateExcelFile {
	
	private static HSSFWorkbook hWorkbook = null; 
	private static XSSFWorkbook xssfWorkbook = null;
	
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
    public static boolean XlsSheetExist(String fileDir, String sheetName){
    	
    	 boolean flag = false;  
         File file = new File(fileDir); 
         
         if (file.exists()) {
			 //�ļ����ڣ�����workbook        	 
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
			//�ļ�������
        	flag = false;
		}         
         return flag;
    }
    
    /**
     * ������excel(xls).
     * @param fileDir excel��·�� 
     * @param sheetName Ҫ�����ı������ 
     * @param titleRow  excel�ĵ�һ�м����ͷ
     */
    public static void createExcelXls(String fileDir, String sheetName, String titleRow[]){
    	
    	//����workbook
    	hWorkbook = new HSSFWorkbook();
    	//���Worksheet�������sheetʱ���ɵ�xls�ļ���ʱ�ᱨ��)
    	hWorkbook.createSheet(sheetName);
    	
    	//�½��ļ�
    	FileOutputStream fileOutputStream = null;
    	
    	try {
			//��ӱ�ͷ, ������һ��
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
     * ��excel(xls)��д��(�Ѵ��ڵ������޷�д��). 
     * @param fileDir    �ļ�·�� 
     * @param sheetName  ������� 
     * @param object 
     * @throws Exception 
     */  
    
    public static void writeToExcelXls(String fileDir, String sheetName, List<Map<String,String>> mapList) throws Exception{
    	
    	//����workbook
    	File file = new File(fileDir);
    	
    	try {
			hWorkbook = new HSSFWorkbook(new FileInputStream(file));			
			
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	//�ļ���
    	FileOutputStream fileOutputStream = null;
    	HSSFSheet sheet = hWorkbook.getSheet(sheetName);
    	// ��ȡ����������  
        // int rowCount = sheet.getLastRowNum() + 1; // ��Ҫ��һ
    	//��ȡ��ͷ������
    	int columnCount = sheet.getRow(0).getLastCellNum();
    	
    	try {  
            // ��ñ�ͷ�ж���  
            HSSFRow titleRow = sheet.getRow(0);
            
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
     * ����Excel(xlsx)
     * @param fileDir  �ļ����Ƽ���ַ
     * @param sheetName sheet������
     * @param titleRow  ��ͷ
     */
    public static void createExcelXlsx(String fileDir, String sheetName, String titleRow[]){
    	
    	
    	
    }
    
    
    
    public static void main(String[] args) {
    	
    	
    	
    	String fileDir = "d:\\xWorkbook.xlsx";
    	
    	xssfWorkbook = new XSSFWorkbook();
    	
    	//���������Ļ�sheet����Ĭ����sheet0
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
