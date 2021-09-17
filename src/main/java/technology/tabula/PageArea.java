package technology.tabula;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Float.compare;
import static java.util.Collections.min;

@SuppressWarnings("serial")
public class PageArea extends Rectangle {

    private int number;
    private Integer rotation;
    private float minCharWidth;
    private float minCharHeight;

    private List<TextElement> textElements;

    // TODO: Create a class for 'List <Ruling>' that encapsulates all of these lists and their behaviors?
    private List<Ruling> rulings,
            cleanRulings = null,
            verticalRulingLines = null,
            horizontalRulingLines = null;

    private PDPage pdPage;
    private PDDocument pdDoc;

    private RectangleSpatialIndex<TextElement> spatialIndex;

    private static final float DEFAULT_MIN_CHAR_LENGTH = 7;

    PageArea(
            PageDims pageDims,
            int rotation,
            int number,
            PDPage pdPage,
            PDDocument doc,
            List<TextElement> characters,
            List<Ruling> rulings,
            float minCharWidth,
            float minCharHeight,
            RectangleSpatialIndex<TextElement> index
            ) {
        super(pageDims.getTop(), pageDims.getLeft(), pageDims.getWidth(), pageDims.getHeight());
        this.rotation = rotation;
        this.number = number;
        this.pdPage = pdPage;
        this.pdDoc = doc;
        this.textElements = characters;
        this.rulings = rulings;
        this.minCharWidth = minCharWidth;
        this.minCharHeight = minCharHeight;
        this.spatialIndex = index;
    }

    public PageArea getArea(Rectangle area) {
        List<TextElement> areaTextElements = getText(area);

        float minimumCharWidth = getMinimumCharWidthFrom(areaTextElements);
        float minimumCharHeight = getMinimumCharHeightFrom(areaTextElements);

        final PageArea pageArea = PageArea.Builder.newInstance()
                .withPageDims(PageDims.of(area.getTop(), area.getLeft(), (float) area.getWidth(), (float) area.getHeight()))
                .withRotation(rotation)
                .withNumber(number)
                .withPdPage(pdPage)
                .withPdDocument(pdDoc)
                .withTextElements(areaTextElements)
                .withRulings(Ruling.cropRulingsToArea(getRulings(), area))
                .withMinCharWidth(minimumCharWidth)
                .withMinCharHeight(minimumCharHeight)
                .withIndex(spatialIndex)
                .build();

        addBorderRulingsTo(pageArea);

        return pageArea;
    }

    private float getMinimumCharWidthFrom(List<TextElement> areaTextElements) {
        if (!areaTextElements.isEmpty()) {
            return min(areaTextElements, (te1, te2) -> compare(te1.width, te2.width)).width;
        }
        return DEFAULT_MIN_CHAR_LENGTH;
    }

    private float getMinimumCharHeightFrom(List<TextElement> areaTextElements) {
        if (!areaTextElements.isEmpty()) {
            return min(areaTextElements, (te1, te2) -> compare(te1.height, te2.height)).height;
        }
        return DEFAULT_MIN_CHAR_LENGTH;
    }

    private void addBorderRulingsTo(PageArea pageArea) {
        Point2D.Double leftTop = new Point2D.Double(pageArea.getLeft(), pageArea.getTop()),
                rightTop = new Point2D.Double(pageArea.getRight(), pageArea.getTop()),
                rightBottom = new Point2D.Double(pageArea.getRight(), pageArea.getBottom()),
                leftBottom = new Point2D.Double(pageArea.getLeft(), pageArea.getBottom());
        pageArea.addRuling(new Ruling(leftTop, rightTop));
        pageArea.addRuling(new Ruling(rightTop, rightBottom));
        pageArea.addRuling(new Ruling(rightBottom, leftBottom));
        pageArea.addRuling(new Ruling(leftBottom, leftTop));
    }

    public PageArea getArea(float top, float left, float bottom, float right) {
        Rectangle area = new Rectangle(top, left, right - left, bottom - top);
        return getArea(area);
    }

    public Integer getRotation() {
        return rotation;
    }

    public int getPageNumber() {
        return number;
    }


    public List<TextElement> getText() {
        return textElements;
    }

    public List<TextElement> getText(Rectangle area) {
        return spatialIndex.contains(area);
    }

    /**
     * Returns the minimum bounding box that contains all the TextElements on this Page
     */
    public Rectangle getTextBounds() {
        List<TextElement> texts = this.getText();
        if (!texts.isEmpty()) {
            return Utils.bounds(texts);
        } else {
            return new Rectangle();
        }
    }


    public List<Ruling> getRulings() {
        if (cleanRulings != null) {
            return cleanRulings;
        }

        if (rulings == null || rulings.isEmpty()) {
            verticalRulingLines = new ArrayList<>();
            horizontalRulingLines = new ArrayList<>();
            return new ArrayList<>();
        }

        // TODO: Move as a static method to the Ruling class?
        Utils.snapPoints(rulings, minCharWidth, minCharHeight);

        verticalRulingLines = getCollapsedVerticalRulings();
        horizontalRulingLines = getCollapsedHorizontalRulings();

        cleanRulings = new ArrayList<>(verticalRulingLines);
        cleanRulings.addAll(horizontalRulingLines);

        return cleanRulings;
    }

    // TODO: Create a class for 'List <Ruling>' and encapsulate these behaviors within it?
    private List<Ruling> getCollapsedVerticalRulings() {
        List<Ruling> verticalRulings = new ArrayList<>();
        for (Ruling ruling : rulings) {
            if (ruling.vertical()) {
                verticalRulings.add(ruling);
            }
        }
        return Ruling.collapseOrientedRulings(verticalRulings);
    }

    private List<Ruling> getCollapsedHorizontalRulings() {
        List<Ruling> horizontalRulings = new ArrayList<>();
        for (Ruling ruling : rulings) {
            if (ruling.horizontal()) {
                horizontalRulings.add(ruling);
            }
        }
        return Ruling.collapseOrientedRulings(horizontalRulings);
    }

    public List<Ruling> getVerticalRulings() {
        if (verticalRulingLines != null) {
            return verticalRulingLines;
        }
        getRulings();
        return verticalRulingLines;
    }

    public List<Ruling> getHorizontalRulings() {
        if (horizontalRulingLines != null) {
            return horizontalRulingLines;
        }
        getRulings();
        return horizontalRulingLines;
    }

    public void addRuling(Ruling ruling) {
        if (ruling.oblique()) {
            throw new UnsupportedOperationException("Can't add an oblique ruling.");
        }
        rulings.add(ruling);
        // Clear caches:
        verticalRulingLines = null;
        horizontalRulingLines = null;
        cleanRulings = null;
    }

    public List<Ruling> getUnprocessedRulings() {
        return rulings;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //
    public PDPage getPDPage() {
        return pdPage;
    }

    public PDDocument getPDDoc() {
        return pdDoc;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //

    /**
     * @deprecated with no replacement
     */
    @Deprecated
    public RectangleSpatialIndex<TextElement> getSpatialIndex() {
        return spatialIndex;
    }
}
