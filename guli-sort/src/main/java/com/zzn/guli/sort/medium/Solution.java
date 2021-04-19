package com.zzn.guli.sort.medium;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

class Solution {
    public static int lengthOfLongestSubstring(String s) {
        int i = 0, j = 0, maxLength = 0;
        Set<Character> set = new HashSet<>();
        for (; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!set.contains(c)) {
                set.add(c);
                maxLength = Math.max(maxLength, set.size());
            } else {
                while (set.contains(c)) {
                    char cj = s.charAt(j);
                    set.remove(cj);
                    j++;
                }
                set.add(c);
            }
        }
        return maxLength;
    }

    public static String longestPalindrome(String string) {
        if (null == string || string.length() < 1) return "";
        int start = 0, end = 0;
        int num1 = 0, num2 = 0, len, max = 0;
        for (int i = 0; i < string.length(); i++) {
            int left = i;
            int right = i;
            while (left >= 0 && right < string.length() && string.charAt(left)==string.charAt(right)) {
                left--;
                right++;
            }
//            if (left < 0) {
//                continue;
//            }
            len = right - left - 1;
            if(len>max){
                max=len;
                start = left + 1;
            }
//            num1 = getMaxLength(string, i, i);
//            num2= getMaxLength(string, i, i+1);
//            len=Math.max(num1,num2);
//            if(len>end-start){
//
//            }
        }
        return string.substring(start, max);
    }

    public static int getMaxLength(String s, int left, int right) {
        int l = left;
        int r = right;
        while (l >= 0 && r < s.length() && Objects.equals(s.charAt(l), s.charAt(r))) {
            l--;
            r++;
        }
        if (l < 0) {
            return 0;
        }
        return r - l + 1;
    }

    public static void main(String[] args) {
        String string = "abcba";
//        int i = lengthOfLongestSubstring(string);
//        System.out.println(i);
        for (int i = 0; i < string.length(); i++) {
//            System.out.println(abbcd);
        }
        String s = longestPalindrome(string);

//        int abbcd2 = getMaxLength("abbcd", 0, 1);
    }
}