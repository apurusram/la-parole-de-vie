import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class BibleLoader {

    public static Bible loadBible(File xmlFile) {
        Bible bible = new Bible();

        try {
            Document document = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(xmlFile);

            document.getDocumentElement().normalize();

            NodeList bookNodes = document.getElementsByTagName("BIBLEBOOK");

            for (int i = 0; i < bookNodes.getLength(); i++) {
                Element bookElement = (Element) bookNodes.item(i);

                int bookNumber = Integer.parseInt(bookElement.getAttribute("bnumber"));
                String bookName = bookElement.getAttribute("bname");
                String bookShortName = bookElement.getAttribute("bsname");

                Book book = new Book(bookNumber, bookName, bookShortName);

                NodeList chapterNodes = bookElement.getElementsByTagName("CHAPTER");
                for (int j = 0; j < chapterNodes.getLength(); j++) {
                    Element chapterElement = (Element) chapterNodes.item(j);

                    int chapterNumber = Integer.parseInt(chapterElement.getAttribute("cnumber"));
                    Chapter chapter = new Chapter(chapterNumber);

                    NodeList verseNodes = chapterElement.getElementsByTagName("VERS");
                    for (int k = 0; k < verseNodes.getLength(); k++) {
                        Element verseElement = (Element) verseNodes.item(k);

                        int verseNumber = Integer.parseInt(verseElement.getAttribute("vnumber"));
                        String verseText = verseElement.getTextContent();

                        Verse verse = new Verse(verseNumber, verseText);
                        chapter.getVerses().add(verse);
                    }

                    book.getChapters().add(chapter);
                }

                bible.getBooks().add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bible;
    }
}
