package Models;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class BankData {

    public ConcurrentHashMap<Integer,User> users = new ConcurrentHashMap<>();
public AtomicInteger totalBankBalance = new AtomicInteger(10000);
public ConcurrentHashMap<Integer, List<UserTransactions>> userTransactions = new ConcurrentHashMap<>();


}
