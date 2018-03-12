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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;






/**
 * @author bpan
 *
 * created 2018��2��27��
 */
public class CreateExcelFile {
	
	private static HSSFWorkbook hWorkbook = null; 
	private static XSSFWorkbook xWorkbook = null;
	
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
					//�ļ����ڣ�sheet����
					
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
     * @param sheetNames Ҫ�����ı�������б�
     * @param titleRow  excel�ĵ�һ�м����ͷ
     */
    public static void createExcelXls(String fileDir, List<String> sheetNames, String titleRow[]){
    	
    	//����workbook
    	hWorkbook = new HSSFWorkbook();
    	//�½��ļ�
    	FileOutputStream fileOutputStream = null;
    	HSSFRow row = null;    	
    	try {
			
    		CellStyle cellStyle = hWorkbook.createCellStyle();
    		cellStyle.setAlignment(HorizontalAlignment.LEFT);
    		cellStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
    		
    		//���Worksheet�������sheetʱ���ɵ�xls�ļ���ʱ�ᱨ��)
        	for(int i = 0; i<sheetNames.size(); i++){
        		hWorkbook.createSheet(sheetNames.get(i));
        		hWorkbook.getSheet(sheetNames.get(i)).createRow(0);
        		//��ӱ�ͷ, ������һ��
        		row = hWorkbook.getSheet(sheetNames.get(i)).createRow(0);
        		row.setHeight((short)(20*20));
        		for (short j = 0; j < titleRow.length; j++) {
    				
    				HSSFCell cell = row.createCell(j, CellType.BLANK);
    				cell.setCellValue(titleRow[j]);
    				cell.setCellStyle(cellStyle);
    			}
    			fileOutputStream = new FileOutputStream(fileDir);
    			hWorkbook.write(fileOutputStream);
        	}
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
            //������Ԫ����ʾ��ʽ
            CellStyle cellStyle = hWorkbook.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.LEFT);
            cellStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
            
            
            if(titleRow!=null){ 
                for(int rowId = 0; rowId < mapList.size(); rowId++){
                    Map<String,String> map = mapList.get(rowId);
                    HSSFRow newRow=sheet.createRow(rowId+1);
                    newRow.setHeight((short)(20*20));//�����и�  ����Ϊ20
                    
                    for (short columnIndex = 0; columnIndex < columnCount; columnIndex++) {  //������ͷ  
                    	//trim()�ķ�����ɾ���ַ�������β�Ŀո�	
                    	String mapKey = titleRow.getCell(columnIndex).toString().trim();  
                        HSSFCell cell = newRow.createCell(columnIndex);  
                        cell.setCellStyle(cellStyle);
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
    	
    	String fileDir = "d:\\workbook.xls";
    	
    	List<String> sheetName = new ArrayList<>();
    	
    	sheetName.add("A");
    	sheetName.add("B");
    	sheetName.add("C");
    	
    	System.out.println(sheetName);
    	
    	String[] title = {"id","name","password"};
    	CreateExcelFile.createExcelXls(fileDir, sheetName, title);
    	
    	List<Map<String,String>> userList1 = new ArrayList<Map<String,String>>();
        Map<String,String> map=new HashMap<String,String>();
        map.put("id", "111");
        map.put("name", "����");
        map.put("password", "111��@#");
        
        Map<String,String> map2=new HashMap<String,String>();
        map2.put("id", "222");
        map2.put("name", "����");
        map2.put("password", "222��@#");
        
        Map<String,String> map3=new HashMap<String,String>();
        map3.put("id", "33");
        map3.put("name", "����");
        map3.put("password", "333��@#");
        userList1.add(map);
        userList1.add(map2);
        userList1.add(map3);
        
        Map<String, List<Map<String, String>>> users = new HashMap<>();
        
        users.put("A", userList1);
        
        List<Map<String,String>> userList2 = new ArrayList<Map<String,String>>();
        Map<String,String> map4=new HashMap<String,String>();
        map4.put("id", "111");
        map4.put("name", "����");
        map4.put("password", "111��@#");
        
        Map<String,String> map5=new HashMap<String,String>();
        map5.put("id", "222");
        map5.put("name", "����");
        map5.put("password", "222��@#");
        
        Map<String,String> map6=new HashMap<String,String>();
        map6.put("id", "33");
        map6.put("name", "����");
        map6.put("password", "333��@#");
        userList2.add(map4);
        userList2.add(map5);
        userList2.add(map6);
        
        users.put("B", userList2);
        
        List<Map<String,String>> userList3 = new ArrayList<Map<String,String>>();
        
        
        users.put("C", userList3);
        
        System.out.println(sheetName.size());
        
        //ɾ��List �������ض���Ԫ��
        for(Iterator<String> sheeNameIterator = sheetName.iterator();sheeNameIterator.hasNext();){
        	
        	String sheet = sheeNameIterator.next();
        	
        	if ( users.get(sheet).size() == 0) {
				
        		sheeNameIterator.remove();
        		
			}
        }
        
        System.out.println(sheetName.size());
        
        createExcelXls(fileDir, sheetName, title);
        for (int j = 0; j < sheetName.size(); j++) {
        	
        	try {
				writeToExcelXls(fileDir, sheetName.get(j), users.get(sheetName.get(j)));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
        
        
    }
}
