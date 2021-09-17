package technology.tabula;

import java.io.IOException;
import java.util.Iterator;

public class PageIterator implements Iterator<PageArea> {

    private TextExtractor textExtractor;
    private Iterator<Integer> pageIndexIterator;

    public PageIterator(TextExtractor textExtractor, Iterable<Integer> pages) {
        super();
        this.textExtractor = textExtractor;
        this.pageIndexIterator = pages.iterator();
    }

    @Override
    public boolean hasNext() {
        return pageIndexIterator.hasNext();
    }

    @Override
    public PageArea next() {
        PageArea nextPageArea = null;
        if (!this.hasNext()) {
            throw new IllegalStateException();
        }
        try {
            nextPageArea = textExtractor.extractPage(pageIndexIterator.next());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return nextPageArea;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
