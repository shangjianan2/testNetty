package study.recycler.again;

import io.netty.util.Recycler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 断点2 主线程回收池中会有大量CyclerA
 */
public class CycliMutiThread5 {
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
        ConcurrentLinkedQueue<Object> q = new ConcurrentLinkedQueue();
        Thread t = Thread.currentThread();
        Thread thread = new Thread(() -> {
            Thread tt = t;
            System.out.println("begin");
            // 1、从回收池获取对象
            CyclerA cycler1 = CyclerRecyclerA.get();
            // 2、开始使用对象
            cycler1.setValue("hello,java");
            // 3、将对象放到队列中
            q.add(cycler1);

            // 4、再放一个对象到队列中
            CyclerB cyclerB = CyclerRecyclerB.get();
            cyclerB.setValue("cyclerB");
            q.add(cyclerB);

            System.out.println("end");//断点1
        });
        thread.start();
        thread.join();

        CyclerA cycler3 = (CyclerA) q.poll();
        // 5、回收对象到对象池
        cycler3.recycle();

        CyclerB cycler4 = (CyclerB) q.poll();
        // 5、回收对象到对象池
        cycler4.recycle();

        // 6、随便放一个其他对象到回收池中
        CyclerC cyclerC = CyclerRecyclerC.get();
        cyclerC.setValue("cyclerC");
        cyclerC.recycle();


        List<CyclerA> list = new ArrayList<>();
        for (int i = 0; i < 901; ++i) {
            list.add(CyclerRecyclerA.get());
        }
        for (int i = 0; i < 901; ++i) {
            list.get(i).recycle();
        }
        System.out.println("over");//断点2
    }
}