package hu.progressus.seed;

import hu.progressus.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class BalanceDataSeeder implements CommandLineRunner {

  private final UserRepository userRepository;

  public BalanceDataSeeder(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public void run(String... args) throws Exception {
    userRepository.findAll().forEach(user -> {
      if (user.getBalance() == null || user.getBalance() < 20_000) {
        user.setBalance(20_000);
        userRepository.save(user);
      }
    });
  }
}
