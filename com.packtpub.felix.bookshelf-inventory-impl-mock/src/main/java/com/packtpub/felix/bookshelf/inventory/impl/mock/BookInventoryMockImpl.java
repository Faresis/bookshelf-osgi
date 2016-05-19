package com.packtpub.felix.bookshelf.inventory.impl.mock;

import com.packtpub.felix.bookshelf.inventory.api.*;

import java.util.*;

public class BookInventoryMockImpl implements BookInventory {

    public static final String DEFAULT_CATEGORY = "default";

    private Map<String, MutableBook> booksByIsbn = new HashMap<String, MutableBook>();
    private Map<String, Integer> categories = new HashMap<String, Integer>();

    public Set<String> getCategories() {
        return new HashSet<String>(this.categories.keySet());
    }

    public MutableBook createBook(final String isbn) throws BookAlreadyExistsException {
        if (booksByIsbn.containsKey(isbn)) {
            throw new BookAlreadyExistsException();
        }
        return new MutableBookImpl(isbn);
    }

    public String storeBook(final MutableBook book) throws InvalidBookException {
        if (book.getIsbn() == null) {
            throw new InvalidBookException();
        }

        booksByIsbn.put(book.getIsbn(), book);

        String category = Objects.toString(book.getCategory(), DEFAULT_CATEGORY);
        Integer count = categories.containsKey(category) ? categories.get(category) : Integer.valueOf(0);
        categories.put(category, ++count);
        return book.getIsbn();
    }

    public Book loadBook(String isbn) throws BookNotFoundException {
        return loadBookForEdit(isbn);
    }

    public MutableBook loadBookForEdit(final String isbn) throws BookNotFoundException {
        if (!booksByIsbn.containsKey(isbn)) {
            throw new BookNotFoundException();
        }
        return booksByIsbn.get(isbn);
    }

    public void removeBook(String isbn) throws BookNotFoundException {
        if (!booksByIsbn.containsKey(isbn)) {
            throw new BookNotFoundException();
        }
        Book book = booksByIsbn.remove(isbn);
        String category = Objects.toString(book.getCategory(), DEFAULT_CATEGORY);
        categories.put(category, categories.get(category) - 1);
    }

    //TODO: investigate how third party dependencies should be exposed through a bundle
    public Set<String> searchBooks(final Map<SearchCriteria, String> criteria) {
        LinkedList<Book> books = new LinkedList<Book>(booksByIsbn.values());

        for(Map.Entry<SearchCriteria, String> criterion : criteria.entrySet()) {
            for (Iterator<Book> i = books.iterator(); i.hasNext();) {
                Book book = i.next();

                switch (criterion.getKey()) {
                    case AUTHOR_LIKE:
                        if (!checkStringMatch(book.getAuthor(), criterion.getValue())) {
                            i.remove();
                            continue;
                        }
                        break;
                    case ISBN_LIKE:
                        if (!checkStringMatch(book.getIsbn(), criterion.getValue())) {
                            i.remove();
                            continue;
                        }
                        break;
                    case CATEGORY_LIKE:
                        if (!checkStringMatch(book.getCategory(), criterion.getValue())) {
                            i.remove();
                            continue;
                        }
                        break;
                    case TITLE_LIKE:
                        if (!checkStringMatch(book.getTitle(), criterion.getValue())) {
                            i.remove();
                            continue;
                        }
                        break;
                    case GRADE_GT:
                        if (!checkIntegerGreater(book.getRating(), criterion.getValue())) {
                            i.remove();
                            continue;
                        }
                        break;
                    case GRADE_LT:
                        if (!checkIntegerSmaller(book.getRating(), criterion.getValue())) {
                            i.remove();
                            continue;
                        }
                        break;
                }
            }
        }

        HashSet<String> isbns = new HashSet<String>();
        for(Book book : books) {
            isbns.add(book.getIsbn());
        }
        return isbns;

        // Temporarily commented because can't satisfy guava dependency for the bundle
        /*
        return ImmutableSet.copyOf(transform(filter(ImmutableList.copyOf(booksByIsbn.values()), new Predicate<MutableBook>() {
            public boolean apply(MutableBook book) {
                for (Map.Entry<SearchCriteria, String> criterion : criteria.entrySet()) {
                    switch (criterion.getKey()) {
                        case AUTHOR_LIKE:
                            if (!checkStringMatch(book.getAuthor(), criterion.getValue())) {
                                return false;
                            }
                            break;
                        case ISBN_LIKE:
                            if (!checkStringMatch(book.getIsbn(), criterion.getValue())) {
                                return false;
                            }
                            break;
                        case CATEGORY_LIKE:
                            if (!checkStringMatch(book.getCategory(), criterion.getValue())) {
                                return false;
                            }
                            break;
                        case TITLE_LIKE:
                            if (!checkStringMatch(book.getTitle(), criterion.getValue())) {
                                return false;
                            }
                            break;
                        case GRADE_GT:
                            if (!checkIntegerGreater(book.getRating(), criterion.getValue())) {
                                return false;
                            }
                            break;
                        case GRADE_LT:
                            if (!checkIntegerSmaller(book.getRating(), criterion.getValue())) {
                                return false;
                            }
                            break;
                    }
                }
                return true;
            }
        }), new Function<MutableBook, String>() {
            public String apply(MutableBook book) {
                return book.getIsbn();
            }
        }));
        */
    }

    private static boolean checkIntegerGreater(int actual, String criteria) {
        int crit;
        try {
            crit = Integer.parseInt(criteria);
        } catch (NumberFormatException ex) {
            return false;
        }
        if (actual >= crit) {
            return true;
        }
        return false;
    }

    private static boolean checkIntegerSmaller(int actual, String criteria) {
        int crit;
        try {
            crit = Integer.parseInt(criteria);
        } catch (NumberFormatException ex) {
            return false;
        }
        if (actual <= crit) {
            return true;
        }
        return false;
    }

    private static boolean checkStringMatch(String actual, String criteria) {
        if (actual == null) {
            return false;
        }
        actual = actual.toLowerCase();
        criteria = criteria.toLowerCase();

        boolean startsWith = criteria.startsWith("%");
        boolean endsWith = criteria.endsWith("%");

        if (startsWith && endsWith) {
            if (criteria.length() == 1) {
                return true;
            } else {
                return actual.contains(criteria.substring(1, criteria.length() - 1));
            }
        } else if (startsWith) {
            return actual.endsWith(criteria.substring(1));
        } else if (endsWith) {
            return actual.startsWith(criteria.substring(0, criteria.length() - 1));
        } else {
            return actual.equals(criteria);
        }
    }
}
