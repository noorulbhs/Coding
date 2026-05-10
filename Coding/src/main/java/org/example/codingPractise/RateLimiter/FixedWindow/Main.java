package org.example.codingPractise.RateLimiter.FixedWindow;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    /*
    Rate Limiter is an algorithm that protect the system from getting overload
    by allowing only n number of request within specific timeframe so that
    no user block the resources and Prevents malicious bot activity, reduces API costs
    for paid services, and ensures high availability by reducing server load.

    Implementation Locations: Rate limiters can be implemented on the client-side,
    server-side, or as middleware (e.g., an API Gateway).

    Algorithm:
        1:- Token Bucket Algorithm: This algorithm uses a bucket that holds a
            certain number of tokens.
        2:- Leaky Bucket Algorithm: This algorithm uses a bucket that leaks at a
            constant rate.
        3:- Fixed Window Algorithm: This algorithm divides time into fixed windows
            and counts the number of requests in each window.
        4:- Sliding Window Algorithm: This algorithm uses a sliding window to count
            the number of requests in a given time frame.
     */

    public static void main(String[] args){
        FixedWindow fixedWindowRateLimiter = new FixedWindow(5);
        ExecutorService ex = Executors.newFixedThreadPool(5);
        for(int i=0;i<10;i++){
            ex.submit(()->fixedWindowRateLimiter.execute(123));
        }
        ex.shutdown();
    }

}
