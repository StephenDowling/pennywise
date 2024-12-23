package stephendowling.pennywise.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import stephendowling.pennywise.config.CustomUserDetails;
import stephendowling.pennywise.dto.TransactionResponse;
import stephendowling.pennywise.dto.UserSummary;
import stephendowling.pennywise.exceptions.CategoryNotFoundException;
import stephendowling.pennywise.exceptions.TransactionNotFoundException;
import stephendowling.pennywise.exceptions.UnauthorisedAccessException;
import stephendowling.pennywise.exceptions.UserNotFoundException;
import stephendowling.pennywise.model.Category;
import stephendowling.pennywise.model.Transaction;
import stephendowling.pennywise.model.User;
import stephendowling.pennywise.repository.CategoryRepository;
import stephendowling.pennywise.repository.TransactionRepository;
import stephendowling.pennywise.repository.UserRepository;

@Service
public class TransactionService extends BaseService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository, CategoryRepository categoryRepository){
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.categoryRepository=categoryRepository;
    }
    
    /* ADMIN ONLY */
    public List<TransactionResponse> findAll() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) principal;
        
            //check if the user has ADMIN role
            boolean isAdmin = userDetails.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ADMIN"));

            if (!isAdmin) {
                throw new UnauthorisedAccessException("Unauthorised access");
            }
        } else {
            throw new RuntimeException("User not authenticated");
        }
        List<Transaction> transactions = transactionRepository.findAll();
        return transactions.stream()
                            .map(this::mapToTransactionResponse)
                            .collect(Collectors.toList());
    }

    /* METHODS FOR ALL USERS */
    //get all transactions for the currently logged-in user
    public List<TransactionResponse> getAllTransactionsForCurrentUser() {
        //get the authenticated user's ID
        Integer authenticatedUserId = getAuthenticatedUserId();

        //fetch all transactions for the authenticated user
        List<Transaction> transactions = transactionRepository.findByUser_UserId(authenticatedUserId);

        //map the list of transactions to TransactionResponse DTOs and return the list
        return transactions.stream()
                    .map(this::mapToTransactionResponse)
                    .collect(Collectors.toList());
    }

    //create a transaction 
    public TransactionResponse create(Transaction transaction) {
        //get the authenticated user's ID
        Integer authenticatedUserId = getAuthenticatedUserId();
    
        //fetch the authenticated user from the database
        User user = userRepository.findById(authenticatedUserId)
                                .orElseThrow(() -> new UserNotFoundException());
    
        //associate the transaction with the authenticated user
        transaction.setUser(user);
    
        //fetch the category using the categoryId provided in the request
        if (transaction.getCategory() == null || transaction.getCategory().getCategoryId() == null) {
            throw new IllegalArgumentException("Category ID must be provided");
        }
    
        Category category = categoryRepository.findById(transaction.getCategory().getCategoryId())
                     .orElseThrow(() -> new CategoryNotFoundException());
    
        //associate the transaction with the fetched category
        transaction.setCategory(category);
    
        //save the Transaction entity
        transaction = transactionRepository.save(transaction);
    
        //map the saved transaction entity to a TransactionResponse and return it
        return mapToTransactionResponse(transaction);
    }
    
    //update a transaction 
    public TransactionResponse update(Transaction transaction, Integer id) {
        //get the authenticated user's ID
        Integer authenticatedUserId = getAuthenticatedUserId();
    
        return transactionRepository.findById(id)
                .map(existingTransaction -> {
                    //ensure the authenticated user is the owner of the transaction
                    if (!existingTransaction.getUser().getUserId().equals(authenticatedUserId)) {
                        throw new UnauthorisedAccessException("You can only update your own transactions");
                    }
    
                    //fetch and validate the category provided in the request
                    if (transaction.getCategory() == null || transaction.getCategory().getCategoryId() == null) {
                        throw new IllegalArgumentException("Category ID must be provided");
                    }
    
                    Category category = categoryRepository.findById(transaction.getCategory().getCategoryId())
                            .orElseThrow(() -> new CategoryNotFoundException());
    
                    //update the transaction fields
                    existingTransaction.setCategory(category);  //ensure the valid category is set
                    existingTransaction.setAmount(transaction.getAmount());
                    existingTransaction.setDate(transaction.getDate());
                    existingTransaction.setType(transaction.getType());
                    existingTransaction.setDescription(transaction.getDescription());
    
                    //save and return the updated transaction
                    Transaction updatedTransaction = transactionRepository.save(existingTransaction);
                    return mapToTransactionResponse(updatedTransaction); 
                })
                .orElseThrow(() -> new TransactionNotFoundException());
    }
    
    //delete a transaction 
    public void delete(Integer transactionId){
        Integer authenticatedUserId = getAuthenticatedUserId();

        Transaction transaction = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new TransactionNotFoundException());

        //verify that the transaction belongs to the authenticated user
        if(!transaction.getUser().getUserId().equals(authenticatedUserId)){
            throw new UnauthorisedAccessException("Unauthorised delete attempt");
        }

        //delete the transaction
        transactionRepository.deleteById(transactionId);
    }

    //helper method for mapping Transaction to TransactionResponse DTO
    public TransactionResponse mapToTransactionResponse(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();

        //set fields from Transaction
        response.setTransactionId(transaction.getTransactionId());
        response.setAmount(transaction.getAmount());
        response.setDate(transaction.getDate());
        response.setType(transaction.getType().toString());
        response.setDescription(transaction.getDescription());

        //map User to UserSummary
        UserSummary userSummary = new UserSummary(transaction.getUser().getUserId(), 
                                                transaction.getUser().getUsername());
        response.setUser(userSummary);

        //map Category Name
        response.setCategoryName(transaction.getCategory().getName());

        return response;
    }

    //find transaction by Id
    public TransactionResponse findById(Integer transactionId) {
        //retrieve the authenticated user's ID
        Integer authenticatedUserId = getAuthenticatedUserId();
    
        //fetch the transaction from the repository
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException());
    
        //verify that the transaction belongs to the authenticated user
        if (!transaction.getUser().getUserId().equals(authenticatedUserId)) {
            throw new UnauthorisedAccessException("Unauthorised access attempt for transaction ID " + transactionId);
        }
    
        //return the transaction if validation passes
        return mapToTransactionResponse(transaction);
    }

}
