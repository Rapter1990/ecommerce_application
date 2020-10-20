package com.example.demo.controllers;

import java.util.Optional;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;

@RestController
@RequestMapping("/api/cart")
public class CartController {

	private final static Logger log= LoggerFactory.getLogger(CartController.class);

	String errorMarkerText = "ERROR";
	String infoMarkerText = "INFO";

	Marker errorMarker = MarkerFactory.getMarker(errorMarkerText);
	Marker infoMarker = MarkerFactory.getMarker(infoMarkerText);
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private ItemRepository itemRepository;
	
	@PostMapping("/addToCart")
	public ResponseEntity<Cart> addTocart(@RequestBody ModifyCartRequest request) {
		User user = userRepository.findByUsername(request.getUsername());
		if(user == null) {

			//log.error(errorMarker,"CartController | addTocart | No User with Username : " + user.getUsername());

			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Optional<Item> item = itemRepository.findById(request.getItemId());
		if(!item.isPresent()) {

			//log.error(errorMarker,"CartController | addTocart | No item found by id : " + request.getItemId());

			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Cart cart = user.getCart();

		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.addItem(item.get()));

		cartRepository.save(cart);

		log.info(infoMarker,"CartController | addTocart | item added by its id : " + request.getItemId() + " to the cart for the user " + user.getUsername());

		return ResponseEntity.ok(cart);
	}
	
	@PostMapping("/removeFromCart")
	public ResponseEntity<Cart> removeFromcart(@RequestBody ModifyCartRequest request) {
		User user = userRepository.findByUsername(request.getUsername());
		if(user == null) {

			//log.error(errorMarker,"CartController | removeFromcart | No User with Username : " + user.getUsername());

			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Optional<Item> item = itemRepository.findById(request.getItemId());
		if(!item.isPresent()) {

			//log.error("CartController | removeFromcart | No item found by id :  " + user.getUsername());

			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Cart cart = user.getCart();
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.removeItem(item.get()));

		cartRepository.save(cart);

		log.info(infoMarker,"CartController | removeFromcart | item removed by its id : " + request.getItemId() + " to the cart for the user " + user.getUsername());

		return ResponseEntity.ok(cart);
	}
		
}
