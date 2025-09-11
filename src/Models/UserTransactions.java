package Models;

import java.util.concurrent.atomic.AtomicInteger;

public class UserTransactions {
    static AtomicInteger id = new AtomicInteger(1);



  public   Integer userId;
    public Integer transactionId;
   public User associatedUser;
  public   Integer amountOfTransaction;
  public   String operationPerformed;


    public UserTransactions(Integer userId, Integer amountOfTransaction, String operationPerformed){
        this.userId = userId;
        this.transactionId = id.getAndIncrement();

        this.amountOfTransaction = amountOfTransaction;
        this.operationPerformed = operationPerformed;

    }

    public UserTransactions(Integer userId,User associatedUser, Integer amountOfTransaction, String operationPerformed) {
        this.userId = userId;
        this.transactionId = id.getAndIncrement();

        this.amountOfTransaction = amountOfTransaction;
        this.operationPerformed = operationPerformed;
        this.associatedUser = associatedUser;

    }

}
