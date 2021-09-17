package technology.tabula.extractors;

import java.util.List;

import technology.tabula.PageArea;
import technology.tabula.Table;

public interface ExtractionAlgorithm {

    List<? extends Table> extract(PageArea pageArea);
    String toString();
    
}
