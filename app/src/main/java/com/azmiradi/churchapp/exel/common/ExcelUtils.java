package com.azmiradi.churchapp.exel.common;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.azmiradi.churchapp.all_applications.ApplicationPojo;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/**
 * Excel Worksheet Utility Methods
 * <p>
 * Created by: Ranit Raj Ganguly on 16/04/21.
 */
public class ExcelUtils {
    public static final String TAG = "ExcelUtil";
    private static Cell cell;
    private static Sheet sheet;
    private static Workbook workbook;
    private static CellStyle headerCellStyle;

    private static List<ApplicationPojo> importedExcelData;

    /**
     * Import data from Excel Workbook
     *
     * @param context  - Application Context
     * @param fileName - Name of the excel file
     * @return importedExcelData
     */
    public static List<ApplicationPojo> readFromExcelWorkbook(Context context, String fileName) {
        return retrieveExcelFromStorage(context, fileName);
    }


    /**
     * Export Data into Excel Workbook
     *
     * @param context  - Pass the application context
     * @param fileName - Pass the desired fileName for the output excel Workbook
     * @param dataList - Contains the actual data to be displayed in excel
     */
    public static boolean exportDataIntoWorkbook(Context context, String fileName,
                                                 List<ApplicationPojo> dataList) {
        boolean isWorkbookWrittenIntoStorage;

        // Check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.e(TAG, "Storage not available or read only");
            return false;
        }

        // Creating a New HSSF Workbook (.xls format)
        workbook = new HSSFWorkbook();

        setHeaderCellStyle();

        // Creating a New Sheet and Setting width for each column
        sheet = workbook.createSheet(Constants.EXCEL_SHEET_NAME);
        sheet.setColumnWidth(0, (15 * 400));
        sheet.setColumnWidth(1, (15 * 400));

        setHeaderRow();
        fillDataIntoExcel(dataList);
        isWorkbookWrittenIntoStorage = storeExcelInStorage(context, fileName);

        return isWorkbookWrittenIntoStorage;
    }

    /**
     * Checks if Storage is READ-ONLY
     *
     * @return boolean
     */
    private static boolean isExternalStorageReadOnly() {
        String externalStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(externalStorageState);
    }

    /**
     * Checks if Storage is Available
     *
     * @return boolean
     */
    private static boolean isExternalStorageAvailable() {
        String externalStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(externalStorageState);
    }

    /**
     * Setup header cell style
     */
    private static void setHeaderCellStyle() {
        headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFillForegroundColor(HSSFColor.AQUA.index);
        headerCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        headerCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
    }

    /**
     * Setup Header Row
     */
    private static void setHeaderRow() {
        Row headerRow = sheet.createRow(0);

        cell = headerRow.createCell(0);
        cell.setCellValue("البريد الالكتروني");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(1);
        cell.setCellValue("الرقم القومي");
        cell.setCellStyle(headerCellStyle);


        cell = headerRow.createCell(2);
        cell.setCellValue("رقم الهاتف");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(3);
        cell.setCellValue("المسمي الوظيفي");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(4);
        cell.setCellValue("اللقب");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(5);
        cell.setCellValue("الاسم");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(6);
        cell.setCellValue("جهة العمل");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(7);
        cell.setCellValue("الفئة");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(8);
        cell.setCellValue("منطقة الجلوس");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(9);
        cell.setCellValue("مفعلة");
        cell.setCellStyle(headerCellStyle);


        cell = headerRow.createCell(10);
        cell.setCellValue("ملاحظات");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(11);
        cell.setCellValue("تم ارساله التفعيل");
        cell.setCellStyle(headerCellStyle);


        cell = headerRow.createCell(12);
        cell.setCellValue("وجة البطاقة");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(13);
        cell.setCellValue("خلفية البطاقة");
        cell.setCellStyle(headerCellStyle);
    }


    /**
     * Fills Data into Excel Sheet
     * <p>
     * NOTE: Set row index as i+1 since 0th index belongs to header row
     *
     * @param dataList - List containing data to be filled into excel
     */
    private static void fillDataIntoExcel(List<ApplicationPojo> dataList) {
        for (int i = 0; i < dataList.size(); i++) {
            // Create a New Row for every new entry in list
            Row rowData = sheet.createRow(i + 1);

            // Create Cells for each row
            cell = rowData.createCell(0);
            cell.setCellValue(dataList.get(i).getEmail());

            // Create Cells for each row
            cell = rowData.createCell(1);
            cell.setCellValue(dataList.get(i).getNationalID());

            // Create Cells for each row
            cell = rowData.createCell(2);
            cell.setCellValue(dataList.get(i).getPhone());


            // Create Cells for each row
            cell = rowData.createCell(3);
            cell.setCellValue(dataList.get(i).getJobTitle());


            // Create Cells for each row
            cell = rowData.createCell(4);
            cell.setCellValue(dataList.get(i).getTitle());


            // Create Cells for each row
            cell = rowData.createCell(5);
            cell.setCellValue(dataList.get(i).getName());

            // Create Cells for each row
            cell = rowData.createCell(6);
            cell.setCellValue(dataList.get(i).getEmployer());


            // Create Cells for each row
            cell = rowData.createCell(7);
            cell.setCellValue(dataList.get(i).getClassName());

            // Create Cells for each row
            cell = rowData.createCell(8);
            cell.setCellValue(dataList.get(i).getZoneID());

            // Create Cells for each row
            cell = rowData.createCell(9);
            cell.setCellValue(Boolean.TRUE.equals(dataList.get(i).isApproved()));

            // Create Cells for each row
            cell = rowData.createCell(10);
            cell.setCellValue(dataList.get(i).getNote());

            // Create Cells for each row
            cell = rowData.createCell(11);
            cell.setCellValue(Boolean.TRUE.equals(dataList.get(i).isSandedApproved()));

            // Create Cells for each row
            cell = rowData.createCell(12);
            cell.setCellValue(dataList.get(i).getImage1());

            // Create Cells for each row
            cell = rowData.createCell(13);
            cell.setCellValue(dataList.get(i).getImage2());

        }
    }

    public static File commonDocumentDirPath(String FolderName) {
        File dir = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/" + FolderName);
        } else {
            dir = new File(Environment.getExternalStorageDirectory() + "/" + FolderName);
        }

        // Make sure the path directory exists.
        if (!dir.exists()) {
            // Make it, if it doesn't exit
            boolean success = dir.mkdirs();
            if (!success) {
                dir = null;
            }
        }
        return dir;
    }

    /**
     * Store Excel Workbook in external storage
     *
     * @param context  - application context
     * @param fileName - name of workbook which will be stored in device
     * @return boolean - returns state whether workbook is written into storage or not
     */
    private static boolean storeExcelInStorage(Context context, String fileName) {
        boolean isSuccess;

        File file = new File(commonDocumentDirPath("FilesExels"), Calendar.getInstance().getTimeInMillis() + ".xls");
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(file);
            workbook.write(fileOutputStream);
            Log.e(TAG, "Writing file" + file);
            isSuccess = true;
        } catch (IOException e) {
            Log.e(TAG, "Error writing Exception: ", e);
            isSuccess = false;
        } catch (Exception e) {
            Log.e(TAG, "Failed to save file due to Exception: ", e);
            isSuccess = false;
        } finally {
            try {
                if (null != fileOutputStream) {
                    fileOutputStream.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return isSuccess;
    }

    /**
     * Retrieve excel from External Storage
     *
     * @param context  - application context
     * @param fileName - name of workbook to be read
     * @return importedExcelData
     */
    private static List<ApplicationPojo> retrieveExcelFromStorage(Context context, String fileName) {
        importedExcelData = new ArrayList<>();

        File file = new File(commonDocumentDirPath("FilesExels"), fileName);
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(file);
            Log.e(TAG, "Reading from Excel" + file);

            // Create instance having reference to .xls file
            workbook = new HSSFWorkbook(fileInputStream);

            // Fetch sheet at position 'i' from the workbook
            sheet = workbook.getSheetAt(0);

            // Iterate through each row
            for (Row row : sheet) {
                int index = 0;
                ApplicationPojo applicationPojo = new ApplicationPojo();

                if (row.getRowNum() > 0) {
                    Iterator<Cell> cellIterator = row.cellIterator();

                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        if (index == 0)
                            applicationPojo.setEmail(cell.getStringCellValue());

                        if (index == 1)
                            applicationPojo.setNationalID(cell.getStringCellValue());

                        if (index == 2)
                            applicationPojo.setPhone(cell.getStringCellValue());

                        if (index == 3)
                            applicationPojo.setJobTitle(cell.getStringCellValue());

                        if (index == 4)
                            applicationPojo.setTitle(cell.getStringCellValue());

                        if (index == 5)
                            applicationPojo.setName(cell.getStringCellValue());

                        if (index == 6)
                            applicationPojo.setEmployer(cell.getStringCellValue());

                        if (index == 7)
                            applicationPojo.setClassName(cell.getStringCellValue());

                        if (index == 8)
                            applicationPojo.setZoneID(cell.getStringCellValue());

                        if (index == 9)
                            applicationPojo.setApproved(cell.getBooleanCellValue());

                        if (index == 10)
                            applicationPojo.setNote(cell.getStringCellValue());

                        if (index == 11)
                            applicationPojo.setSandedApproved(cell.getBooleanCellValue());

                        if (index == 12)
                            applicationPojo.setImage1(cell.getStringCellValue());

                        if (index == 13)
                            applicationPojo.setImage2(cell.getStringCellValue());

                        index++;
                    }
                }
                importedExcelData.add(applicationPojo);
            }

        } catch (IOException e) {
            Log.e(TAG, "Error Reading Exception: ", e);

        } catch (Exception e) {
            Log.e(TAG, "Failed to read file due to Exception: ", e);

        } finally {
            try {
                if (null != fileInputStream) {
                    fileInputStream.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return importedExcelData;
    }

}
