package stephendowling.pennywise.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import stephendowling.pennywise.dto.TransactionResponse;
import stephendowling.pennywise.model.Transaction;
import stephendowling.pennywise.service.TransactionService;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    
    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService){
        this.transactionService=transactionService;
    }

    /* ADMIN ONLY */
    //get all transactions 
    @GetMapping //http://localhost:8080/api/transactions
    public List<TransactionResponse> getAllGoals(){
        return transactionService.findAll();
    }

    /* METHODS FOR ALL USERS */
    // Get all transactions for the currently logged-in user
    @GetMapping("/my-transactions") //http://localhost:8080/api/transactions/my-transactions
    public List<TransactionResponse> getAllTransactionsForAuthenticatedUser(){
        return transactionService.getAllTransactionsForCurrentUser();
    }

    // Create a new transaction
    @PostMapping //http://localhost:8080/api/transactions
    public ResponseEntity<TransactionResponse> createTransaction(@RequestBody Transaction transaction){
        TransactionResponse response = transactionService.create(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //update a transaction 
    @PutMapping("/{id}") //http://localhost:8080/api/transactions/{id}
    public ResponseEntity<TransactionResponse> updateTransaction(@RequestBody Transaction transaction, @PathVariable Integer id) {
        try {
            TransactionResponse updatedTransaction = transactionService.update(transaction, id);
            return ResponseEntity.ok(updatedTransaction);
        } catch (RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    // Delete a transaction
    @DeleteMapping("/{id}") //http://localhost:8080/api/transactions/{id}
    public ResponseEntity<Void> deleteTransaction(@PathVariable Integer id){
        transactionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // get transaction by transaction ID
    @GetMapping("/{transactionId}") // Make sure this annotation matches your intent
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable Integer transactionId) {
        TransactionResponse transaction = transactionService.findById(transactionId);
        if (transaction == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(transaction);
    }
}
