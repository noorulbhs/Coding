package org.example.codingPractise.RateLimiter.slidingWindow;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class SlidingWindow {
    private final int windowSize;
    private final ConcurrentHashMap<Integer, Queue<Long>> requestMap;

    public SlidingWindow(int windowSize){
        this.windowSize = windowSize;
        this.requestMap = new ConcurrentHashMap<>();
    }

    public boolean acquire(Integer userId){
        Long currentTime = System.currentTimeMillis();
        final AtomicReference<Boolean> isValid = new AtomicReference(false);
        requestMap.compute(userId,(id,count)->{
            if(count == null){
                count = new LinkedList<>();
            }
            while(!count.isEmpty() && (currentTime - count.peek()) > 60000){
                count.poll();
            }
            if(count.size() < windowSize){
                count.offer(currentTime);
                isValid.set(true);
                return count;
            }
            return count;
        });
        return isValid.get();
    }

}
