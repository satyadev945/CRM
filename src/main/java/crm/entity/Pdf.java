package crm.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * Cloud-ready PDF entity
 * Stores S3 key instead of local file path
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
     * S3 key (path) where the PDF is stored in cloud storage
     * Replaces local file path for cloud compatibility
     */
    @Column(name = "s3_key", length = 500)
    private String s3Key;

    /**
     * AWS region where the PDF is stored (optional)
     */
    @Column(name = "aws_region", length = 50)
    private String awsRegion;

}
