package service;

import CustomException.ErrorCode;
import Models.BankData;
import Models.User;
import Models.UserTransactions;
import CustomException.BankingOperationException;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BankingOperation {
    public BankData bankData = new BankData();
    Lock lock = new ReentrantLock();

    public User registerUser(User user) throws InterruptedException {
        boolean acquired = lock.tryLock(100, TimeUnit.MILLISECONDS);
        try {
            if (acquired) {
                bankData.users.put(user.userId, user);
                System.out.println("User Registered Successfully : " + user);
                System.out.println("User Registered Successfully : " +
                        "\nUser name :" + user.name+
                        "\nuserId :" + user.userId+
                        "\nBank Balance : " + user.bankBalance);
            } else {
 //                   throw new BankingOperationException(ErrorCode.ANOTHER_THREAD_EXECTING.getCode(), ErrorCode.ANOTHER_THREAD_EXECTING.getMessage());
                lock.lockInterruptibly();
                try {
                    bankData.users.put(user.userId, user);
                    System.out.println("User Registered Successfully : " +
                            "\nUser name :" + user.name+
                            "\nuserId :" + user.userId+
                    "\nBank Balance : " + user.bankBalance);
                } finally {
                    lock.unlock();
                }
            }
        }  finally {
            if (acquired) {
                lock.unlock();
            }
        }
        return user;
    }

    public void depositAmount(Integer amount, int userId) throws Exception {
        if(!bankData.users.containsKey(userId)) {
            throw new BankingOperationException(ErrorCode.USER_NOT_FOUND.getCode(), ErrorCode.USER_NOT_FOUND.getMessage());
        }
        boolean transactionStatus = false;
        boolean acquired = lock.tryLock(100, TimeUnit.MILLISECONDS);
        try {
            if (acquired) {
                if (amount > 0) {
                    bankData.totalBankBalance.set(bankData.totalBankBalance.get() + amount);
                    bankData.users.get(userId).bankBalance.set(
                            bankData.users.get(userId).bankBalance.get() + amount
                    );
                    System.out.println("User id " + userId + " Deposited Successfully : " + amount +
                            " Bank balance of user is : " + bankData.users.get(userId).bankBalance);

                    transactionStatus = true;
                } else throw new BankingOperationException(ErrorCode.INSUFFICIENT_BALANCE.getCode(), ErrorCode.INSUFFICIENT_BALANCE.getMessage());
                UserTransactions userTransaction =
                        new UserTransactions(userId, amount, "deposit amount");
                bankData.userTransactions.computeIfAbsent(userId, k -> new ArrayList<>()).add(userTransaction);
                System.out.println("User " + userId + " has been deposited successfully\n" +
                        "Transaction details: " + userTransaction);
            } else {
                lock.lockInterruptibly();
            }
        } catch (BankingOperationException e) {
            System.out.println(e.getMessage());
        } finally {
            if (acquired) {
                lock.unlock();
            }
        }
    }


    public void withdrawAmount(int amount, Integer userId) throws Exception {
        if(!bankData.users.containsKey(userId)) {
            throw new BankingOperationException(ErrorCode.USER_NOT_FOUND.getCode(), ErrorCode.USER_NOT_FOUND.getMessage());
        }
        boolean transactionStatus = false;
        boolean acquired = lock.tryLock(100, TimeUnit.MILLISECONDS);
        try {
            if (acquired) {
                if (amount <= bankData.totalBankBalance.get()) {
                    bankData.totalBankBalance.set(bankData.totalBankBalance.get() - amount);
                    bankData.users.get(userId).bankBalance.set(
                            bankData.users.get(userId).bankBalance.get() - amount
                    );

                    System.out.println("User id " + userId + " Withdrawal Successfully : " + amount);
                    System.out.println("Bank balance of user is : " + bankData.users.get(userId).bankBalance);
                    transactionStatus = true;
                } else {
                   throw new BankingOperationException(ErrorCode.INSUFFICIENT_BALANCE.getCode(),  ErrorCode.INSUFFICIENT_BALANCE.getMessage());
                }
                if (transactionStatus) {
                    UserTransactions userTransaction =
                            new UserTransactions(userId, amount, "withdraw amount");
                    bankData.userTransactions.computeIfAbsent(userId, k -> new ArrayList<>()).add(userTransaction);
                    System.out.println("User " + userId + " has withdrawn amount " + amount + " successfully\n" +
                            "Transaction details: " + userTransaction);
                }
            } else {
                lock.lockInterruptibly();
            }
        } finally {
            if (acquired) {
                lock.unlock();
            }
        }
    }

//TODO : add validations, create deadlock
    public void transferAmount(int amount, Integer userId, Integer trasferUserId) throws Exception {
        if((!bankData.users.containsKey(userId) || !bankData.userTransactions.containsKey(trasferUserId))){
            throw new BankingOperationException(ErrorCode.USER_NOT_FOUND.getCode(), ErrorCode.USER_NOT_FOUND.getMessage());
        }
        boolean acquired = lock.tryLock(100, TimeUnit.MILLISECONDS);
        try {
            if (acquired) {
                boolean transactionStatus = false;
                if (amount < bankData.users.get(userId).bankBalance.get()) {
                    bankData.users.get(trasferUserId).bankBalance.set(bankData.users.get(trasferUserId).bankBalance.get() + amount);
                    bankData.users.get(userId).bankBalance.set(bankData.users.get(userId).bankBalance.get() - amount);
                    transactionStatus = true;
                } else {
                   throw new BankingOperationException(ErrorCode.INSUFFICIENT_BALANCE.getCode(),  ErrorCode.INSUFFICIENT_BALANCE.getMessage());
                }
                if (transactionStatus) {
                    UserTransactions userTransaction = new UserTransactions(userId, bankData.users.get(trasferUserId), amount, "transfer amount");
                    bankData.userTransactions.computeIfAbsent(userId, k -> new ArrayList<>()).add(userTransaction);
                    System.out.println("Amount transfer successfully");
                    System.out.println("Transaction details: " +
                            "\nUser " + userTransaction.userId +
                            "\nTrasferUserId : " + userTransaction.associatedUser.userId +"" +
                            "\nTransfer amount : " + userTransaction.amountOfTransaction+
                            "\nTransaction id : " + userTransaction.transactionId);
                }
                System.out.println("Bank Balance of User : " + bankData.users.get(userId).bankBalance);
            } else {
                lock.lockInterruptibly();
                try {
                    if (amount < bankData.users.get(userId).bankBalance.get()) {
                        bankData.users.get(trasferUserId).bankBalance.set(bankData.users.get(trasferUserId).bankBalance.get() + amount);
                        bankData.users.get(userId).bankBalance.set(bankData.users.get(userId).bankBalance.get() - amount);
                        UserTransactions userTransaction = new UserTransactions(userId, bankData.users.get(trasferUserId), amount, "transfer amount");
                        bankData.userTransactions.computeIfAbsent(userId, k -> new ArrayList<>()).add(userTransaction);
                        System.out.println("Amount transfer successfully");
                        System.out.println("Transaction details: " + userTransaction);
                    } else {
                        System.out.println("User does not have enough balance");
                    }
                } finally {
                    lock.unlock();
                }
            }
        } finally {
            if (acquired) {
                lock.unlock();
            }
        }
    }
}
