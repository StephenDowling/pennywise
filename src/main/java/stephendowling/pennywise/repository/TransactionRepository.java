package stephendowling.pennywise.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import stephendowling.pennywise.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer>{
    
    //custom methods 
    List<Transaction> findByUser_UserId(Integer authenticatedUserId);
}
