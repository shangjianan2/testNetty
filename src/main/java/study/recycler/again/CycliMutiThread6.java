package study.recycler.again;

import io.netty.util.Recycler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 断点2 主线程回收池中会有大量CyclerA
 */
public class CycliMutiThread6 {
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
        ConcurrentLinkedQueue<CyclerA> qAThread = new ConcurrentLinkedQueue();
        ConcurrentLinkedQueue<CyclerB> qBThread = new ConcurrentLinkedQueue();
        ConcurrentLinkedQueue<CyclerA> qAThread1 = new ConcurrentLinkedQueue();
        ConcurrentLinkedQueue<CyclerB> qBThread1 = new ConcurrentLinkedQueue();
        Thread t = Thread.currentThread();
        Thread thread = new Thread(() -> {
            for (int i = 0; i < 901; ++i) {
                CyclerA cyclerA = CyclerRecyclerA.get();
                qAThread.add(cyclerA);
            }
            for (int i = 0; i < 901; ++i) {
                CyclerB cyclerB = CyclerRecyclerB.get();
                qBThread.add(cyclerB);
            }
        });
        thread.start();
        thread.join();

        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 901; ++i) {
                CyclerA cyclerA = CyclerRecyclerA.get();
                qAThread1.add(cyclerA);
            }
            for (int i = 0; i < 901; ++i) {
                CyclerB cyclerB = CyclerRecyclerB.get();
                qBThread1.add(cyclerB);
            }
        });
        thread1.start();
        thread1.join();

        for (int i = 0; i < 901; ++i) {
            CyclerA poll = qAThread.poll();
            poll.recycle();
        }

        for (int i = 0; i < 901; ++i) {
            CyclerB poll = qBThread.poll();
            poll.recycle();
        }

        for (int i = 0; i < 901; ++i) {
            CyclerA poll = qAThread1.poll();
            poll.recycle();
        }

        for (int i = 0; i < 901; ++i) {
            CyclerB poll = qBThread1.poll();
            poll.recycle();
        }

        System.out.println("over");//断点2
    }
}