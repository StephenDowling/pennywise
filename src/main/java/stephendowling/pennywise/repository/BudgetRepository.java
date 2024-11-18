package stephendowling.pennywise.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import stephendowling.pennywise.model.Budget;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Integer> {

    //customer methods here 

    List<Budget> findByUser_UserId(Integer authenticatedUserId);


}
