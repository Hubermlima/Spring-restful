package curso.api.rest.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import curso.api.rest.model.UserSystem;
@Repository
@Transactional
public interface IUserSystem extends CrudRepository<UserSystem, Long> {

	@Query("select u from UserSystem u where u.username = ?1")
	public UserSystem findUserByLogin(String username);
	
	@Query("select u.password from UserSystem u where u.id = ?1")
	public String findPasswordById(Long id);
	
}
