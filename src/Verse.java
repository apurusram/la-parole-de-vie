public class Verse {
    private int number;
    private String text;

    // Constructor
    public Verse(int number, String text) {
        this.number = number;
        this.text = text;
    }

    public int getNumber() {
        return number;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return String.valueOf(number); // Show verse number only
    }

}
