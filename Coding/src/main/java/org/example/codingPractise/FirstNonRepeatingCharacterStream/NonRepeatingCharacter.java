package org.example.codingPractise.FirstNonRepeatingCharacterStream;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class NonRepeatingCharacter {
    //a,a,b,c,b
    //a,-1,b,b,c

    private HashMap<Character,Integer> freqCharacter;
    private Queue<Character> nonRepeatingCharacterQueue;

    public NonRepeatingCharacter(){
        this.freqCharacter = new HashMap<>();
        this.nonRepeatingCharacterQueue = new LinkedList<>();
    }

    public void insert(char c) {
        if (freqCharacter.containsKey(c)) {
            freqCharacter.put(c, freqCharacter.get(c) + 1);
        } else {
            freqCharacter.put(c, 1);
            nonRepeatingCharacterQueue.offer(c);
        }
    }

    public char firstNonRepeating(){
        while(!nonRepeatingCharacterQueue.isEmpty()){
            if(freqCharacter.get(nonRepeatingCharacterQueue.peek()) == 1){
                return nonRepeatingCharacterQueue.peek();
            }
            nonRepeatingCharacterQueue.poll();
        }
        return '-';
    }
}
