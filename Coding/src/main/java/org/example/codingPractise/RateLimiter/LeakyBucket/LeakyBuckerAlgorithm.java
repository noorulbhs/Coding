package org.example.codingPractise.RateLimiter.LeakyBucket;

import java.util.concurrent.ConcurrentHashMap;

public class LeakyBuckerAlgorithm {
    private final long maxBucketSize;
    private final long leakRateMs;
    private final ConcurrentHashMap<Integer, Bucket> leakyBucket;

    public LeakyBuckerAlgorithm(long maxBucketSize, long leakRateMs){
        this.maxBucketSize = maxBucketSize;
        this.leakRateMs = leakRateMs;
        this.leakyBucket = new ConcurrentHashMap<>();
    }


    public boolean acquire(int userId){
        Bucket bucket = leakyBucket.computeIfAbsent(userId, k->new Bucket());

        synchronized (bucket){
            leakBucket(bucket);

            if(bucket.currentLevel < maxBucketSize){
                bucket.currentLevel ++;
                System.out.println(Thread.currentThread().getName() + " has acuire the token");
                return true;
            }

            System.out.println(Thread.currentThread().getName() + " has not acquire the token");
            return false;
        }
    }

    public void leakBucket(Bucket bucket){
        long now = System.currentTimeMillis();

        long leaky = ((now - bucket.lastLeaskTime) * leakRateMs)/1000;
        if(leaky > 0){
            bucket.currentLevel = Math.max(0,bucket.currentLevel - leaky);
            bucket.lastLeaskTime = now;
        }
    }

}

class Bucket{
    long currentLevel;
    long lastLeaskTime;

    Bucket(){
        currentLevel = 0;
        lastLeaskTime = System.currentTimeMillis();
    }
}
