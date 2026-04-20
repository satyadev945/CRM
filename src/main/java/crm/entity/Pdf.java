package crm.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * Cloud-ready PDF entity that stores S3 location instead of local file paths.
 * This ensures data durability and availability in cloud environments.
 */
@Entity(name = "pdf")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pdf {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    @Size(min = 2)
    private String name;

    @Transient
    private String content;

    /**
     * S3 object key where the PDF is stored in Amazon S3.
     * Format: "pdfs/filename.pdf"
     * This replaces local file path storage for cloud-native deployments.
     */
    @Column(name = "s3_key", length = 500)
    private String s3Key;

    /**
     * S3 bucket name where the PDF is stored.
     * Can be null if using default bucket from configuration.
     */
    @Column(name = "s3_bucket", length = 255)
    private String s3Bucket;

}
