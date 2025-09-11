import Models.BankData;
import Models.User;
import service.BankingOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
/*
    Banking System Simulation with Multithreading
    Problem Statement
    Implement a multithreaded banking system that allows multiple users to deposit, withdraw
, and transfer money concurrently. Use threads, synchronization, locks, deadlock handling
, and ExecutorService with thread pools.

 */
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Scanner sc = new Scanner(System.in);
        BankingOperation bankingOperation = new BankingOperation();
        List<Runnable> runnables = new ArrayList<>();
        List<Callable<User>> callables = new ArrayList<>();



        System.out.println("Welcome to Banking System Simulation ");
        System.out.println("Enter the number of operations you want to perform on Banking System");
        Integer operations = sc.nextInt();
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5);

        CountDownLatch countDownLatch = new CountDownLatch(operations);

        //boolean flag = true;

        while (countDownLatch.getCount() != 0) {
            System.out.println("Please enter your choice");
            System.out.println("1. Register User\n" +
                    "2. Deposit Amount\n" +
                    "3. Withdraw Amount\n" +
                    "4. Transfer Amount\n" +
                    "5. Exit");

            Short choice = sc.nextShort();
            switch (choice) {
                case 1:
                    System.out.println("Please enter the name of the user");
                    String name = sc.next();
                    User user = new User(name);
                    callables.add(()-> bankingOperation.registerUser(user));

                   countDownLatch.countDown();
                    break;
                case 2:
                    System.out.println("Please enter the user id :");
                    Integer userId = sc.nextInt();
                    System.out.println("Please enter the amount :");
                    int amount = sc.nextInt();
                    runnables.add(() -> {
                        try {
                            bankingOperation.depositAmount(amount, userId);

                            System.out.println("Successfully deposited amount ");
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });
//                    executorService.submit(()->{
//                    bankingOperation.depositAmount(userId,amount);
//                    });
                    countDownLatch.countDown();
                    break;
                case 3:
                    System.out.println("Please enter the user id :");
                    Integer id1 = sc.nextInt();
                    System.out.println("Please enter the amount :");
                    int amount1 = sc.nextInt();
                    runnables.add(() -> {
                        try {
                            bankingOperation.withdrawAmount(amount1, id1);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });
                   // bankingOperation.withdrawAmount(amount1,id1);
                    countDownLatch.countDown();
                    break;
                case 4:
                    System.out.println("Please enter the user id :");
                    Integer id2 = sc.nextInt();
                    System.out.println("Please enter the transfer amount :");
                    int amount2 = sc.nextInt();
                    System.out.println("Please enter the transfer user id :");
                    Integer id3 = sc.nextInt();
                    runnables.add(() -> {
                        try {
                            bankingOperation.transferAmount(amount2, id2, id3);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    //bankingOperation.transferAmount(amount2,id2,id3);
                    countDownLatch.countDown();
                    break;
                case 5:
                    //flag = false;
                    break;
                default:
                    System.out.println("Invalid choice");
                    break;

            }

        }
         executorService.invokeAll(callables,4,TimeUnit.SECONDS);



        countDownLatch.await();
        for (Runnable runnable : runnables) {
            executorService.schedule(runnable,2,TimeUnit.SECONDS);

        }
        executorService.awaitTermination(10, TimeUnit.MILLISECONDS);
        executorService.shutdown();




        }
    }
