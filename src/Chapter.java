import java.util.ArrayList;
import java.util.List;

public class Chapter {
    private int number;
    private List<Verse> verses = new ArrayList<>();

    // Constructor
    public Chapter(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public List<Verse> getVerses() {
        return verses;
    }

    @Override
    public String toString() {
        return "Chapter " + number;
    }

}
