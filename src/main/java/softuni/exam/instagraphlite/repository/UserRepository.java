package softuni.exam.instagraphlite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import softuni.exam.instagraphlite.models.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findUserByUsername(String name);
}
