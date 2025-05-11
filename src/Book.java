import java.util.ArrayList;
import java.util.List;

public class Book {
    private int number;
    private String name;
    private String shortName;
    private List<Chapter> chapters = new ArrayList<>();

    // Constructor
    public Book(int number, String name, String shortName) {
        this.number = number;
        this.name = name;
        this.shortName = shortName;
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public List<Chapter> getChapters() {
        return chapters;
    }

    @Override
    public String toString() {
        return name; // Show book name
    }

}
