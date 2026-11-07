// One library member. Stored as the VALUE in a HashMap (key = member ID).
public class Member {
    String name;
    boolean faculty;        // true = faculty, false = student
    int borrowedCount = 0;  // how many books they currently hold

    Member(String name, boolean faculty) {
        this.name = name;
        this.faculty = faculty;
    }

    String type() {
        if (faculty) {
            return "Faculty";
        }
        return "Student";
    }

    // Faculty may borrow more books than students
    int limit() {
        if (faculty) {
            return 5;
        }
        return 3;
    }
}
