package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import utils.log;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import au.com.bytecode.opencsv.CSVReader;

/**\
 * 从Excel文件中读取测试数据
 * @param filePath 
 * @param fileName 
 * @param sheetName 
 * @return
 */

public class ReadFile {
	public static Object[][] getTestDataFromExcel(
			String filePath,//文件路径
			String fileName,//文件名称
			String sheetName //表单名称
		){
		try {
		//创建一个读取文件流
		File file = new File(filePath + "\\" + fileName);
		//FileInputStream读取文件数据的输入字节流
		//创建一个获取文件的数据的输入字节流，inputStream类型是FileInputStream
		//获取文件
		FileInputStream inputStream = new FileInputStream(file);
		//获取文件的后缀名，substirng字符串截取，截取点后面的内容
		//indexOf截取点后面的内容
		String fileExtensionName = fileName.substring(fileName.indexOf("."));
		//判断后缀，生成WorkBook对象
		//赋值一个初始值为空
		Workbook workbook = null;
		//截取后面的文件类型是.xlsx，就执行下面的语句
		if (fileExtensionName.equals(".xlsx")) {
			//把前面的inputStream做参数作为初始化
			//XSSFWorkbook解析excel文件高版本的
			workbook = new XSSFWorkbook(inputStream);
		} else 
			if (fileExtensionName.equals(".xls")) {
				//HSSFWorkbook解析excel文件低版本的
				workbook = new HSSFWorkbook(inputStream);
			}
		//获得表单标签页
		Sheet sheet = workbook.getSheet(sheetName);
		//sheet.getLastRowNum()-sheet.getFirstRowNum();获得行数和（不算第一行）列数
		int rowCount = sheet.getLastRowNum()-sheet.getFirstRowNum();
		//获取列数，得到第一行和最后的号码，得到列数，行默认从0开始
		int colCount = sheet.getRow(0).getLastCellNum();
		//这个列表存放所有行的数据
		//List<Object[]>基本数据类型-列表
		//ArrayList<Object[]>是一种可变长列表数组实现
		List<Object[]> records = new ArrayList<Object[]>();
		//遍历所有单元格，编号为1的第二行开始遍历
		for (int i=1;i<rowCount+1;i++) {
			//获取当前行放到变量里
			Row row = sheet.getRow(i);
			//字符串数据，有多少列就存放多少列数组,长度是列数
			String fields[] = new String[colCount];
			//列循环，我们的列号从0开始循环的所有不用加1
			for (int j=0; j<colCount;j++){
			    //获取单元格数据，列的值，返回值是单元格的数据
				Cell cell=row.getCell(j);
				//当前遍历的列已经超过最后一个列了或为空的
				//当前行没有编号为j的列存在，就赋值为空串
				if (j>=row.getLastCellNum() || cell == null){
					fields[j]="";
				//否则就是有单元格
				} else {
					//字符串类型Cell.CELL_TYPE_STRING，指定单元格格式：为字符串
					cell.setCellType(Cell.CELL_TYPE_STRING);
					//获取单元格字符串数据存储到fields中
					fields[j] = cell.getStringCellValue();
				}
			}
			//每一个列的值都加到records列表数组里去
			records.add(fields);
			//转换数据格式
			//第一维就是列表的长度
		}
		//转换数据格式为二维数组，长度就是列表的长度
		Object[][] results = new Object[records.size()][];
		for (int i=0; i<records.size(); i++) {
				//把i的值赋值到数组里
			results[i] = records.get(i);
		}
			//返回值
		return results;
	}catch(IOException e){
			e.printStackTrace();
			log.error(e.getMessage());
			return null;
	}
  }
  /*
   * 读取CSV文件中读取数据
   * @param filePath
   * @param fileName
   * @return
   */
	public static Object[][] getTestDataFromCSVFile(
			String filePath,String fileName){
			CSVReader csvReader;
			List<String[]> records =new ArrayList<String[]>();
			try {
			//普通读取方式，对其文件
			//FileReader file = new FileReader(filePath+"\\"+fileName);
			//csvReader = new CSVReader(file);
			//设置字符集读取方式
			FileInputStream fins = new FileInputStream(filePath+"\\"+fileName);
			//也可以修改为UTF-8
			//读取文件编码集是GBK字符集
			InputStreamReader gbReader = new InputStreamReader(fins,"GBK" );
			//创建一个对象csvReader
			csvReader = new CSVReader(gbReader);		
			//读取CSV所有信息
			records = csvReader.readAll();
			//关闭CSV
			csvReader.close();
			}catch(Exception e) {
				e.printStackTrace();
				log.error("Can not read csv file");
			}
			//返回从第2行开始的数据
			Object[][] results = new Object[records.size()-1][];
			//从1开始减1
			for (int i=1;i<records.size();i++) {
				//results是2维数组，默认从0开始，第一行
				//results[i-1]，把第二行给结果，放到第一行去
				results[i-1] = records.get(i);
			}
			//静态方法返回数据
			return results;
		}
}

