package crm.service;

import crm.entity.Book;

/**
 * BookService
 * Service interface for Book operations
 */
public interface BookService {

    /**
     * List all books
     *
     * @return Iterable of all books
     */
    Iterable<Book> listAllBooks();

    /**
     * Get a specific book by ID
     *
     * @param id book ID
     * @return Book entity
     */
    Book getBookById(Long id);

    /**
     * Save a book
     *
     * @param book Book entity to save
     * @return Saved book entity
     */
    Book saveBook(Book book);

    /**
     * Find all available books
     *
     * @return Iterable of available books
     */
    Iterable<Book> findAllAvailableBooks();

    /**
     * Find book by title
     *
     * @param title book title
     * @return Book entity
     */
    Book findByTitle(String title);

    /**
     * Find books by author
     *
     * @param author book author
     * @return Iterable of books
     */
    Iterable<Book> findByAuthor(String author);

    /**
     * Find books by genre
     *
     * @param genre book genre
     * @return Iterable of books
     */
    Iterable<Book> findByGenre(String genre);

    /**
     * Delete a book
     *
     * @param id book ID
     */
    void deleteBook(Long id);

}
