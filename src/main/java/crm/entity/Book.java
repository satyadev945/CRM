package crm.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * Book Entity
 * Represents a book in the system
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    @NotEmpty(message = "Title is required")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;

    @Column(nullable = false)
    @NotEmpty(message = "Author is required")
    @Size(min = 1, max = 255, message = "Author must be between 1 and 255 characters")
    private String author;

    @Column
    private String isbn;

    @Column
    private String publisher;

    @Column
    private Integer publishedYear;

    @Column
    private String genre;

    @Column(nullable = false)
    private Boolean available = true;

}
