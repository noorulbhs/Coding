/*
 * ============================================================================
 * EVALUATION SUMMARY - FixedWindowRateLimiter.java
 * ============================================================================
 *
 * MARKS: 5.5/10
 *
 * SUMMARY OF FEEDBACK:
 * ❌ CRITICAL BUG: Logic error in acquire() allows count to exceed maxRequests
 * ❌ CRITICAL BUG: Race condition between requestCounts and windowStartTimes updates
 * ❌ MAJOR ISSUE: Time and Space Complexity NOT documented
 * ⚠️ OPTIMIZATION: Hard-coded constant "60000" should use a constant field
 * ⚠️ OPTIMIZATION: Redundant logic in compute() - simplify null checks
 * ⚠️ CODE QUALITY: Unused field 'windowSize' - ambiguous units
 * ✅ GOOD: Correctly uses ConcurrentHashMap for thread-safety
 * ✅ GOOD: Clear method separation with acquire() and execute()
 *
 * DEDUCTIONS:
 * - Logic bug allowing over-limit requests: -2 points
 * - Race condition in window reset logic: -1 point
 * - Missing complexity analysis: -1.5 points
 *
 * ============================================================================
 */

package org.example.codingPractise.RateLimiter.FixedWindow;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class FixedWindowRateLimiter {
    private final int maxRequests;
    // ⚠️ OPTIMIZATION: Hard-coded values should be named constants for clarity and maintainability
    // Use TimeUnit for explicit conversions instead of raw milliseconds (60000 is unclear)
    // Original: private final int windowSize = 60; // 1 minute window
    private static final long WINDOW_DURATION_MILLIS = TimeUnit.MINUTES.toMillis(1); // 60000ms - much clearer!
    // Why: Constants make code self-documenting, and TimeUnit prevents mistakes in time conversions

    private final ConcurrentHashMap<Integer, Integer> requestCounts; // userId -> request count
    private final ConcurrentHashMap<Integer, Long> windowStartTimes; // userId -> window start time

    public FixedWindowRateLimiter(int maxRequests) {
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

            // ❌ CRITICAL BUG ON NEXT LINE - Logic Error
            // return reqCount != null ? reqCount +1:1;
            //
            // PROBLEM: This line ALWAYS increments the count, even when it's already >= maxRequests!
            // When reqCount >= maxRequests, the condition on line 33 is false, so we reach this line.
            // This causes the stored counter to grow beyond maxRequests, leading to inconsistent behavior.
            //
            // EXAMPLE:
            // - maxRequests = 5
            // - Requests 1-5: stored value = 1,2,3,4,5 (all allowed)
            // - Request 6: reqCount=5, condition "5 < 5" is FALSE, so we execute this buggy line
            //   Result: stored value becomes 6! But acquire() returns FALSE (6 > 5)
            // - Request 7: reqCount=6, again we reach this line, stored value becomes 7
            // - This violates the rate limit contract and causes unbounded counter growth
            //
            // CORRECTED CODE:
            // return reqCount; // Simply return the current count WITHOUT incrementing
            //
            // WHY THIS FIX: If the request should be denied (count >= maxRequests), we must NOT
            // increment the stored value. Only increment when the request is actually allowed.
            // This keeps the stored state consistent with the allow/deny decision.
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

/*
 * ============================================================================
 * COMPLEXITY ANALYSIS
 * ============================================================================
 *
 * ❌ MISSING IN YOUR CODE - Deduction: -1.5 points
 *
 * TIME COMPLEXITY:
 * - acquire(userId): O(1) average case
 *   Reason: ConcurrentHashMap.compute() and get() operations are O(1) on average
 *   The window check and counter increment are constant-time operations
 *   However, note: Under high contention, ConcurrentHashMap may degrade slightly due to locking
 *
 * - execute(userId): O(1) average case
 *   Simply calls acquire() which is O(1), plus a println() which is O(1)
 *
 * SPACE COMPLEXITY: O(N)
 * - N = number of unique users making requests
 * - We store 2 HashMap entries per user: one in requestCounts, one in windowStartTimes
 * - Each entry stores: userId (Integer) and either a counter (Integer) or timestamp (Long)
 * - Space per user: 2 * (key + value) ≈ O(1) per user
 * - Total: O(N) where N is the number of unique users
 *
 * POTENTIAL ISSUE: Unbounded growth if old users are never evicted!
 * If a user makes a request once and never again, their entry remains in both maps forever.
 * For a long-running system with many users, this causes memory leak.
 *
 * SUGGESTED IMPROVEMENT:
 * Add user eviction logic - remove entries older than some threshold (e.g., 24 hours).
 * This would keep space usage bounded: O(min(N, active_users_threshold))
 *
 * ============================================================================
 * ADDITIONAL BUGS & ISSUES
 * ============================================================================
 *
 * ⚠️ ISSUE 1: Race Condition in Window Reset
 * - Problem: Inside compute(), we do windowStartTimes.get(id) followed by windowStartTimes.put(id)
 * - These are NOT atomic. Multiple threads could both read null and both reset the window
 * - Result: Counter could be reset multiple times, allowing more requests than intended
 *
 * - Example:
 *   Thread 1: read lastStartTime = null, decide to reset
 *   Thread 2: read lastStartTime = null, decide to reset (same window!)
 *   Both threads set the window start time, but only one properly sets count to 1
 *
 * - CORRECTED APPROACH:
 *   Use computeIfPresent() and computeIfAbsent() to make window reset atomic,
 *   or refactor to store both count and window-start in a single object (e.g., AtomicReference<Pair>)
 *
 * ⚠️ ISSUE 2: Hard-coded "60000" on line 27 should use WINDOW_DURATION_MILLIS constant
 * - You defined WINDOW_DURATION_MILLIS but didn't use it!
 * - if(lastStartTime == null || (currentMilliSecond - lastStartTime) > 60000){
 * + Should be: if(lastStartTime == null || (currentMilliSecond - lastStartTime) > WINDOW_DURATION_MILLIS){
 *
 * ⚠️ ISSUE 3: Redundant Null Checks in compute()
 * - Line 31: Integer reqCount = count;
 * - Line 32: if(reqCount != null && reqCount < maxRequests){
 * - This is okay, but the logic can be cleaner
 *
 * SIMPLIFIED VERSION:
 * if(count != null && count < maxRequests){
 *     return count + 1;
 * }
 * return (count != null) ? count : 1; // If denied, return current or initialize to 1
 *
 * But this still has the bug from the main issue (incrementing when denied).
 *
 * ============================================================================
 */

