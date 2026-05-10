package org.example.codingPractise.RateLimiter.LeakyBucket;

public class Main{
    public static void main(String[] args) throws InterruptedException {
        LeakyBuckerAlgorithm limiter = new LeakyBuckerAlgorithm(3,1);
        int user = 2002;

        // Fill the bucket
        for (int i = 0; i < 5; i++) {
            limiter.acquire(user);
        }

        System.out.println("--- Waiting 1.5 seconds ---");
        Thread.sleep(1500); // Should leak 1 request

        limiter.acquire(user); // Should be allowed
        limiter.acquire(user);
    }
}
