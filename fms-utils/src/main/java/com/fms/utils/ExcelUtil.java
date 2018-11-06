/**
 * Project Name:dbhs File Name:ExcelUtil.java Package Name:com.glodon.dbhs.utils
 * Date:2016年10月24日上午10:23:42 Copyright (c) 2016, chenzhou1025@126.com All Rights Reserved.
 *
 */

package com.fms.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * ClassName:ExcelUtil <br/>
 * Function: Excel导出（导出采用SXSSF，导入采用XSSF）. <br/>
 * Date: 2016年10月24日 上午10:23:42 <br/>
 * 
 * @author drc
 * @since JDK 1.8
 */
public class ExcelUtil {

	//文件后缀：xls文件
	public static String XLS_SUFFIX = ".xls";

	//文件后缀：xlsx文件
	public static String XLSX_SUFFIX = ".xlsx";

	public static List<List<String>> readXls(File file) throws IOException {
		if (!file.exists()) {
			throw new FileNotFoundException("未找到文件");
		}
		List<List<String>> list = null;
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			list = readXls(in, file.getPath());
		} finally {
			if (in != null) {
				in.close();
			}
		}
		return list;
	}

	public static List<List<String>> readXls(InputStream in, String realPath) throws IOException {
		if (in == null) {
			throw new IllegalArgumentException("输入流不能为空");
		}
		List<List<String>> resList = null;
		try {
			resList = parseXls(in, realPath);
		} finally {
			if (in != null) {
				in.close();
			}
		}
		return resList;
	}

	private static List<List<String>> parseXls(InputStream in, String filePath) throws IOException {
		List<List<String>> resList = new ArrayList();
		Workbook wb = null;
		try {
			if (ExcelUtil.isExcel2003(filePath)) {// 当excel是2003时,创建excel2003
				wb = new HSSFWorkbook(in);
			} else {// 当excel是2007时,创建excel2007
				wb = new XSSFWorkbook(in);
			}
			Sheet sheet = wb.getSheetAt(0);
			for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
				Row row = sheet.getRow(rowNum);
				List<String> rowContent = new ArrayList();
				if (null != row) {
					for (int j = 0; j < row.getLastCellNum(); j++) {
						rowContent.add(getCell(row.getCell(j)));
					}
				}
				resList.add(rowContent);
			}
		} finally {
			if (wb != null) {
				wb.close();
			}
		}
		return resList;
	}

	private static String getCell(Cell cell) {
		DecimalFormat df = new DecimalFormat("#");
		if (cell == null) {
			return "";
		}
		switch (cell.getCellType()) {
			case 0:
				if (HSSFDateUtil.isCellDateFormatted(cell)) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					return sdf.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue())).toString();
				}
				return df.format(cell.getNumericCellValue());
			case 1:
				return cell.getStringCellValue();
			case 2:
				return cell.getCellFormula();
			case 3:
				return "";
			case 4:
				return cell.getBooleanCellValue() + "";
			case 5:
				return cell.getErrorCellValue() + "";
		}
		return "";
	}

	// @描述：是否是2003的excel，返回true是2003
	public static boolean isExcel2003(String filePath) {
		return filePath.matches("^.+\\.(?i)(xls)$");
	}

	// @描述：是否是2007的excel，返回true是2007
	public static boolean isExcel2007(String filePath) {
		return filePath.matches("^.+\\.(?i)(xlsx)$");
	}

}
