package com.timesphere.timesphere.config.paypal;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/paypal")
public class PaypalController {

    private final PaypalService paypalService;

    @GetMapping
    public String home() {
        return "index";
    }

    @PostMapping("/create")
    public RedirectView createPayment() {
        try {
            String cancelUrl = "http://localhost:8081/api/paypal/cancel";
            String successUrl = "http://localhost:8081/api/paypal/success";
            Payment payment = paypalService.createPayment(
                    10.0,
                    "USD",
                    "paypal",
                    "sale",
                    "Payment description",
                    cancelUrl,
                    successUrl
            );

            for (Links links : payment.getLinks()) {
                if (links.getRel().equals("approval_url")) {
                    return new RedirectView(links.getHref());
                }
            }
        } catch (PayPalRESTException e) {
            log.error("Error occurred: ", e);
        }
        return new RedirectView("/error");
    }

    @GetMapping("/success")
    public String paymentSuccess(
            @RequestParam("paymentId") String paymentId,
            @RequestParam("payerId") String payerId
    ){
        try {
            Payment payment = paypalService.executePayment(paymentId, payerId);
            if (payment.getState().equals("approve")){
                return "paymentSuccess";
            }
        } catch (PayPalRESTException e) {
            log.error("Error occurred: ", e);
        }
        return "paymentSuccess";
    }

    @GetMapping("/cancel")
    public String paymentCancel(){
        return "paymentCancel";
    }

    @GetMapping("/error")
    public String paymentError(){
        return "paymentError";
    }
}

