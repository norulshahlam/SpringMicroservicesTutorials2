package PaymentService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class PaymentController {

  @Value("${organization.name}")
  private String OrganizationName;

  @Value("${service.welcome.message}")
  private String message;

  @RequestMapping("/")
  public String getPayment() {
    return "Organisation: " + OrganizationName + ", message: " + message;
  }

  @GetMapping("/home")
  public String getHome() {
    return "Welcome to home page of payment service!";
  }
}
