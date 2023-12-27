package com.azmiradi.invitations.exel.common;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.azmiradi.invitations.all_applications.ApplicationPojo;

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
import java.util.Objects;

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


    public static List<ApplicationPojo> readFromExcelWorkbook(File file) {
        return retrieveExcel(file);
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
        sheet = workbook.createSheet("applications");
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
        headerCellStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
        headerCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        headerCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
    }

    /**
     * Setup Header Row
     */
    private static void setHeaderRow() {
        Row headerRow = sheet.createRow(0);

        cell = headerRow.createCell(0);
        cell.setCellValue("مسلسل");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(1);
        cell.setCellValue("الاسم");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(2);
        cell.setCellValue("الصفة");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(3);
        cell.setCellValue("set");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(4);
        cell.setCellValue("الجهة");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(5);
        cell.setCellValue("رقم الدعوة");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(6);
        cell.setCellValue("Color");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(7);
        cell.setCellValue("Class");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(8);
        cell.setCellValue("الرقم القومي");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(9);
        cell.setCellValue("Note");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(10);
        cell.setCellValue("Priority");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(11);
        cell.setCellValue("approved");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(12);
        cell.setCellValue("attend");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(13);
        cell.setCellValue("رقم المحمول");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(14);
        cell.setCellValue("وجه البطاقه");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(15);
        cell.setCellValue("خلف البطاقة");
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
            cell.setCellValue(i + 1);

            // Create Cells for each row
            cell = rowData.createCell(1);
            cell.setCellValue(dataList.get(i).getName());

            // Create Cells for each row
            cell = rowData.createCell(2);
            cell.setCellValue(dataList.get(i).getTitle());


            // Create Cells for each row
            cell = rowData.createCell(3);
            cell.setCellValue(dataList.get(i).getSeat());


            // Create Cells for each row
            cell = rowData.createCell(4);
            cell.setCellValue(dataList.get(i).getJobTitle());


            // Create Cells for each row
            cell = rowData.createCell(5);
            cell.setCellValue(dataList.get(i).getInvitationNumber());

            // Create Cells for each row
            cell = rowData.createCell(6);
            cell.setCellValue(dataList.get(i).getZoneID());


            // Create Cells for each row
            cell = rowData.createCell(7);
            cell.setCellValue(dataList.get(i).getClassName());

            // Create Cells for each row
            cell = rowData.createCell(8);
            cell.setCellValue(dataList.get(i).getNationalID());

            cell = rowData.createCell(9);
            cell.setCellValue(dataList.get(i).getNote());

            cell = rowData.createCell(10);
            if (dataList.get(i).getPriority() != null)
                cell.setCellValue(dataList.get(i).getPriority());
            else
                cell.setCellValue(0);

            // Create Cells for each row
            cell = rowData.createCell(11);
            cell.setCellValue(Boolean.TRUE.equals(dataList.get(i).isApproved()));

            // Create Cells for each row
            cell = rowData.createCell(12);
            cell.setCellValue(Boolean.TRUE.equals(dataList.get(i).isAttend()));

            cell = rowData.createCell(13);
            cell.setCellValue(dataList.get(i).getPhone());

            cell = rowData.createCell(14);
            cell.setCellValue(dataList.get(i).getImage1());

            cell = rowData.createCell(15);
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


    private static List<ApplicationPojo> retrieveExcel(File file) {
        importedExcelData = new ArrayList<>();

        FileInputStream is = null;
        try {
            is = new FileInputStream(file);
            jxl.Workbook workbook = jxl.Workbook.getWorkbook(is);

            // Fetch sheet at position 'i' from the workbook
            jxl.Sheet sheet = workbook.getSheet(0);
            for (int i = 1; i < sheet.getRows(); i++) {

                ApplicationPojo applicationPojo = new ApplicationPojo();
                String id = sheet.getCell(0, i).getContents().trim();
                applicationPojo.setInvitationNumber(id);

                String companionNationalID = Calendar.getInstance().getTimeInMillis() + "" + i + "" + id;
                String invitationCode = sheet.getCell(0, i).getContents().trim();
                applicationPojo.setCode(invitationCode);

                String namePlusTitle = sheet.getCell(1, i).getContents();

                if (namePlusTitle.contains("/")) {
                    String[] fullName = namePlusTitle.split("/");
                    applicationPojo.setTitle(fullName[0]);
                    applicationPojo.setName(fullName[1]);
                } else if (namePlusTitle.contains(".")) {
                    String[] fullName = namePlusTitle.split("\\.");
                    applicationPojo.setTitle(fullName[0] + " " +fullName[1]);
                    applicationPojo.setName(fullName[fullName.length-1]);
                } else {
                    applicationPojo.setName(namePlusTitle);
                }
//                String email = sheet.getCell(3, i).getContents();
//                applicationPojo.setEmail(email);


                String jobTitle = sheet.getCell(2, i).getContents();
                applicationPojo.setJobTitle(jobTitle);
//
//                String nationalID = sheet.getCell(3, i).getContents();
//                applicationPojo.setNationalID(nationalID);

                String employer = sheet.getCell(4, i).getContents();
                applicationPojo.setEmployer(employer);

                String zoneColor = sheet.getCell(5, i).getContents();
                applicationPojo.setZoneColor(zoneColor);

                String zoneName = sheet.getCell(6, i).getContents();
                applicationPojo.setZoneName(zoneName);

                String raw = sheet.getCell(7, i).getContents();
                applicationPojo.setRow(raw);

                applicationPojo.setNationalID(companionNationalID);

                applicationPojo.setApproved(true);

                if (applicationPojo.getTitle() == null)
                    applicationPojo.setTitle("");

                if (!Objects.equals(applicationPojo.getCode(), ""))
                    importedExcelData.add(applicationPojo);
            }
        } catch (
                IOException e) {
            Log.e(TAG, "Error Reading Exception: ", e);

        } catch (
                Exception e) {
            Log.e(TAG, "Failed to read file due to Exception: ", e);

        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }


        return importedExcelData;
    }

    private static List<ApplicationPojo> retrieveExcelFromStorage(File file) {
        importedExcelData = new ArrayList<>();

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
                        if (index == 1) {
                            if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                                if (cell.getStringCellValue().contains("/")) {
                                    String[] fullName = cell.getStringCellValue().split("/");
                                    if (fullName[0] != null)
                                        applicationPojo.setTitle(fullName[0]);
                                    if (fullName[1] != null)
                                        applicationPojo.setName(fullName[1]);

                                } else {
                                    applicationPojo.setName(cell.getStringCellValue());
                                }
                            }
                        }

                        if (index == 2) {
                            if (cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                                if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                    applicationPojo.setJobTitle(cell.getNumericCellValue() + "");
                                } else {
                                    applicationPojo.setJobTitle(cell.getStringCellValue());
                                }
                            }
                        }
//                        if (index == 3) {
//                            if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
//                                applicationPojo.setNationalID(cell.getNumericCellValue() + "");
//                                if (applicationPojo.getNationalID().contains("."))
//                                    applicationPojo.setNationalID(applicationPojo.getNationalID().replace(".", ""));
//                            } else {
//                            }
//
//                        }

                        if (index == 4)
                            applicationPojo.setEmployer(cell.getStringCellValue());

                        if (index == 5) {
                            if (cell.getCellType() == Cell.CELL_TYPE_STRING)
                                applicationPojo.setInvitationNumber(cell.getStringCellValue());
                            else
                                applicationPojo.setInvitationNumber(cell.getNumericCellValue() + "");
                        }

                        if (index == 6)
                            applicationPojo.setZoneColor(cell.getStringCellValue());

                        if (index == 7) {
                            if (cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                                if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                    applicationPojo.setZoneName(cell.getNumericCellValue() + "");
                                } else {
                                    applicationPojo.setZoneName(cell.getStringCellValue());
                                }
                            }
                        }


                        if (index == 8) {
                            if (cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                                if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                    applicationPojo.setRow(cell.getNumericCellValue() + "");
                                } else {
                                    applicationPojo.setRow(cell.getStringCellValue());
                                }
                            }
                        }

                        if (index == 9) {
                            if (cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                                if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                    applicationPojo.setSeat(cell.getNumericCellValue() + "");
                                } else {
                                    applicationPojo.setSeat(cell.getStringCellValue());
                                }
                            }
                        }

                        index++;
                    }
                }
                applicationPojo.setApproved(true);
                applicationPojo.setNationalID(Calendar.getInstance().getTimeInMillis() + "" + row.getRowNum());
                if (applicationPojo.getName() == null)
                    applicationPojo.setName("-----");
                if (applicationPojo.getTitle() == null)
                    applicationPojo.setTitle("----");
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
