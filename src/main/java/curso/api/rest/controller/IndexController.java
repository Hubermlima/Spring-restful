package curso.api.rest.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import curso.api.rest.model.UserSystem;
import curso.api.rest.repository.IUserSystem;
import curso.api.rest.repository.IUserTelephone;
import curso.api.rest.service.UserDetailsServiceImpl;

@CrossOrigin(origins = "*") // Libera este controller especifico pra todos
@RestController
@RequestMapping(value = "/userSystem")
public class IndexController {

	@Autowired
	private IUserSystem iUserSystem;
	
	@Autowired
	private IUserTelephone iUserTelephone;
	
	@Autowired
	private UserDetailsServiceImpl userDetailsServiceImpl;

	@GetMapping(value = "/listUsers/page/{page}", produces = "application/json")
	//@CachePut("listUsers") 
	public ResponseEntity<Page<UserSystem>> userListPaged(@PathVariable(value = "page") int page) {

		PageRequest pageRequest = PageRequest.of(page, 5, Sort.by("nameUser"));
		Page<UserSystem> listUserByPage = iUserSystem.findAll(pageRequest);
		
		return new ResponseEntity<Page<UserSystem>>(listUserByPage, HttpStatus.OK);
		
	}
	
	@GetMapping(value = "/returnUserById/{id}", produces = "application/json")
	@CachePut("listUsers")
	public ResponseEntity<UserSystem> returnUserById(@PathVariable(value = "id") Long id) {

		UserSystem userSystem = iUserSystem.findById(id).get();

		return new ResponseEntity<UserSystem>(userSystem, HttpStatus.OK);
	}
	
	// ENDPOINT - Consulta de usuarios por fragmento de nomes
	@GetMapping(value = "/returnUserbyName/{fragmentName}", produces = "application/json")
	@CachePut("listUsers") 
	public ResponseEntity<Page<UserSystem>> returnUserbyName(@PathVariable(value = "fragmentName") String fragmentName) {

		Pageable pageRequest = PageRequest.of(0, 5, Sort.by("nameUser"));
		Page<UserSystem> listUserByPage = null;
		
		if (fragmentName == null || 
		   (fragmentName != null && fragmentName.trim().isEmpty()) || 
		    fragmentName.equalsIgnoreCase("undefined")) {
			
			listUserByPage = iUserSystem.findAll(pageRequest);
			
		} else {
			
			listUserByPage = iUserSystem.findByNameUserContainingIgnoreCase(fragmentName, pageRequest);
			
		}

				
		return new ResponseEntity<Page<UserSystem>>(listUserByPage, HttpStatus.OK);
		
	}

	@PostMapping(value = "/", produces = "application/json")
	public ResponseEntity<UserSystem> saveUser(@RequestBody UserSystem userSystem) {

		// FAzer a associação dos objetos em memoria
		userSystem.getTelephones().forEach(t -> t.setUserSystem(userSystem));

		// Criptografar a senha do usuario antes de gravar no banco
		String cryptoPassword = new BCryptPasswordEncoder().encode(userSystem.getPassword());
		userSystem.setPassword(cryptoPassword);

		UserSystem userSystemSaved = iUserSystem.save(userSystem);
		userDetailsServiceImpl.inserirAcessoPadrao(userSystemSaved.getId());
		
		return new ResponseEntity<UserSystem>(userSystemSaved, HttpStatus.OK);

	}

	@PutMapping(value = "/", produces = "application/json")
	public ResponseEntity<UserSystem> updateUser(@RequestBody UserSystem userSystem) {

		// Obtendo a senha antiga pelo id imutável
		String oldPasswordUserSystem = iUserSystem.findPasswordById(userSystem.getId());
		
		// Comparando a senha antiga com a nova; se são diferentes, altera
		if (!oldPasswordUserSystem.equalsIgnoreCase(userSystem.getPassword())) {
			userSystem.setPassword(new BCryptPasswordEncoder().encode(userSystem.getPassword()));       	
		}
			
		// FAzer a associação dos objetos em memoria
	    userSystem.getTelephones().forEach(t -> t.setUserSystem(userSystem));
 
		UserSystem userSystemSaved = iUserSystem.save(userSystem);
		return new ResponseEntity<UserSystem>(userSystemSaved, HttpStatus.OK);

	}

	@DeleteMapping(value = "/{id}", produces = "application/text")
	public ResponseEntity<String> deleteUser(@PathVariable(value = "id") Long id) {
		iUserSystem.deleteById(id);
		return new ResponseEntity<String>("Successfully removed!", HttpStatus.OK);

	}
	
	@DeleteMapping(value = "/removerTelephone/{id}", produces = "application/text")
	public String deleteUserTelephone(@PathVariable(value = "id") Long id) {
		iUserTelephone.deleteById(id);
		return "ok";

	}
}
