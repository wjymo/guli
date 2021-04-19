package com.zzn.guli.sort.medium;

import java.util.Arrays;
import java.util.Random;

public class QuickSort {
    public static void sort(int[] arr) {
        sort(arr, 0, arr.length-1);
    }

    private static void sort(int[] arr, int l, int r) {
        if(l>=r){
            return;
        }
        int partion = partion(arr, l, r);
        sort(arr,l,partion-1);
        sort(arr,partion+1,r);

    }

    private static int partion(int[] arr, int l, int r) {
        // arr[l+1...j] < v ; arr[j+1...i] >= v
        int j = l;
        for (int i = l + 1; i <= r; i++) {
            //如果i指针对应的数据小于被比较的v，要将i对应的数据移动至前半段小于v的数据段中，就是讲i于j的位置交换
            if (arr[i] < arr[l]) {
                j++;
                swap(arr, i, j);
            }
        }
        //最后交换被比较的v的索引l和小于v的最后一个的数据的索引,最后j的位置就是v
        swap(arr, l, j);
        return j;
    }


    public static void sort2ways(int[] arr) {
        Random rnd=new Random();
        sort2ways(arr, 0, arr.length-1,rnd);
    }

    private static void sort2ways(int[] arr, int l, int r, Random rnd) {
        if(l>=r){
            return;
        }
        int partion = partion2ways(arr, l, r,rnd);
        sort(arr,l,partion-1);
        sort(arr,partion+1,r);

    }

    private static int partion2ways(int[] arr, int l, int r, Random rnd) {
        int p = l + rnd.nextInt(r - l + 1);
        swap(arr,l,p);

        int i=l+1,j=r;
        while (true){
            while (i<=j&&arr[l]>arr[i]){
                i++;
            }
            while (i<=j&&arr[l]<arr[j]){
                j--;
            }
            if(i>=j){
                break;
            }
            swap(arr,i,j);
            i++;
            j--;
        }
        swap(arr,l,j);
        return j;
    }

    public static void main(String[] args) {
        int[] arr = new int[]{4, 6, 1, 3, 7, 2, 5,10,9,13};
//        int partion = partion(arr, 0, arr.length-1);
//        System.out.println(partion);
        sort2ways(arr);
        System.out.println(Arrays.toString(arr));
    }


    private static void swap(int[] arr, int i, int j) {

        int t = arr[i];
        arr[i] = arr[j];
        arr[j] = t;
    }
}
