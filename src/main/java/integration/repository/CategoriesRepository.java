package integration.repository;

import integration.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriesRepository extends JpaRepository<Category, Long> {

    @Override
    Optional<Category> findById(Long aLong);
}
