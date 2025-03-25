package io.camunda.getstarted.ticketingAgent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class PaymentStorageService {
    private final Map<String, Map<String, Object>> paymentStorage = new ConcurrentHashMap<>();

    public void savePaymentDetails(String transactionId, Map<String, Object> details) {
        paymentStorage.put(transactionId, details);
    }

    public Map<String, Object> getPaymentDetails(String transactionId) {
        return paymentStorage.get(transactionId);
    }
}
