package test.recycler;

import io.netty.util.Recycler;
import io.netty.util.concurrent.FastThreadLocalThread;

import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.List;

/**
 * unreachable Recycler$DefaultHandle
 */
public class RecycleNoRelease1 {
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
        try {
            //此处添加延时是为了让我来得及打开JVisualVM去查看动态变化并使用jmap生成dump文件
            Thread.sleep(20000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("begin");
        FastThreadLocalThread thread = new FastThreadLocalThread(new Runnable() {
            public void run() {
                long nnn = 1024 * 32;
                List<User> list1 = new ArrayList<User>();
                for (int i = 0; i < nnn; ++i) {
                    list1.add(RECYCLER.get());
                }
                for (int i = 0; i < nnn; ++i) {
                    list1.get(i).recycle();
                }
            }
        });

        thread.start();
        thread.join();
        System.out.println("=======================over===================================");

        while (true) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}