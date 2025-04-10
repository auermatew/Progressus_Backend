package hu.progressus.service;

import hu.progressus.aws.s3.S3Service;
import hu.progressus.entity.Image;
import hu.progressus.entity.User;
import hu.progressus.repository.ImageRepository;
import hu.progressus.response.ImageResponse;
import hu.progressus.util.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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


  public ImageResponse uploadImage(MultipartFile multipartFile) {
    if (!isValidImage(multipartFile)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid image type");
    }

    try {
      String uploadedFileName = uploadImageToS3(multipartFile);
      Image image = saveToDatabase(uploadedFileName);
      return ImageResponse.of(image);
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
    Image image = Image.builder().url(url)
        .user(user).build();
    return imageRepository.save(image);
  }
}