package hu.progressus.controller;

import hu.progressus.response.ImageResponse;
import hu.progressus.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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

  @PostMapping("/profile-picture/upload")
  @Operation(summary = "Upload a profile picture", description = "Upload a profile picture.")
  public ResponseEntity<ImageResponse> uploadProfilePicture(@RequestParam("file")MultipartFile file) {
    return ResponseEntity.ok(imageService.uploadProfilePicture(file));
  }
  @DeleteMapping("/profile-picture/delete")
  @Operation(summary = "Remove profile picture", description = "Deletes the profile picture.")
  public void removeProfilePicture(){
    imageService.removeProfilePicture();
  }
}
