package hu.progressus.aws.s3;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;

/**
 * Service class for handling S3 operations.
 */

@Slf4j
@Service
public class S3Service {
  @Value("${aws.s3.bucketName}")
  private String bucketName;

  @Value("${aws.s3.accessKey}")
  private String accessKey;

  @Value("${aws.s3.secretKey}")
  private String secretKey;

  @Value("${aws.s3.region}")
  private String region;

  private S3Client s3Client;

  /**
   * Initializes the S3 client using the provided credentials and region.
   */
  @PostConstruct
  public void initializeS3Client() {
    AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
    s3Client = S3Client.builder()
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .region(Region.of(region))
        .build();
    log.info("Initialized S3 client for bucket: {}", bucketName);
  }

  /**
   * Uploads a file to S3.
   *
   * @param fileName the name of the file to upload
   * @param inputStream the InputStream of the file
   * @throws IOException if an I/O error occurs
   * @throws S3Exception if an S3 error occurs
   */
  public void uploadFile(String fileName, InputStream inputStream) throws IOException,S3Exception {
      String contentType = "image/jpeg";

      PutObjectRequest putObjectRequest = PutObjectRequest.builder()
          .bucket(bucketName)
          .key(fileName)
          .contentDisposition("inline")
          .contentType(contentType)
          .build();

      long contentLength = inputStream.available();
      if (contentLength <= 0) {
        throw new IllegalArgumentException("Content length is zero or negative");
      }

      s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, contentLength));
      logUpload(fileName);
  }

  /**
   * Deletes a file from S3.
   *
   * @param key the key of the file to delete
   * @throws S3Exception if the AWS SDK reports an error during deletion
   */
  public void deleteFile(String key) throws S3Exception {
    DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .build();

    s3Client.deleteObject(deleteObjectRequest);
    log.info("Deleted file from S3 with key: {}", key);
  }


  /**
   * Generates a public URL for an image stored in S3.
   *
   * @param fileName the name of the file
   * @return the generated URL
   */
  // Format: https://<bucket-name>.s3.<region>.amazonaws.com/<file-name>
  public String generateImageUrl(String fileName) {
    return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, fileName);
  }

  /**
   * Logs the upload of a file to S3.
   *
   * @param key the key of the uploaded file
   */
  private static void logUpload(String key) {
    log.info("File uploaded to S3 with key: {}", key);
  }
}