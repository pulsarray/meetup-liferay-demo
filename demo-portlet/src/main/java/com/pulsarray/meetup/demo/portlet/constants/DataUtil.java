package com.pulsarray.meetup.demo.portlet.constants;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.StringUtil;

/**
 * 
 * @author thouroro
 *
 */
public class DataUtil {
	private static final String[] GENDER = new String[] { "M", "F" };
	private static final String ETAT_CIVIL = "USER DATA";
	private static final int NB_ROW_MAX = 20000;
	private static final Object[] FILE_HEADER = new Object[] { "ID", "NAME", "FIRST NAME", "GENDER", "ADDRESS", "PHONE",
			"EMAIL" };
	public static final String MIME_TYPE = "mimeType";
	public static final String FILE_CONTENT = "fileContent";
	public static final String FILE_NAME = "fileName";
	private static final Log log = LogFactoryUtil.getLog(DataUtil.class);

	/**
	 * GET DATA AS BYTE
	 * @return
	 */
	public static byte[] getData() {
		return (byte[]) getDataMap().get(FILE_CONTENT);
	}

	/**
	 * CREATE XLSX FILE WITH FAKE DATA
	 * 
	 * @return
	 */
	public static Map<String, Object> getDataMap() {
		Map<String, Object> dataMap = new HashMap<>();
		try {
			XSSFWorkbook workbook = new XSSFWorkbook();

			XSSFSheet sheet = workbook.createSheet(ETAT_CIVIL);

			Map<String, Object[]> data = new TreeMap<>();
			data.put("1", FILE_HEADER);
			Random rand = new Random();
			for (int i = 2; i < NB_ROW_MAX; i++) {
				data.put(i + "",
						new Object[] { i, StringUtil.randomString(7), StringUtil.randomString(7),
								Arrays.asList(GENDER).get(rand.nextInt(2)), StringUtil.randomString(7),
								StringUtil.randomString(7), StringUtil.randomString(7) });
			}

			// Iterate over data and write to sheet
			Set<String> keyset = data.keySet();
			int rownum = 0;
			for (String key : keyset) {
				Row row = sheet.createRow(rownum++);
				Object[] objArr = data.get(key);
				int cellnum = 0;
				for (Object obj : objArr) {
					Cell cell = row.createCell(cellnum++);
					if (obj instanceof String)
						cell.setCellValue((String) obj);
					else if (obj instanceof Integer)
						cell.setCellValue((Integer) obj);
				}
			}

			// Write the workbook in file system
			File file = FileUtil.createTempFile("demo", "xlsx");
			FileOutputStream out = new FileOutputStream(file);
			workbook.write(out);
			out.close();
			byte[] fileContent = Files.readAllBytes(file.toPath());

			dataMap.put(FILE_NAME, "demo.xlsx");
			dataMap.put(FILE_CONTENT, fileContent);
			dataMap.put(MIME_TYPE, MimeTypesUtil.getContentType(file));
			return dataMap;

		} catch (Exception e) {
			log.error(e, e);
		}
		return null;
	}
	
}
