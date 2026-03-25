package crm.controller;

import crm.dto.BookResponse;
import crm.entity.Book;
import crm.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * BookController
 * REST Controller for book-related operations
 */
@RestController
@RequestMapping("/create")
public class BookController {

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * GET /create/books
     * <p>
     * Creates and returns a list of books
     * This endpoint creates sample books if none exist and returns them
     *
     * @return ResponseEntity with BookResponse containing success message and timestamp
     */
    @GetMapping("/books")
    public ResponseEntity<BookResponse> createBooks() {
        try {
            logger.info("Processing request to create/list books");

            // Get all existing books
            Iterable<Book> existingBooks = bookService.listAllBooks();
            List<Book> bookList = new ArrayList<>();
            existingBooks.forEach(bookList::add);

            // If no books exist, create sample books
            if (bookList.isEmpty()) {
                logger.info("No books found, creating sample books");
                createSampleBooks();
                
                // Fetch the newly created books
                existingBooks = bookService.listAllBooks();
                bookList.clear();
                existingBooks.forEach(bookList::add);
            }

            String message = String.format("Successfully retrieved %d books", bookList.size());
            logger.info(message);

            BookResponse response = BookResponse.success(message);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error processing create books request", e);
            BookResponse errorResponse = BookResponse.error("Failed to create/retrieve books: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Helper method to create sample books
     */
    private void createSampleBooks() {
        List<Book> sampleBooks = new ArrayList<>();

        sampleBooks.add(Book.builder()
                .title("The Great Gatsby")
                .author("F. Scott Fitzgerald")
                .isbn("978-0-7432-7356-5")
                .publisher("Scribner")
                .publishedYear(1925)
                .genre("Fiction")
                .available(true)
                .build());

        sampleBooks.add(Book.builder()
                .title("To Kill a Mockingbird")
                .author("Harper Lee")
                .isbn("978-0-06-112008-4")
                .publisher("J.B. Lippincott & Co.")
                .publishedYear(1960)
                .genre("Fiction")
                .available(true)
                .build());

        sampleBooks.add(Book.builder()
                .title("1984")
                .author("George Orwell")
                .isbn("978-0-452-28423-4")
                .publisher("Secker & Warburg")
                .publishedYear(1949)
                .genre("Dystopian")
                .available(true)
                .build());

        sampleBooks.add(Book.builder()
                .title("Pride and Prejudice")
                .author("Jane Austen")
                .isbn("978-0-14-143951-8")
                .publisher("T. Egerton")
                .publishedYear(1813)
                .genre("Romance")
                .available(true)
                .build());

        sampleBooks.add(Book.builder()
                .title("The Catcher in the Rye")
                .author("J.D. Salinger")
                .isbn("978-0-316-76948-0")
                .publisher("Little, Brown and Company")
                .publishedYear(1951)
                .genre("Fiction")
                .available(true)
                .build());

        // Save all sample books
        for (Book book : sampleBooks) {
            bookService.saveBook(book);
            logger.info("Created sample book: {}", book.getTitle());
        }
    }

    /**
     * GET /create/books/list
     * <p>
     * Returns all books in the system
     *
     * @return ResponseEntity with list of books
     */
    @GetMapping("/books/list")
    public ResponseEntity<Iterable<Book>> listAllBooks() {
        try {
            logger.info("Fetching all books");
            Iterable<Book> books = bookService.listAllBooks();
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            logger.error("Error fetching books", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /create/books/{id}
     * <p>
     * Get a specific book by ID
     *
     * @param id book ID
     * @return ResponseEntity with book details
     */
    @GetMapping("/books/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        try {
            logger.info("Fetching book with ID: {}", id);
            Book book = bookService.getBookById(id);
            
            if (book == null) {
                logger.warn("Book not found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(book);
        } catch (Exception e) {
            logger.error("Error fetching book with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * POST /create/books/add
     * <p>
     * Add a new book
     *
     * @param book          Book entity to add
     * @param bindingResult validation result
     * @return ResponseEntity with BookResponse
     */
    @PostMapping("/books/add")
    public ResponseEntity<BookResponse> addBook(@Valid @RequestBody Book book, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                logger.warn("Validation errors while adding book");
                StringBuilder errorMessage = new StringBuilder("Validation errors: ");
                bindingResult.getAllErrors().forEach(error -> 
                    errorMessage.append(error.getDefaultMessage()).append("; ")
                );
                BookResponse errorResponse = BookResponse.error(errorMessage.toString());
                return ResponseEntity.badRequest().body(errorResponse);
            }

            logger.info("Adding new book: {}", book.getTitle());
            Book savedBook = bookService.saveBook(book);
            
            String message = String.format("Book '%s' added successfully with ID: %d", 
                savedBook.getTitle(), savedBook.getId());
            BookResponse response = BookResponse.success(message);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Error adding book", e);
            BookResponse errorResponse = BookResponse.error("Failed to add book: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * PUT /create/books/{id}
     * <p>
     * Update an existing book
     *
     * @param id            book ID
     * @param book          updated Book entity
     * @param bindingResult validation result
     * @return ResponseEntity with BookResponse
     */
    @PutMapping("/books/{id}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable Long id, 
                                                   @Valid @RequestBody Book book, 
                                                   BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                logger.warn("Validation errors while updating book");
                StringBuilder errorMessage = new StringBuilder("Validation errors: ");
                bindingResult.getAllErrors().forEach(error -> 
                    errorMessage.append(error.getDefaultMessage()).append("; ")
                );
                BookResponse errorResponse = BookResponse.error(errorMessage.toString());
                return ResponseEntity.badRequest().body(errorResponse);
            }

            Book existingBook = bookService.getBookById(id);
            if (existingBook == null) {
                logger.warn("Book not found with ID: {}", id);
                BookResponse errorResponse = BookResponse.error("Book not found with ID: " + id);
                return ResponseEntity.notFound().build();
            }

            logger.info("Updating book with ID: {}", id);
            book.setId(id);
            bookService.saveBook(book);
            
            String message = String.format("Book with ID %d updated successfully", id);
            BookResponse response = BookResponse.success(message);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error updating book with ID: {}", id, e);
            BookResponse errorResponse = BookResponse.error("Failed to update book: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * DELETE /create/books/{id}
     * <p>
     * Delete a book
     *
     * @param id book ID
     * @return ResponseEntity with BookResponse
     */
    @DeleteMapping("/books/{id}")
    public ResponseEntity<BookResponse> deleteBook(@PathVariable Long id) {
        try {
            Book existingBook = bookService.getBookById(id);
            if (existingBook == null) {
                logger.warn("Book not found with ID: {}", id);
                BookResponse errorResponse = BookResponse.error("Book not found with ID: " + id);
                return ResponseEntity.notFound().build();
            }

            logger.info("Deleting book with ID: {}", id);
            bookService.deleteBook(id);
            
            String message = String.format("Book with ID %d deleted successfully", id);
            BookResponse response = BookResponse.success(message);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error deleting book with ID: {}", id, e);
            BookResponse errorResponse = BookResponse.error("Failed to delete book: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

}
