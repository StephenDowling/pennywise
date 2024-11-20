package stephendowling.pennywise.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import stephendowling.pennywise.model.Goal;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Integer> {
    
    //custom methods 
    List<Goal> findByUser_UserId(Integer authenticatedUserId);

}
