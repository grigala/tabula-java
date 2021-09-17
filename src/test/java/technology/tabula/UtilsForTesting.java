package technology.tabula;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.Assert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

public class UtilsForTesting {

    public static PageArea getAreaFromFirstPage(String path, float top, float left, float bottom, float right) throws IOException {
        return getAreaFromPage(path, 1, top, left, bottom, right);
    }

    public static PageArea getAreaFromPage(String path, int page, float top, float left, float bottom, float right) throws IOException {
        return getPage(path, page).getArea(top, left, bottom, right);
    }

    public static PageArea getPage(String path, int pageNumber) throws IOException {
        TextExtractor oe = null;
        try {
            PDDocument document = PDDocument
                    .load(new File(path));
            oe = new TextExtractor(document);
            PageArea pageArea = oe.extract(pageNumber);
            return pageArea;
        } finally {
            if (oe != null)
                oe.close();
        }
    }

    public static String[][] tableToArrayOfRows(Table table) {
        List<List<RectangularTextContainer>> tableRows = table.getRows();

        int maxColCount = 0;

        for (int i = 0; i < tableRows.size(); i++) {
            List<RectangularTextContainer> row = tableRows.get(i);
            if (maxColCount < row.size()) {
                maxColCount = row.size();
            }
        }
        
        Assert.assertEquals(maxColCount, table.getColCount());
        
        String[][] rv = new String[tableRows.size()][maxColCount];

        for (int i = 0; i < tableRows.size(); i++) {
            List<RectangularTextContainer> row = tableRows.get(i);
            for (int j = 0; j < row.size(); j++) {
                rv[i][j] = table.getCell(i, j).getText();
            }
        }

        return rv;
    }

    public static String loadJson(String path) throws IOException {
 
	    	StringBuilder stringBuilder = new StringBuilder();
	    try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"))) {
	        String line = null;
	        while ((line = reader.readLine()) != null) {
	            stringBuilder.append(line);
	        }
	    }

        return stringBuilder.toString();

    }

    public static String loadCsv(String path) throws IOException {

        StringBuilder out = new StringBuilder();
        CSVParser parse = org.apache.commons.csv.CSVParser.parse(new File(path), Charset.forName("utf-8"), CSVFormat.EXCEL);

        CSVPrinter printer = new CSVPrinter(out, CSVFormat.EXCEL);
        printer.printRecords(parse);
        printer.close();

        String csv = out.toString().replaceAll("(?<!\r)\n", "\r");
        return csv;

    }


}
