package hu.progressus.exceptionhandling;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomErrorResponse {
  int status;
  String message;
}