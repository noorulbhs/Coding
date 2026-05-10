package org.example.codingPractise.RateLimiter.FixedWindow;

import java.util.concurrent.ConcurrentHashMap;

public class FixedWindow {
    private final int maxRequests;
    private final int windowSize = 60; // 1 minute window
    private final ConcurrentHashMap<Integer, Integer> requestCounts; // userId -> request count
    private final ConcurrentHashMap<Integer, Long> windowStartTimes; // userId -> window start time

    public FixedWindow(int maxRequests) {
        this.maxRequests = maxRequests;
        this.requestCounts = new ConcurrentHashMap<>();
        this.windowStartTimes = new ConcurrentHashMap<>();
    }

    private boolean acquire(Integer userId){
        Long currentMilliSecond = System.currentTimeMillis();
        Integer currentCount = requestCounts.compute(userId,(id,count)->{
            Long lastStartTime = windowStartTimes.get(id);
            if(lastStartTime == null || (currentMilliSecond - lastStartTime) > 60000){
                windowStartTimes.put(id,currentMilliSecond);
                return 1;
            }
            Integer reqCount = count;
            if(reqCount != null && reqCount < maxRequests){
                return reqCount+1;
            }

            return reqCount != null ? reqCount +1:1;
        });
        return currentCount <= maxRequests;
    }

    public void execute(Integer userId){
        if(acquire(userId)){
            System.out.println(Thread.currentThread().getName()+" Able to proceed");
        }else{
            System.out.println(Thread.currentThread().getName()+ " Denied! -> Exhausted all the slots ");
        }
    }
}

// REVIEW COMMENTS (non-functional, do not change behavior):
// 1) Thread-safety: `windowStartTimes` is a plain `HashMap` while `requestCounts` is a `ConcurrentHashMap`.
//    Accessing and mutating `windowStartTimes` from multiple threads will cause data races and undefined behavior.
//    Suggested: use `ConcurrentHashMap<Integer, Long>` for `windowStartTimes` or synchronize access per-user.
//    // Corrected (suggested) declaration (commented-out):
//    // private final ConcurrentHashMap<Integer, Long> windowStartTimes = new ConcurrentHashMap<>();

// 2) Hard-coded window duration and unused `windowSize`: the code uses `60000` (milliseconds) directly.
//    Prefer using `windowSize` consistently and clearly (e.g., `windowSizeSeconds` or `windowDurationMillis`).
//    // Corrected (suggested):
//    // private final long windowDurationMillis = TimeUnit.SECONDS.toMillis(windowSize);

// 3) compute(...) misuse: inside the `compute` lambda you call `requestCounts.get(id)` even though `count` parameter
//    already represents the current value for that key. Using `requestCounts.get` inside the compute may produce confusing
//    semantics and is less efficient. Use `count` directly.
//    Also, the lambda increments the count even when the request should be denied (i.e., when count >= maxRequests).
//    This means the stored counter will grow past the `maxRequests` value. It's better to avoid incrementing when over limit.

// 4) Logic for allowing / denying: the method returns `cuurentCount <= maxRequests` after compute. Because the lambda
//    increments the stored count even when it's already at/over the limit, the decision and the stored state can become inconsistent.
//    Better approach: only increment when the request is allowed; otherwise keep the count unchanged and return denial.

// 5) Typo & naming: `cuurentCount` is misspelled; `Exahausted` spelled incorrectly in `execute`. Also `windowSize` comment is ambiguous.
//    These don't affect runtime but reduce readability. Consider renaming `windowSize` to `windowSizeSeconds` or `windowDurationMillis`.

// 6) Race between check and update for `windowStartTimes`: because `windowStartTimes` is not thread-safe, multiple threads can reset
//    the window for the same user concurrently and incorrectly set counts. Use atomic per-user update or `ConcurrentHashMap`.

// Suggested corrected `acquire` implementation (commented-out) using ConcurrentHashMap and compute:
/*
private final ConcurrentHashMap<Integer, Long> windowStartTimes = new ConcurrentHashMap<>();

private boolean acquire(Integer userId) {
    final long now = System.currentTimeMillis();
    final long windowMillis = windowSize * 1000L; // if windowSize is seconds

    // compute atomically the new count for this user
    int newCount = requestCounts.compute(userId, (id, count) -> {
        Long start = windowStartTimes.get(id);
        if (start == null || now - start >= windowMillis) {
            // new window
            windowStartTimes.put(id, now);
            return 1; // first request in new window
        }
        int current = (count == null) ? 0 : count;
        if (current >= maxRequests) {
            // don't increment - request should be denied and the stored count stays the same
            return current;
        }
        return current + 1; // allowed, increment
    });

    // If the stored value is > maxRequests it means it was incremented incorrectly; in our scheme above
    // we keep the stored value <= maxRequests. To decide allow/deny we check the previous value atomically
    // which is non-trivial inside compute; an alternative is to use compute and return a pair or use a dedicated
    // object (AtomicInteger) as the map value. For clarity, the following pattern is recommended instead:
}
*/

// Recommended clearer implementation using AtomicInteger as the map value (commented-out):
/*
private final ConcurrentHashMap<Integer, java.util.concurrent.atomic.AtomicInteger> requestCountsAtomic = new ConcurrentHashMap<>();
private final ConcurrentHashMap<Integer, Long> windowStartTimes = new ConcurrentHashMap<>();

private boolean acquire(Integer userId) {
    final long now = System.currentTimeMillis();
    final long windowMillis = windowSize * 1000L;

    // Ensure start time is set atomically for the user
    windowStartTimes.compute(userId, (id, start) -> {
        if (start == null || now - start >= windowMillis) {
            // reset window
            requestCountsAtomic.put(id, new java.util.concurrent.atomic.AtomicInteger(0));
            return now;
        }
        return start;
    });

    java.util.concurrent.atomic.AtomicInteger counter = requestCountsAtomic.computeIfAbsent(userId, id -> new java.util.concurrent.atomic.AtomicInteger(0));
    int current = counter.incrementAndGet();
    if (current <= maxRequests) {
        return true; // allowed
    } else {
        // We exceeded the limit - roll back the increment to keep accurate counts (optional):
        counter.decrementAndGet();
        return false; // denied
    }
}
*/

// Small cosmetic fixes (non-functional suggestions):
// - rename `cuurentCount` -> `currentCount` and use `windowSize` variable consistently
// - fix spelling in messages: "Denied! -> Exhausted all the slots"

// Suggested `execute` message correction (commented-out):
// System.out.println(Thread.currentThread().getName() + " Denied! -> Exhausted all the slots");

// Suggested unit tests to add (in README / tests):
// - Single-thread: send exactly `maxRequests` requests within window and ensure allowed, 1 more denied.
// - Multi-thread: many concurrent requests for the same user; ensure at most `maxRequests` are allowed within window.
// - Window rollover: requests before/after the window boundary behave correctly (counts reset).

// End of review comments.
