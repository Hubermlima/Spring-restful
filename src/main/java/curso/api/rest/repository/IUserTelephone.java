package curso.api.rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import curso.api.rest.model.Telephone;
@Repository
@Transactional
public interface IUserTelephone extends JpaRepository<Telephone, Long> {
	
}
