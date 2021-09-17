package technology.tabula;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestTextExtractor {

    /*@Test(expected=IOException.class)
    public void testWrongPasswordRaisesException() throws IOException {
        PDDocument pdf_document = PDDocument.load(new File("src/test/resources/technology/tabula/encrypted.pdf"));
        ObjectExtractor oe = new ObjectExtractor(pdf_document, "wrongpass"); 
        oe.extract().next();
    }*/

    @Test(expected = IOException.class)
    public void testEmptyOnEncryptedFileRaisesException() throws IOException {
        PDDocument pdf_document = PDDocument.load(new File("src/test/resources/technology/tabula/encrypted.pdf"));
        try (TextExtractor oe = new TextExtractor(pdf_document)) {
          oe.extract().next();
        }
    }

    @Test
    public void testCanReadPDFWithOwnerEncryption() throws IOException {
        PDDocument pdf_document = PDDocument.load(new File("src/test/resources/technology/tabula/S2MNCEbirdisland.pdf"));
        try (TextExtractor oe = new TextExtractor(pdf_document)) {
          PageIterator pi = oe.extract();
          int i = 0;
          while (pi.hasNext()) {
            i++;
            pi.next();
          }
          assertEquals(2, i);
        }
    }
    

    @Test
    public void testGoodPassword() throws IOException {
        PDDocument pdf_document = PDDocument.load(new File("src/test/resources/technology/tabula/encrypted.pdf"), "userpassword");
        try (TextExtractor oe = new TextExtractor(pdf_document)) {
          List<PageArea> pageAreas = new ArrayList<>();
          PageIterator pi = oe.extract();
          while (pi.hasNext()) {
            pageAreas.add(pi.next());
          }
          assertEquals(1, pageAreas.size());
        }
    }


    @Test
    public void testTextExtractionDoesNotRaise() throws IOException {
        PDDocument pdf_document = PDDocument.load(new File("src/test/resources/technology/tabula/rotated_page.pdf"));
        try (TextExtractor oe = new TextExtractor(pdf_document)) {
          PageIterator pi = oe.extract();

          assertTrue(pi.hasNext());
          assertNotNull(pi.next());
          assertFalse(pi.hasNext());
        }
    }

    @Test
    public void testShouldDetectRulings() throws IOException {
        PDDocument pdf_document = PDDocument.load(new File("src/test/resources/technology/tabula/should_detect_rulings.pdf"));
        try (TextExtractor oe = new TextExtractor(pdf_document)) {
          PageIterator pi = oe.extract();

          PageArea pageArea = pi.next();
          List<Ruling> rulings = pageArea.getRulings();

          for (Ruling r: rulings) {
            assertTrue(pageArea.contains(r.getBounds()));
          }
        }
    }

    @Test
    public void testDontThrowNPEInShfill() throws IOException {
        PDDocument pdf_document = PDDocument.load(new File("src/test/resources/technology/tabula/labor.pdf"));

        try (TextExtractor oe = new TextExtractor(pdf_document)) {
          PageIterator pi = oe.extract();
          assertTrue(pi.hasNext());
          try {
            PageArea p = pi.next();
            assertNotNull(p);
          } catch (NullPointerException e) {
            fail("NPE in ObjectExtractor " + e.toString());
          }
        }
    }

    @Test
    public void testExtractOnePage() throws IOException {
        PDDocument pdf_document = PDDocument.load(new File("src/test/resources/technology/tabula/S2MNCEbirdisland.pdf"));
        assertEquals(2, pdf_document.getNumberOfPages());

        try (TextExtractor oe = new TextExtractor(pdf_document)) {
          PageArea pageArea = oe.extract(2);

          assertNotNull(pageArea);
        }

    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testExtractWrongPageNumber() throws IOException {
        PDDocument pdf_document = PDDocument.load(new File("src/test/resources/technology/tabula/S2MNCEbirdisland.pdf"));
        assertEquals(2, pdf_document.getNumberOfPages());

        try (TextExtractor oe = new TextExtractor(pdf_document)) {
          oe.extract(3);
        }
    }

    @Test
    public void testTextElementsContainedInPage() throws IOException {
        PDDocument pdf_document = PDDocument.load(new File("src/test/resources/technology/tabula/cs-en-us-pbms.pdf"));

        try (TextExtractor oe = new TextExtractor(pdf_document)) {
          PageArea pageArea = oe.extractPage(1);

          for (TextElement te: pageArea.getText()) {
            assertTrue(pageArea.contains(te));
          }
        }

    }

    @Test public void testDoNotNPEInPointComparator() throws IOException {
        PDDocument pdf_document = PDDocument.load(new File("src/test/resources/technology/tabula/npe_issue_206.pdf"));

        try (TextExtractor oe = new TextExtractor(pdf_document)) {
            PageArea p = oe.extractPage(1);
            assertNotNull(p);
        } catch (NullPointerException e) {
            fail("NPE in ObjectExtractor " + e.toString());
        }
    }
}
