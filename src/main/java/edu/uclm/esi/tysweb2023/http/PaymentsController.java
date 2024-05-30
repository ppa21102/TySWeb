package edu.uclm.esi.tysweb2023.http;

import java.util.Map;

import javax.servlet.http.HttpSession;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

@RestController
@RequestMapping("payments")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class PaymentsController {
	static {
		Stripe.apiKey = "sk_test_51PKfPnDlwa3sEP0vnXcYYLrys0o1GPx0ffkkSj3vq4Ki5frl7N9yg0ZHf2ZAEDVK9I6kLGdAD5nj6BlwDqygRpLp00P2JPXkEB";
	}

	@RequestMapping("/prepay")
	public String prepay(@RequestParam double amount) {
		long total = (long) Math.floor(amount * 100);
		PaymentIntentCreateParams params = new PaymentIntentCreateParams.Builder().setCurrency("eur").setAmount(total)
				.build();
		try {
			PaymentIntent intent = PaymentIntent.create(params);
			JSONObject jso = new JSONObject(intent.toJson());
			String clientSecret = jso.getString("client_secret");
			return clientSecret;
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se ha podido realizar el pago");
		}
	}
}