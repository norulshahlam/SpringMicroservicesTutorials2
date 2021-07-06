package shah.CurrencyService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CurrencyController {
    @Value("${server.port}")
    private int port;

    @RequestMapping("/")
    public String getExchangeRate() {
        return ("My port number is: " + port);
    }
}
