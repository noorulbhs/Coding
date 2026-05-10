package org.example.codingPractise.RateLimiter.TokenBucket;

import java.util.concurrent.ConcurrentHashMap;

public class TokenBucketAlgorithm {
    private final long maxToken;
    private final long refillRatePerMs;

    private final ConcurrentHashMap<Integer,Bucket> tokenBucket;

    public TokenBucketAlgorithm(long maxToken, long refillRatePerMs){
        this.maxToken = maxToken;
        this.refillRatePerMs = refillRatePerMs;
        tokenBucket = new ConcurrentHashMap<>();
    }

    public boolean acquire(int userId){

        Bucket bucket = tokenBucket.computeIfAbsent(userId,(id)->new Bucket(maxToken));

        synchronized (bucket){
            updateToken(bucket);

            if(bucket.token > 0){
                bucket.token--;
                System.out.println(Thread.currentThread().getName() + " has acquire the token");
                return true;
            }
            System.out.println(Thread.currentThread().getName() + " all token expired");
            return  false;
        }
    }

    private void updateToken(Bucket bucket){
        long now = System.currentTimeMillis();
        long tokenToAdd = ((now - bucket.lastRefillTime) * refillRatePerMs)/1000;

        if(tokenToAdd > 0){
            bucket.token = Math.min( maxToken , bucket.token + tokenToAdd);
            bucket.lastRefillTime = now;
        }
    }

}


class Bucket{
    Long lastRefillTime;
    long token;

    public Bucket(long maxToken){
        this.token = maxToken;
        lastRefillTime = System.currentTimeMillis();
    }


}
