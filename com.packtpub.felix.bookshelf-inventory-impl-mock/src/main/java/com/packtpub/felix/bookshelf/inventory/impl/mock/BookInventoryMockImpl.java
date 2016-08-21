package com.packtpub.felix.bookshelf.inventory.impl.mock;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.packtpub.felix.bookshelf.inventory.api.Book;
import com.packtpub.felix.bookshelf.inventory.api.BookAlreadyExistsException;
import com.packtpub.felix.bookshelf.inventory.api.BookInventory;
import com.packtpub.felix.bookshelf.inventory.api.BookNotFoundException;
import com.packtpub.felix.bookshelf.inventory.api.InvalidBookException;
import com.packtpub.felix.bookshelf.inventory.api.MutableBook;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;

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

    public Set<String> searchBooks(final Map<SearchCriteria, String> criteria) {
        return copyOf(transform(filter(booksByIsbn.values(), new Predicate<MutableBook>() {
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
