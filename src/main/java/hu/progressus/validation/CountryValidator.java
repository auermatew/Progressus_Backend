package hu.progressus.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Locale;

public class CountryValidator implements ConstraintValidator<ValidCountry, String> {

  @Override
  public void initialize(ValidCountry constraintAnnotation) {
  }

  @Override
  public boolean isValid(String countryCode, ConstraintValidatorContext context) {
    if (countryCode == null || countryCode.isEmpty()) {
      return true;
    }
    return Arrays.asList(Locale.getISOCountries())
        .contains(countryCode.toUpperCase());
  }
}

