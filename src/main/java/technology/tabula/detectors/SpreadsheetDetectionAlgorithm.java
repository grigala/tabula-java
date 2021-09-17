package technology.tabula.detectors;

import java.util.Collections;
import java.util.List;

import technology.tabula.Cell;
import technology.tabula.PageArea;
import technology.tabula.Rectangle;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

/**
 * Created by matt on 2015-12-14.
 *
 * This is the basic spreadsheet table detection algorithm currently implemented in tabula (web).
 *
 * It uses intersecting ruling lines to find tables.
 */
public class SpreadsheetDetectionAlgorithm implements DetectionAlgorithm {
    @Override
    public List<Rectangle> detect(PageArea pageArea) {
        List<Cell> cells = SpreadsheetExtractionAlgorithm.findCells(pageArea.getHorizontalRulings(), pageArea.getVerticalRulings());

        SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();

        List<Rectangle> tables = SpreadsheetExtractionAlgorithm.findSpreadsheetsFromCells(cells);

        // we want tables to be returned from top to bottom on the page
        Collections.sort(tables, Rectangle.ILL_DEFINED_ORDER);

        return tables;
    }
}
