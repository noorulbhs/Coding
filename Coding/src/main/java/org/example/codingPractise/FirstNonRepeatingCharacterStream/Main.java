package org.example.codingPractise.FirstNonRepeatingCharacterStream;

public class Main {
    public static void main(String[] args) {
        String str ="aabcb";
        NonRepeatingCharacter nonRepeatingCharacter = new NonRepeatingCharacter();
        for(int i=0;i<str.length();i++) {
            nonRepeatingCharacter.insert(str.charAt(i));
            char ch = nonRepeatingCharacter.firstNonRepeating();
            if (ch == '-') {
                System.out.println(-1);
            } else {
                System.out.println(ch);
            }
        }
    }

}
