package test.recycler;

import io.netty.util.Recycler;
import io.netty.util.concurrent.FastThreadLocalThread;

import java.util.ArrayList;
import java.util.List;

public class RecycleInterval {
    private static final Recycler<User> RECYCLER = new Recycler<User>() {
        //没有对象的时候，新建一个对象， 会传入一个handler，在Recycler池里，所有的对象都会转成DefaultHandle对象
        @Override
        protected User newObject(Handle<User> handle) {
            return new User(handle);
        }
    };
    private static ThreadLocal<Long> numLong = new ThreadLocal<Long>();

    private static class User {
        private final Recycler.Handle<User> handle;
        public long a1 = 1;//1kb
        public long a2 = 2;
        public long a3 = 1;//1kb
        public long a4 = 2;

        public long a5 = 1;//1kb
        public long a6 = 2;
        public long a7 = 1;//1kb
        public long a8 = 2;


        public User(Recycler.Handle<User> handle) {
            this.handle = handle;
        }

        public void recycle() {
            //通过handler进行对象的回收
            handle.recycle(this);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        FastThreadLocalThread thread = new FastThreadLocalThread(new Runnable() {
            public void run() {
                try {
                    for (int i = 0; i < 1024; ++i) {
                        User user = RECYCLER.get();//这个对象没有回收
                        user.recycle();
                        User user1 = RECYCLER.get();
                        System.out.println(i + " " + String.valueOf(user == user1));
                        Thread.sleep(1000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        thread.join();
        List<Long> list1 = new ArrayList<Long>();
        while (true) {
            try {
                list1.add(1L);list1.add(1L);list1.add(1L);list1.add(1L);
                list1.add(1L);list1.add(1L);list1.add(1L);list1.add(1L);
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}