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

@Service
@RequiredArgsConstructor
public class ImageService {

  private final S3Service s3Service;
  private final ImageRepository imageRepository;
  private final UserUtils userUtils;
  private final UserRepository userRepository;

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

  @Transactional
  public void removeProfilePicture (){
  User user = userUtils.currentUser();

  if (user.getProfileImg() == null){
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User has no profile picture.");
  }
  deleteImage(user);
  }

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

  private boolean isValidImage(MultipartFile file) {
    String contentType = file.getContentType();
    return contentType != null && contentType.startsWith("image/");
  }

  private String uploadImageToS3(MultipartFile multipartFile) throws IOException {
    String fileName = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();
    try (InputStream inputStream = multipartFile.getInputStream()) {
      s3Service.uploadFile(fileName, inputStream);
    }

    return fileName;
  }

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