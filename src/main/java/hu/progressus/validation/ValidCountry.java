package hu.progressus.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = CountryValidator.class)
@Target({ FIELD })
@Retention(RUNTIME)
public @interface ValidCountry {
  String message() default "Invalid country code";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}

