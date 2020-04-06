package app.ccb.repositories;

import app.ccb.domain.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    Optional<Employee> findByFirstNameAndLastName(String firstName, String lastName);

    @Query("select e from Employee e " +
            "where size(e.clients) >0 " +
            "order by size(e.clients) desc, e.id asc")
    List<Employee> findAllByClientsCount();
}
