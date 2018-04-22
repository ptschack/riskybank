package riskybank.persistence.repositories;

import org.springframework.data.repository.CrudRepository;

import riskybank.persistence.entities.User;

public interface UserRepository extends CrudRepository<User, Long> {

	User findByUsername(String username);

}
