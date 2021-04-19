package com.zzn.guli.upload.leetcode.easy;

import java.util.*;


public class Solution {
    /**
     * 
     * @param numbers int整型一维数组 
     * @param target int整型 
     * @return int整型一维数组
     */
    public static int[] twoSum (int[] numbers, int target) {
        // write code here
        Map<Integer,Integer> map=new HashMap();
        for (int i = 0; i < numbers.length; i++) {
            int numberI = numbers[i];
            if(map.containsKey(target-numberI)){
                return new int[]{map.get(target-numberI)+1,i+1};
            }
            if(numberI<=target){
                map.put(numberI,i);
            }
        }
        return new int[]{};
    }

    public static int findK (int[] numbers){


        return 0;
    }

    public static void main(String[] args) {
        int[] ints = twoSum(new int[]{0,4,3,0}, 0);
        System.out.println(Arrays.toString(ints));
    }
}