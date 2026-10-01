package com.example.trading.order;

import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService service;
    public OrderController(OrderService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<OrderResponse> place(@RequestHeader("Idempotency-Key") String key,
                                               @Valid @RequestBody PlaceOrderRequest req) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(OrderResponse.from(service.place(key, req)));
    }

    @GetMapping("/{id}")
    public OrderResponse get(@PathVariable UUID id) {
        return service.find(id).map(OrderResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "order not found"));
    }
}
