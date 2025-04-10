package hu.progressus.response;

import hu.progressus.entity.Image;
import lombok.Data;

@Data
public class ImageResponse {
  private Long id;
  private String url;

  protected ImageResponse(Image image) {
    this.id = image.getId();
    this.url = image.getUrl();
  }

  public static ImageResponse of(Image image){
    return new ImageResponse(image);
  }
}
