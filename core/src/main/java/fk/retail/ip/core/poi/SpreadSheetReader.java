package fk.retail.ip.core.poi;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author pragalathan.m
 */
public class SpreadSheetReader {

    //    private final DecimalFormat formatter = new DecimalFormat("#.###");
    public List<Map<String, Object>> read(InputStream xlsxFile) throws InvalidFormatException, IOException {
        List<Map<String, Object>> rows = new ArrayList<>();
        try (OPCPackage pkg = OPCPackage.open(xlsxFile)) {

            XSSFWorkbook wb = new XSSFWorkbook(pkg);
            Sheet sheet = wb.getSheetAt(0);
//            FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();

            int maxRows = sheet.getLastRowNum() + 1;
            List<String> headers = new ArrayList<>();
            Row headerRow = sheet.getRow(0);
            for (int c = 0; c < headerRow.getLastCellNum(); c++) {
                Cell cell = headerRow.getCell(c);
                if (cell == null) {
                    continue;
                }
                headers.add(cell.getStringCellValue());
            }

            for (int r = 1; r < maxRows; r++) {
                Map<String, Object> values = new HashMap<>();
                Row row = sheet.getRow(r);
                boolean blankRow = true;
                for (int c = 0; c < headers.size(); c++) {
                    Cell cell = row.getCell(c);
                    if (cell == null) {
                        // blank cells at the end of the row will be treated as null
                        continue;
                    }
                    DataFormatter formatter = new DataFormatter();

                    if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        String value = formatter.formatCellValue(cell);
                        try {
                            values.put(headers.get(c), Long.parseLong(value));
                        } catch (NumberFormatException ex) {
                            values.put(headers.get(c), Double.parseDouble(value));
                        }
                    } else {
                        values.put(headers.get(c), cell.getStringCellValue());
                    }

                    if (blankRow) {
                        if (values.get(headers.get(c)) instanceof String) {
                            blankRow = ((String) values.get(headers.get(c))).isEmpty();
                        } else {
                            blankRow = false;
                        }
                    }
                }
                if (!blankRow) {
                    rows.add(values);
//                    writer.printRecord(values); // write to out file
                }
            }
        }
        return rows;
    }
}
