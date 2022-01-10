package integration.repository;

import integration.entities.Requests;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestsRepository extends JpaRepository<Requests,Long> {
    public List<Requests> findAllByOrderByIdAsc();

}
