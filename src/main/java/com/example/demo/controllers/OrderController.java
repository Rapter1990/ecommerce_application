package com.example.demo.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

@RestController
@RequestMapping("/api/order")
public class OrderController {

	private final static Logger log= LoggerFactory.getLogger(OrderController.class);

	String errorMarkerText = "ERROR";
	String infoMarkerText = "INFO";

	Marker errorMarker = MarkerFactory.getMarker(errorMarkerText);
	Marker infoMarker = MarkerFactory.getMarker(infoMarkerText);
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrderRepository orderRepository;
	
	
	@PostMapping("/submit/{username}")
	public ResponseEntity<UserOrder> submit(@PathVariable String username) {

		log.info(infoMarker,"OrderController | submit | Searching username : " + username);


		User user = userRepository.findByUsername(username);

		if(user == null) {

			//log.error(errorMarker,"OrderController | submit | No User with username : " + user.getUsername());

			return ResponseEntity.notFound().build();
		}
		UserOrder order = UserOrder.createFromCart(user.getCart());
		orderRepository.save(order);

		log.info(infoMarker,"OrderController | submit | Order submitted from user with username : " + username);

		return ResponseEntity.ok(order);
	}
	
	@GetMapping("/history/{username}")
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {

		log.info(infoMarker,"OrderController | getOrdersForUser | Searching username : " + username);

		User user = userRepository.findByUsername(username);
		if(user == null) {

			//log.error(errorMarker,"OrderController | getOrdersForUser | No User with username : " + user.getUsername());

			return ResponseEntity.notFound().build();
		}

		log.info(infoMarker,"OrderController | getOrdersForUser | Order listed from user with username : " + username);

		return ResponseEntity.ok(orderRepository.findByUser(user));
	}
}
