import java.util.PriorityQueue;

// One book in the library. Stored inside the books[] array of Library.
public class Book {
    int id;
    String title;
    String author;
    int year;
    boolean available = true;   // true = on the shelf
    int borrowedBy = 0;         // member ID who has it (0 = nobody)

    // Waiting list for this book. Strings sort alphabetically, so
    // "1-0001-101" (faculty) always comes before "2-0001-205" (student).
    PriorityQueue<String> waitList = new PriorityQueue<>();

    Book(int id, String title, String author, int year) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
    }
}
