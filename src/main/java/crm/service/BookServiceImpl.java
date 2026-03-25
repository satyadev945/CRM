package crm.service;

import crm.entity.Book;
import crm.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * BookServiceImpl
 * Implementation of BookService interface
 */
@Service
public class BookServiceImpl implements BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookServiceImpl.class);

    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Iterable<Book> listAllBooks() {
        logger.info("Fetching all books");
        return bookRepository.findAll();
    }

    @Override
    public Book getBookById(Long id) {
        logger.info("Fetching book with ID: {}", id);
        return bookRepository.findOne(id);
    }

    @Override
    public Book saveBook(Book book) {
        logger.info("Saving book: {}", book.getTitle());
        return bookRepository.save(book);
    }

    @Override
    public Iterable<Book> findAllAvailableBooks() {
        logger.info("Fetching all available books");
        return bookRepository.findAllByAvailable(true);
    }

    @Override
    public Book findByTitle(String title) {
        logger.info("Fetching book by title: {}", title);
        return bookRepository.findByTitle(title);
    }

    @Override
    public Iterable<Book> findByAuthor(String author) {
        logger.info("Fetching books by author: {}", author);
        return bookRepository.findAllByAuthor(author);
    }

    @Override
    public Iterable<Book> findByGenre(String genre) {
        logger.info("Fetching books by genre: {}", genre);
        return bookRepository.findAllByGenre(genre);
    }

    @Override
    public void deleteBook(Long id) {
        logger.info("Deleting book with ID: {}", id);
        bookRepository.delete(id);
    }

}
