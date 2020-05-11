package curso.api.rest.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import curso.api.rest.model.UserSystem;
@Repository
@Transactional
public interface IUserSystem extends PagingAndSortingRepository<UserSystem, Long> {

	@Query("select u from UserSystem u where u.username = ?1")
	public UserSystem findUserByLogin(String username);
	
	@Query("select u.password from UserSystem u where u.id = ?1")
	public String findPasswordById(Long id);
	
	Page<UserSystem> findByNameUserContainingIgnoreCase(String fragmentName, Pageable pageable);
	
	@Transactional
	@Modifying
	@Query(nativeQuery = true, value = "INSERT INTO users_role(user_id, role_id) VALUES (?1, (select id from role where role_name = 'ROLE_USER'))")
	public void inserirAcessoPadrao(Long idUser);
	
}
