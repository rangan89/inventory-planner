package fk.retail.ip.requirement.internal.poi;

import fk.retail.ip.core.enums.RequirementExcelHeaders;
import fk.retail.ip.core.poi.SpreadSheetWriter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Created by nidhigupta.m on 14/03/17.
 */
public class RequirementSpreadSheetWriter extends SpreadSheetWriter {

    protected void applyCellStyle(XSSFWorkbook wb, Cell cell, String columnName) {
        CellStyle editableStyle = wb.createCellStyle();
        editableStyle.setLocked(false);
        cell.setCellStyle(editableStyle);
        CellStyle uneditableStyle = wb.createCellStyle();
        uneditableStyle.setLocked(true);
        if (RequirementExcelHeaders.getLockedHeaders().contains(RequirementExcelHeaders.fromString(columnName))) {
            cell.setCellStyle(uneditableStyle);
        } else {
            cell.setCellStyle(editableStyle);
        }
    }
}
