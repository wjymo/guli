package com.zzn.guli.sort.thread;

import lombok.Setter;

import java.util.concurrent.locks.LockSupport;

class FooBar2 {
    private int n;
    private volatile boolean finish=false;
    public FooBar2(int n) {
        this.n = n;
    }

    public void foo(Runnable printFoo) throws InterruptedException {
        
        for (int i = 0; i < n; i++) {
            
        	// printFoo.run() outputs "foo". Do not change or remove this line.
            while (finish){
                Thread.yield();
            }
        	printFoo.run();
            finish=true;
        }
    }

    public void bar(Runnable printBar) throws InterruptedException {
        
        for (int i = 0; i < n; i++) {
            
            // printBar.run() outputs "bar". Do not change or remove this line.
            while (!finish){
                Thread.yield();
            }
            printBar.run();
            finish=false;
        }
    }
    static class FooRunnable implements Runnable{
        @Setter
        private Thread t;

        @Override
        public void run() {
            LockSupport.park(t);
            System.out.println("foo");
            LockSupport.unpark(t);
        }
    }
    static class BarRunnable implements Runnable{
        @Setter
        private Thread t;

        @Override
        public void run() {
            LockSupport.park(t);
            System.out.print("bar");
            LockSupport.unpark(t);
        }
    }

    public static void main(String[] args) {
        FooBar2 fooBar2 = new FooBar2(4);
        Thread fooThread = new Thread(()->{
            try {
                fooBar2.foo(()->{
                    System.out.print("foo");
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        Thread barThread = new Thread(()->{
            try {
                fooBar2.bar(()->{
                    System.out.print("bar");
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        fooThread.start();
        barThread.start();

    }
}

