import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;

// All the data structures and the logic of the system live here.
//
//   BookTree (BST)          -> book inventory: add, remove, search by ID
//   Merge Sort              -> sort books by title, author, or year
//   HashMap<Integer,Member> -> member records (hash table)
//   PriorityQueue<String>   -> waiting list inside each Book (faculty first)
//   LinkedList<String>      -> activity log (addFirst = newest on top)
public class Library {
    BookTree tree = new BookTree();

    HashMap<Integer, Member> members = new HashMap<>();
    LinkedList<String> activityLog = new LinkedList<>();
    int requestCounter = 0;               // keeps first-come-first-served order

    void log(String message) {
        activityLog.addFirst(message);
        if (activityLog.size() > 20) {
            activityLog.removeLast();
        }
    }

    // =========================================================
    //  BOOKS - BINARY SEARCH TREE OPERATIONS
    // =========================================================

    // SEARCH by ID (BST search)
    Book findBook(int id) {
        return tree.search(id);
    }

    // ADD a book (BST insert)
    String addBook(int id, String title, String author, int year) {
        if (id <= 0) {
            return "Book ID must be a positive number.";
        }
        boolean added = tree.insert(new Book(id, title, author, year));
        if (!added) {
            return "A book with ID " + id + " already exists.";
        }
        log("Added book #" + id + " \"" + title + "\"");
        return "Book added: " + title;
    }

    // REMOVE a book (BST delete)
    String removeBook(int id) {
        Book book = tree.search(id);
        if (book == null) {
            return "Book not found.";
        }
        if (!book.available) {
            return "Cannot remove: the book is currently borrowed.";
        }
        tree.delete(id);
        log("Removed book #" + id + " \"" + book.title + "\"");
        return "Book removed: " + book.title;
    }

    // LINEAR SEARCH by exact title (ignores uppercase/lowercase)
    Book searchByTitle(String title) {
        Book[] all = tree.toArray();
        for (int i = 0; i < all.length; i++) {
            if (all[i].title.equalsIgnoreCase(title)) {
                return all[i];
            }
        }
        return null;
    }

    // LINEAR SEARCH by author - one author can have many books
    Book[] searchByAuthor(String author) {
        Book[] all = tree.toArray();
        Book[] found = new Book[all.length];
        int count = 0;
        for (int i = 0; i < all.length; i++) {
            if (all[i].author.equalsIgnoreCase(author)) {
                found[count] = all[i];
                count++;
            }
        }
        Book[] result = new Book[count];
        for (int i = 0; i < count; i++) {
            result[i] = found[i];
        }
        return result;
    }

    // All books in ID order (BST in-order traversal)
    Book[] copyOfBooks() {
        return tree.toArray();
    }

    // =========================================================
    //  SORTING - MERGE SORT
    //  Idea: split the array in half, sort each half (by calling
    //  itself again), then MERGE the two sorted halves together.
    // =========================================================

    static final int BY_TITLE = 1;
    static final int BY_AUTHOR = 2;
    static final int BY_YEAR = 3;

    Book[] sortByTitle() {
        return mergeSort(tree.toArray(), BY_TITLE);
    }

    Book[] sortByAuthor() {
        return mergeSort(tree.toArray(), BY_AUTHOR);
    }

    Book[] sortByYear() {
        return mergeSort(tree.toArray(), BY_YEAR);
    }

    // Negative = a comes first, 0 = same, positive = b comes first
    int compare(Book a, Book b, int mode) {
        if (mode == BY_TITLE) {
            return a.title.compareToIgnoreCase(b.title);
        } else if (mode == BY_AUTHOR) {
            return a.author.compareToIgnoreCase(b.author);
        }
        return a.year - b.year;
    }

    Book[] mergeSort(Book[] arr, int mode) {
        // Base case: 0 or 1 book is already sorted
        if (arr.length <= 1) {
            return arr;
        }

        // 1. Split into a left half and a right half
        int mid = arr.length / 2;
        Book[] left = new Book[mid];
        Book[] right = new Book[arr.length - mid];
        for (int i = 0; i < mid; i++) {
            left[i] = arr[i];
        }
        for (int i = mid; i < arr.length; i++) {
            right[i - mid] = arr[i];
        }

        // 2. Sort each half
        left = mergeSort(left, mode);
        right = mergeSort(right, mode);

        // 3. Merge the two sorted halves
        return merge(left, right, mode);
    }

    Book[] merge(Book[] left, Book[] right, int mode) {
        Book[] result = new Book[left.length + right.length];
        int i = 0;   // position in left
        int j = 0;   // position in right
        int k = 0;   // position in result

        // Take the smaller front item from either half
        while (i < left.length && j < right.length) {
            if (compare(left[i], right[j], mode) <= 0) {
                result[k] = left[i];
                i++;
            } else {
                result[k] = right[j];
                j++;
            }
            k++;
        }
        // One half is finished, copy whatever is left of the other half
        while (i < left.length) {
            result[k] = left[i];
            i++;
            k++;
        }
        while (j < right.length) {
            result[k] = right[j];
            j++;
            k++;
        }
        return result;
    }

    // =========================================================
    //  MEMBERS - HASHMAP OPERATIONS
    // =========================================================

    String addMember(int id, String name, boolean faculty) {
        if (id <= 0) {
            return "Member ID must be a positive number.";
        }
        if (members.containsKey(id)) {
            return "A member with ID " + id + " already exists.";
        }
        members.put(id, new Member(name, faculty));
        log("Registered member #" + id + " " + name);
        return "Member registered: " + name;
    }

    String renameMember(int id, String newName) {
        if (!members.containsKey(id)) {
            return "Member not found.";
        }
        members.get(id).name = newName;
        log("Member #" + id + " renamed to " + newName);
        return "Member updated.";
    }

    String removeMember(int id) {
        if (!members.containsKey(id)) {
            return "Member not found.";
        }
        if (members.get(id).borrowedCount > 0) {
            return "Cannot remove: this member still has borrowed books.";
        }
        String name = members.get(id).name;
        members.remove(id);
        log("Removed member #" + id + " " + name);
        return "Member removed: " + name;
    }

    // =========================================================
    //  BORROW / RETURN / WAITING LIST (PRIORITY QUEUE)
    // =========================================================

    void lend(Book book, Member member, int memberId) {
        book.available = false;
        book.borrowedBy = memberId;
        member.borrowedCount++;
    }

    String borrowBook(int bookId, int memberId) {
        if (!members.containsKey(memberId)) {
            return "Member not found.";
        }
        Book book = findBook(bookId);
        if (book == null) {
            return "Book not found.";
        }
        Member member = members.get(memberId);

        if (!book.available && book.borrowedBy == memberId) {
            return "This member already has that book.";
        }

        if (book.available) {
            if (member.borrowedCount >= member.limit()) {
                return member.name + " reached the limit of " + member.limit() + " books.";
            }
            lend(book, member, memberId);
            log(member.name + " borrowed \"" + book.title + "\"");
            return member.name + " borrowed \"" + book.title + "\".";
        }

        // Book is taken -> join the waiting list
        for (String entry : book.waitList) {
            if (entry.endsWith("-" + memberId)) {
                return member.name + " is already on the waiting list.";
            }
        }
        requestCounter++;
        int priority = 2;                       // 1 = faculty, 2 = student
        if (member.faculty) {
            priority = 1;
        }
        String request = priority + "-" + String.format("%04d", requestCounter) + "-" + memberId;
        book.waitList.offer(request);

        log(member.name + " joined waiting list for \"" + book.title + "\"");
        return "Book is not available. " + member.name + " was added to the waiting list ("
                + member.type() + " priority).";
    }

    String returnBook(int bookId) {
        Book book = findBook(bookId);
        if (book == null) {
            return "Book not found.";
        }
        if (book.available) {
            return "That book is not currently borrowed.";
        }

        Member borrower = members.get(book.borrowedBy);
        if (borrower != null) {
            borrower.borrowedCount--;
        }
        String message = "Book returned: " + book.title + ".";
        log("Returned \"" + book.title + "\"");
        book.available = true;
        book.borrowedBy = 0;

        // Give it to the next person in the waiting list (highest priority first)
        while (!book.waitList.isEmpty()) {
            String next = book.waitList.poll();
            int nextId = Integer.parseInt(next.substring(next.lastIndexOf("-") + 1));
            Member nextMember = members.get(nextId);

            if (nextMember == null) {
                continue;   // member was removed, skip
            }
            if (nextMember.borrowedCount >= nextMember.limit()) {
                message += " (" + nextMember.name + " skipped: at borrow limit.)";
                continue;
            }
            lend(book, nextMember, nextId);
            log("\"" + book.title + "\" auto-lent to " + nextMember.name + " (waiting list)");
            message += " Automatically lent to " + nextMember.name + " from the waiting list.";
            break;
        }
        return message;
    }

    // Show the waiting list in priority order WITHOUT destroying the real one
    // (we poll from a copy)
    void showWaitList(int bookId) {
        Book book = findBook(bookId);
        if (book == null) {
            System.out.println("Book not found.");
            return;
        }
        if (book.waitList.isEmpty()) {
            System.out.println("No one is waiting for \"" + book.title + "\".");
            return;
        }
        PriorityQueue<String> copy = new PriorityQueue<>(book.waitList);
        System.out.println("Waiting list for \"" + book.title + "\":");
        int rank = 1;
        while (!copy.isEmpty()) {
            String entry = copy.poll();
            int memberId = Integer.parseInt(entry.substring(entry.lastIndexOf("-") + 1));
            Member m = members.get(memberId);
            if (m == null) {
                System.out.println("  " + rank + ". (removed member #" + memberId + ")");
            } else {
                System.out.println("  " + rank + ". " + m.name + " [" + m.type() + "]");
            }
            rank++;
        }
    }

    // =========================================================
    //  DISPLAY HELPERS
    // =========================================================

    void displayBooks(Book[] list) {
        if (list.length == 0) {
            System.out.println("(no books to show)");
            return;
        }
        System.out.printf("%-5s %-32s %-22s %-5s %s%n", "ID", "TITLE", "AUTHOR", "YEAR", "STATUS");
        System.out.println("-------------------------------------------------------------------------------");
        for (int i = 0; i < list.length; i++) {
            Book b = list[i];
            String status = "Available";
            if (!b.available) {
                status = "Borrowed by #" + b.borrowedBy;
                if (!b.waitList.isEmpty()) {
                    status += " (" + b.waitList.size() + " waiting)";
                }
            }
            System.out.printf("%-5d %-32s %-22s %-5d %s%n", b.id, b.title, b.author, b.year, status);
        }
    }

    void displayMembers() {
        if (members.isEmpty()) {
            System.out.println("(no members registered)");
            return;
        }
        System.out.printf("%-6s %-22s %-8s %s%n", "ID", "NAME", "TYPE", "BOOKS HELD");
        System.out.println("-------------------------------------------------");
        for (Map.Entry<Integer, Member> entry : members.entrySet()) {
            Member m = entry.getValue();
            System.out.printf("%-6d %-22s %-8s %d/%d%n", entry.getKey(), m.name, m.type(),
                    m.borrowedCount, m.limit());
        }
    }

    void displayLog() {
        if (activityLog.isEmpty()) {
            System.out.println("(no activity yet)");
            return;
        }
        System.out.println("Recent activity (newest first):");
        for (String line : activityLog) {
            System.out.println("  - " + line);
        }
    }
}
