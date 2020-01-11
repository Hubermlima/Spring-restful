package curso.api.rest.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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

@CrossOrigin(origins = "*") // Libera este controller especifico pra todos
@RestController
@RequestMapping(value = "/userSystem")
public class IndexController {

	@Autowired
	private IUserSystem iUserSystem;

	@GetMapping(value = "/", produces = "application/json")
	@Cacheable("listUsers") // Essa lista agora está em cache para carregar mais rapido
	public ResponseEntity<UserSystem> init(@RequestParam(value = "name", defaultValue = "Name not entered") String name,
			@RequestParam(value = "lastname", defaultValue = "Last name not entered") String lastname) {

		return new ResponseEntity<UserSystem>(HttpStatus.OK);
	}

	@GetMapping(value = "/user", produces = "application/json")
	public ResponseEntity<List<UserSystem>> userTeste() {

		List<UserSystem> userSystemList = new ArrayList<>();

		UserSystem userS = new UserSystem();

		userS.setId(1L);
		userS.setUsername("admin");
		userS.setPassword("123");
		userS.setNameUser("Huber Martins Lima");

		userSystemList.add(userS);

		userS.setId(2L);
		userS.setUsername("admin2");
		userS.setPassword("123");
		userS.setNameUser("Antonio Carlos Portela");

		userSystemList.add(userS);

		return new ResponseEntity<List<UserSystem>>(userSystemList, HttpStatus.OK);
	}

	@GetMapping(value = "/returnUser/{id}", produces = "application/json")
	public ResponseEntity<UserSystem> returnUser(@PathVariable(value = "id") Long id) {

		UserSystem userSystem = iUserSystem.findById(id).get();

		return new ResponseEntity<UserSystem>(userSystem, HttpStatus.OK);
	}

	@PostMapping(value = "/", produces = "application/json")
	public ResponseEntity<UserSystem> saveUser(@RequestBody UserSystem userSystem) {

		// FAzer a associação dos objetos em memoria
		userSystem.getTelephones().forEach(t -> t.setUserSystem(userSystem));

		// Criptografar a senha do usuario antes de gravar no banco
		String cryptoPassword = new BCryptPasswordEncoder().encode(userSystem.getPassword());
		userSystem.setPassword(cryptoPassword);

		UserSystem userSystemSaved = iUserSystem.save(userSystem);
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
}
