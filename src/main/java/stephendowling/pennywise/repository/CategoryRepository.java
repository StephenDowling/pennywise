package stephendowling.pennywise.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import stephendowling.pennywise.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer>{

    //custom methods
    List<Category> findByUser_UserId(Integer authenticatedUserId);
    

}
