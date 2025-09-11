package Models;

import java.util.concurrent.atomic.AtomicInteger;

public class User {


    static AtomicInteger id = new AtomicInteger(1000);

    public int userId;
    public String name;
    public AtomicInteger bankBalance = new AtomicInteger(1000);


  public   User(String name) {
        this.name = name;
        this.userId = id.incrementAndGet();

    }

}
