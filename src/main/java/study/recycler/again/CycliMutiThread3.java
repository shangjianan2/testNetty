package study.recycler.again;

import io.netty.util.Recycler;
import io.netty.util.internal.InternalThreadLocalMap;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CycliMutiThread3 {
    private static final Recycler<CyclerA> CyclerRecyclerA = new Recycler<CyclerA>() {
        @Override
        protected CyclerA newObject(Handle<CyclerA> handle) {
            return new CyclerA(handle);
        }
    };
    static final class CyclerA {
        private String value;
        public void setValue(String value) {
            this.value = value;
        }
        private Recycler.Handle<CyclerA> handle;
        public CyclerA(Recycler.Handle<CyclerA> handle) {
            this.handle = handle;
        }
        public void recycle() {
            handle.recycle(this);
        }
    }
    private static final Recycler<CyclerB> CyclerRecyclerB = new Recycler<CyclerB>() {
        @Override
        protected CyclerB newObject(Handle<CyclerB> handle) {
            return new CyclerB(handle);
        }
    };
    static final class CyclerB {
        private String value;
        public void setValue(String value) {
            this.value = value;
        }
        private Recycler.Handle<CyclerB> handle;
        public CyclerB(Recycler.Handle<CyclerB> handle) {
            this.handle = handle;
        }
        public void recycle() {
            handle.recycle(this);
        }
    }
    private static final Recycler<CyclerC> CyclerRecyclerC = new Recycler<CyclerC>() {
        @Override
        protected CyclerC newObject(Handle<CyclerC> handle) {
            return new CyclerC(handle);
        }
    };
    static final class CyclerC {
        private String value;
        public void setValue(String value) {
            this.value = value;
        }
        private Recycler.Handle<CyclerC> handle;
        public CyclerC(Recycler.Handle<CyclerC> handle) {
            this.handle = handle;
        }
        public void recycle() {
            handle.recycle(this);
        }
    }
    public static void  main(String[] args) throws InterruptedException {
        Object test = new CycliMutiThread3().test2();
        System.out.println("over");//断点2
        Thread.sleep(20000);
    }

    private Object test2() throws InterruptedException {
        Object test = test();
        System.gc();
        return null;
    }

    private Object test() throws InterruptedException {
        final CyclerA[] temp = {null, null};
        final Object[] ooo = {null};
        Thread t = Thread.currentThread();
        Thread thread = new Thread(() -> {
            System.out.println("thread begin");
            // 1、从回收池获取对象
            CyclerA cycler1 = CyclerRecyclerA.get();
            // 2、开始使用对象
            cycler1.setValue("hello,java");
            // 3、暂存
            temp[0] = cycler1;
            System.out.println("thread end");//断点1
        });
        thread.start();
        thread.join();

        Thread thread1 = new Thread(() -> {
            System.out.println("thread1 begin");
            //4、获取上一个线程创建的对象
            CyclerA cyclerA = temp[0];
            //5、回收此对象
            cyclerA.recycle();
            System.out.println("thread1 end");
            InternalThreadLocalMap internalThreadLocalMap = InternalThreadLocalMap.get();
            List<Object> list = new ArrayList<>();
            for (int i = 0; i < 32; ++i) {
                Object object = internalThreadLocalMap.indexedVariable(i);
                if (object instanceof WeakHashMap) {
                    WeakHashMap weakHashMap = (WeakHashMap)object;

                    for (Object val : weakHashMap.values()) {
                        list.add(val);
                    }
                }
            }
            ooo[0] = list.get(0);
        });
        thread1.start();
        thread1.join();

        //6、上面两个线程运行完毕，触发gc
        return ooo[0];
    }
}
