package com.example.soh.Utils;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.example.soh.Models.Cashier;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class utilsExcel {
    private static Context context;

    public utilsExcel(Context context) {
        this.context = context;
    }
    public static void exportToExcel(List<Cashier> cashierList) {
        // Create a new Excel workbook and sheet
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Cashier List");

        // Create header row
        HSSFRow headerRow = sheet.createRow(0);
        HSSFCell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("Cashier Name");
        HSSFCell headerCellPrice = headerRow.createCell(1);
        headerCellPrice.setCellValue("Cashier price");
        HSSFCell headerCellQuantity = headerRow.createCell(2);
        headerCellQuantity.setCellValue("Cashier quantity");

        // Populate data
        for (int i = 0; i < cashierList.size(); i++) {
            HSSFRow row = sheet.createRow(i + 1);
            HSSFCell cell = row.createCell(0);
            cell.setCellValue(cashierList.get(i).getProductName());

            HSSFCell cellPrice = row.createCell(1);
            cellPrice.setCellValue(cashierList.get(i).getProductPrice());

            HSSFCell cellQuantity = row.createCell(2);
            cellQuantity.setCellValue(cashierList.get(i).getProductQuantity());
            // Nếu có thêm thông tin khác của Cashier, bạn có thể thêm cột và set giá trị tương ứng
        }

        // Add a summary row at the end
//        int lastRowIndex = cashierList.size() + 1;
//        HSSFRow summaryRow = sheet.createRow(lastRowIndex);
//        HSSFCell summaryCellLabel = summaryRow.createCell(0);
//        summaryCellLabel.setCellValue("Summary");
//
//        HSSFCell summaryCellPrice = summaryRow.createCell(1);
//        summaryCellPrice.setCellValue(totalPrice);
//
//        HSSFCell summaryCellTotalQuantity = summaryRow.createCell(2);
//        summaryCellTotalQuantity.setCellValue(totalQuantity);



        // Save the workbook to a file
        try {
            File downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(downloadsDirectory, "cashier_list.xls");
            FileOutputStream outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            outputStream.close();
            Toast.makeText(context,"Cashier list exported to " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context,"Error exporting cashier list", Toast.LENGTH_SHORT).show();
        }
    }
}
