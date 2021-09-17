package technology.tabula.detectors;

import java.util.List;

import technology.tabula.PageArea;
import technology.tabula.Rectangle;

/**
 * Created by matt on 2015-12-14.
 */
public interface DetectionAlgorithm {
    List<Rectangle> detect(PageArea pageArea);
}
