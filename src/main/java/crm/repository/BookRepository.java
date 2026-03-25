package crm.repository;

import crm.entity.Book;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * BookRepository
 * Repository interface for Book entity
 */
@Repository
public interface BookRepository extends CrudRepository<Book, Long> {

    /**
     * Find all books by availability status
     *
     * @param available availability status
     * @return Iterable of books
     */
    Iterable<Book> findAllByAvailable(Boolean available);

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
    Iterable<Book> findAllByAuthor(String author);

    /**
     * Find books by genre
     *
     * @param genre book genre
     * @return Iterable of books
     */
    Iterable<Book> findAllByGenre(String genre);

}
