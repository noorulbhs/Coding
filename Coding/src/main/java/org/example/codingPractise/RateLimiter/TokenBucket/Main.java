package org.example.codingPractise.RateLimiter.TokenBucket;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        long maxToken = 5;
        long refillRate = 1;

        ExecutorService ex = Executors.newFixedThreadPool(10);
        TokenBucketAlgorithm tokenBucketAlgorithm = new TokenBucketAlgorithm(maxToken, refillRate);
        for(int i=0;i<10;i++){
            ex.execute(()->tokenBucketAlgorithm.acquire(1));
        }
        ex.shutdown();
    }
}
