package test.recycler;

import io.netty.util.Recycler;
import io.netty.util.concurrent.FastThreadLocalThread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * thread用于创建对象，thread2用于回收对象。用于引发WeakOrderQueue$Link的unreachable
 */
public class RecycleMultiThread2 {
    private static final Recycler<User> RECYCLER = new Recycler<User>() {
        //没有对象的时候，新建一个对象， 会传入一个handler，在Recycler池里，所有的对象都会转成DefaultHandle对象
        @Override
        protected User newObject(Handle<User> handle) {
            return new User(handle);
        }
    };
    private static Object lock = new Object();

    private static class User {
        private final Recycler.Handle<User> handle;
        public User(Recycler.Handle<User> handle) {
            this.handle = handle;
        }

        public void recycle() {
            //通过handler进行对象的回收
            handle.recycle(this);
        }
    }

    public static void main(String[] args) throws Exception {
        new RecycleMultiThread2().test();
        System.out.println("main over");
        while (true) {
            Thread.sleep(1000);
            System.out.println("1");
        }
    }

    public void test() throws Exception {
        ConcurrentLinkedQueue<User> q = new ConcurrentLinkedQueue();
        final int size = 17;
        System.out.println("begin");
        final List<User> list1 = new ArrayList<User>();
        FastThreadLocalThread thread = new FastThreadLocalThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < size; ++i) {
                    q.add(RECYCLER.get());
                }

                FastThreadLocalThread thread2 = new FastThreadLocalThread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < size; ++i) {
                            User poll = q.poll();
                            poll.recycle();
                        }
                        System.out.println("thread2 over");
                    }
                });

                thread2.start();
                try {
                    thread2.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Thread t = Thread.currentThread();
                q.add(RECYCLER.get());
                System.out.println("1111");
            }
        });
        thread.start();
        Thread.sleep(1000);


        thread.join();
        System.out.println("=======================over===================================");


    }
}