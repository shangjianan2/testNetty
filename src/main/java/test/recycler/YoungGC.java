package test.recycler;

public class YoungGC {
    public static class A {
        public long[] aa = new long[128];//1k
    }
    public static void main(String[] args) throws InterruptedException {
        System.out.println("hello world!");
        for (int i  = 0;; ++i) {
            gc();
            Thread.sleep(1000);
            System.out.println(i);
        }
    }
    public static void gc() {
        for (int i = 0; i < 512; ++i) {
            A a = new A();
        }
    }
}
