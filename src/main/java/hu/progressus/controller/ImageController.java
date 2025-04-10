package hu.progressus.controller;

import hu.progressus.response.ImageResponse;
import hu.progressus.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/images")
public class ImageController {
  private final ImageService imageService;

  @PostMapping("/uploadProfilePicture")
  public ResponseEntity<ImageResponse> uploadImage(@RequestParam("file")MultipartFile file) {
    return ResponseEntity.ok(imageService.uploadProfilePicture(file));
  }
}
