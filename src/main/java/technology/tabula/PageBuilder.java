package technology.tabula;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import java.util.List;

public class PageBuilder {
    private PageDims pageDims;
    private int rotation;
    private int number;
    private PDPage pdPage;
    private PDDocument pdDocument;
    private List<TextElement> textElements;
    private List<Ruling> rulings;
    private float minCharWidth;
    private float minCharHeight;
    private RectangleSpatialIndex<TextElement> index;

    private PageBuilder() {}

    public static PageBuilder newInstance() {
        return new PageBuilder();
    }

    public PageBuilder withPageDims(PageDims pageDims) {
        this.pageDims = pageDims;

        return this;
    }

    public PageBuilder withRotation(int rotation) {
        this.rotation = rotation;

        return this;
    }

    public PageBuilder withNumber(int number) {
        this.number = number;

        return this;
    }

    public PageBuilder withPdPage(PDPage pdPage) {
        this.pdPage = pdPage;

        return this;
    }

    public PageBuilder withPdDocument(PDDocument pdDocument) {
        this.pdDocument = pdDocument;

        return this;
    }

    public PageBuilder withTextElements(List<TextElement> textElements) {
        this.textElements = textElements;

        return this;
    }

    public PageBuilder withRulings(List<Ruling> rulings) {
        this.rulings = rulings;

        return this;
    }

    public PageBuilder withMinCharWidth(float minCharWidth) {
        this.minCharWidth = minCharWidth;

        return this;
    }

    public PageBuilder withMinCharHeight(float minCharHeight) {
        this.minCharHeight = minCharHeight;

        return this;
    }

    public PageBuilder withIndex(RectangleSpatialIndex<TextElement> index) {
        this.index = index;

        return this;
    }

    public PageArea build() {
        return new PageArea(pageDims, rotation, number, pdPage, pdDocument, textElements, rulings, minCharWidth, minCharHeight, index);
    }
}
