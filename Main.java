import java.util.Scanner;

// The command-line menu. No GUI - everything runs in the terminal.
public class Main {
    static Scanner input = new Scanner(System.in);
    static Library lib = new Library();

    // ---------- safe input helpers (so typing letters does not crash the program) ----------
    static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String text = input.nextLine().trim();
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    static String readText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String text = input.nextLine().trim();
            if (!text.isEmpty()) {
                return text;
            }
            System.out.println("This cannot be empty.");
        }
    }

    // ---------- sample data so the demo is quick ----------
    static void loadSampleData() {
        lib.addBook(103, "Clean Code", "Robert Martin", 2008);
        lib.addBook(101, "Java: The Complete Reference", "Herbert Schildt", 2021);
        lib.addBook(105, "Introduction to Algorithms", "Thomas Cormen", 2009);
        lib.addBook(102, "Data Structures in Java", "Robert Lafore", 2002);
        lib.addBook(104, "Head First Java", "Kathy Sierra", 2005);
        lib.addMember(1001, "Dr. Reyes", true);
        lib.addMember(2001, "Ana Santos", false);
        lib.addMember(2002, "Ben Cruz", false);
        lib.activityLog.clear();
    }

    public static void main(String[] args) {
        System.out.println("=====================================");
        System.out.println("   CLI LIBRARY MANAGEMENT SYSTEM");
        System.out.println("=====================================");
        String load = readText("Load sample data for testing? (y/n): ");
        if (load.equalsIgnoreCase("y")) {
            loadSampleData();
            System.out.println("Sample books and members loaded.");
        }

        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("========== MAIN MENU ==========");
            System.out.println("1. Books");
            System.out.println("2. Members");
            System.out.println("3. Borrow / Return / Waiting List");
            System.out.println("4. Recent Activity");
            System.out.println("0. Exit");
            int choice = readInt("Choose: ");

            if (choice == 1) {
                bookMenu();
            } else if (choice == 2) {
                memberMenu();
            } else if (choice == 3) {
                circulationMenu();
            } else if (choice == 4) {
                lib.displayLog();
            } else if (choice == 0) {
                running = false;
            } else {
                System.out.println("Invalid choice.");
            }
        }
        System.out.println("Goodbye!");
        input.close();
    }

    // ================= BOOK MENU =================
    static void bookMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("---------- BOOKS ----------");
            System.out.println("1. Add book");
            System.out.println("2. Remove book");
            System.out.println("3. Search by ID");
            System.out.println("4. Search by title");
            System.out.println("5. Search by author");
            System.out.println("6. Show all books");
            System.out.println("7. Sort by title");
            System.out.println("8. Sort by author");
            System.out.println("9. Sort by year");
            System.out.println("0. Back");
            int choice = readInt("Choose: ");

            if (choice == 1) {
                int id = readInt("Book ID: ");
                String title = readText("Title: ");
                String author = readText("Author: ");
                int year = readInt("Year published: ");
                System.out.println(lib.addBook(id, title, author, year));
            } else if (choice == 2) {
                int id = readInt("Book ID to remove: ");
                System.out.println(lib.removeBook(id));
            } else if (choice == 3) {
                int id = readInt("Book ID to find: ");
                showOne(lib.findBook(id));
            } else if (choice == 4) {
                String title = readText("Exact title: ");
                showOne(lib.searchByTitle(title));
            } else if (choice == 5) {
                String author = readText("Author name: ");
                lib.displayBooks(lib.searchByAuthor(author));
            } else if (choice == 6) {
                lib.displayBooks(lib.copyOfBooks());
            } else if (choice == 7) {
                lib.displayBooks(lib.sortByTitle());
            } else if (choice == 8) {
                lib.displayBooks(lib.sortByAuthor());
            } else if (choice == 9) {
                lib.displayBooks(lib.sortByYear());
            } else if (choice == 0) {
                back = true;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    static void showOne(Book found) {
        if (found == null) {
            System.out.println("Book not found.");
        } else {
            Book[] one = { found };
            lib.displayBooks(one);
        }
    }

    // ================= MEMBER MENU =================
    static void memberMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("---------- MEMBERS ----------");
            System.out.println("1. Register member");
            System.out.println("2. Find member by ID");
            System.out.println("3. Rename member");
            System.out.println("4. Remove member");
            System.out.println("5. Show all members");
            System.out.println("0. Back");
            int choice = readInt("Choose: ");

            if (choice == 1) {
                int id = readInt("Member ID: ");
                String name = readText("Name: ");
                String type = readText("Type (F = Faculty, S = Student): ");
                boolean faculty = type.equalsIgnoreCase("F");
                System.out.println(lib.addMember(id, name, faculty));
            } else if (choice == 2) {
                int id = readInt("Member ID: ");
                if (lib.members.containsKey(id)) {
                    Member m = lib.members.get(id);
                    System.out.println(id + " | " + m.name + " | " + m.type()
                            + " | holding " + m.borrowedCount + "/" + m.limit());
                } else {
                    System.out.println("Member not found.");
                }
            } else if (choice == 3) {
                int id = readInt("Member ID: ");
                if (lib.members.containsKey(id)) {
                    String name = readText("New name: ");
                    System.out.println(lib.renameMember(id, name));
                } else {
                    System.out.println("Member not found.");
                }
            } else if (choice == 4) {
                int id = readInt("Member ID to remove: ");
                System.out.println(lib.removeMember(id));
            } else if (choice == 5) {
                lib.displayMembers();
            } else if (choice == 0) {
                back = true;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    // ================= CIRCULATION MENU =================
    static void circulationMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("---------- BORROW / RETURN ----------");
            System.out.println("1. Borrow a book (or join waiting list)");
            System.out.println("2. Return a book");
            System.out.println("3. View waiting list of a book");
            System.out.println("0. Back");
            int choice = readInt("Choose: ");

            if (choice == 1) {
                int memberId = readInt("Member ID: ");
                int bookId = readInt("Book ID: ");
                System.out.println(lib.borrowBook(bookId, memberId));
            } else if (choice == 2) {
                int bookId = readInt("Book ID being returned: ");
                System.out.println(lib.returnBook(bookId));
            } else if (choice == 3) {
                int bookId = readInt("Book ID: ");
                lib.showWaitList(bookId);
            } else if (choice == 0) {
                back = true;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }
}
