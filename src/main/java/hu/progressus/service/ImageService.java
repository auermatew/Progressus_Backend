package hu.progressus.service;

import hu.progressus.aws.s3.S3Service;
import hu.progressus.entity.Image;
import hu.progressus.entity.User;
import hu.progressus.repository.ImageRepository;
import hu.progressus.repository.UserRepository;
import hu.progressus.response.ImageResponse;
import hu.progressus.util.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * Handles uploading, deleting, and managing user profile images in S3.
 */
@Service
@RequiredArgsConstructor
public class ImageService {

  private final S3Service s3Service;
  private final ImageRepository imageRepository;
  private final UserUtils userUtils;
  private final UserRepository userRepository;

  /**
   * Upload a new profile picture, replacing any existing one.
   *
   * @param multipartFile the multipart image file
   * @return a DTO with the saved Image info (URL, key, etc.)
   * @throws ResponseStatusException if upload fails or image is invalid
   */
@Transactional
  public ImageResponse uploadProfilePicture (MultipartFile multipartFile){
  User user = userUtils.currentUser();
  if (user.getProfileImg() != null){
    deleteImage(user);
  }
    Image image = uploadImage(multipartFile);
    int rowsUpdated = userRepository.updateProfilePicture(user.getId(),image.getId());
    if (rowsUpdated != 0) {
      return ImageResponse.of(image);
    } else {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error setting profile picture.");
    }
  }

  /**
   * Remove the current user's profile picture from S3 and database.
   *
   * @throws ResponseStatusException if user has no picture or deletion fails
   */
  @Transactional
  public void removeProfilePicture (){
  User user = userUtils.currentUser();

  if (user.getProfileImg() == null){
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User has no profile picture.");
  }
  deleteImage(user);
  }

  /**
   * Internal helper to delete image both from S3 and DB, and clear user's reference.
   *
   * @param user the owner of the image
   * @throws ResponseStatusException on any deletion error
   */
  private void deleteImage(User user){
    Image image = user.getProfileImg();
    try {
      s3Service.deleteFile(user.getProfileImg().getKey());
      user.setProfileImg(null);
      userRepository.save(user);
      userRepository.flush();
      imageRepository.delete(image);
    }
    catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete profile picture.");
    }

  }

  /**
   * Validate, upload to S3, and persist a new Image entity.
   *
   * @param multipartFile the multipart file to upload
   * @return the persisted Image entity
   * @throws ResponseStatusException on invalid type or IO errors
   */
  private Image uploadImage(MultipartFile multipartFile) {
    if (!isValidImage(multipartFile)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid image type.");
    }

    try {
      String uploadedFileName = uploadImageToS3(multipartFile);
      Image image = saveToDatabase(uploadedFileName);
      imageRepository.flush();
      return image;
    } catch (IOException e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

  }

  /**
   * Check if the multipart file has an image content type.
   *
   * @param file the multipart file
   * @return true if content type starts with "image/"
   */
  private boolean isValidImage(MultipartFile file) {
    String contentType = file.getContentType();
    return contentType != null && contentType.startsWith("image/");
  }

  /**
   * Generate a unique filename, upload the file stream to S3, and return the key.
   *
   * @param multipartFile the multipart file
   * @return the generated filename key in S3
   * @throws IOException if reading the input stream fails
   */
  private String uploadImageToS3(MultipartFile multipartFile) throws IOException {
    String fileName = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();
    try (InputStream inputStream = multipartFile.getInputStream()) {
      s3Service.uploadFile(fileName, inputStream);
    }

    return fileName;
  }

  /**
   * Saves a new Image entity to the database for the current user.
   *
   * @param filename the unique filename (key) of the image stored in S3
   * @return the persisted Image entity
   */
  private Image saveToDatabase(String filename){
    User user = userUtils.currentUser();
    var url = s3Service.generateImageUrl(filename);
    Image image = Image.builder()
        .url(url)
        .user(user)
        .key(filename)
        .build();
    return imageRepository.save(image);
  }
}