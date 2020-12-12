package study.recycler.again;

import io.netty.util.Recycler;

public class Cycli {
    private static final Recycler<Cycler> CyclerRecycler = new Recycler<Cycler>() {
        @Override
        protected Cycler newObject(Handle<Cycler> handle) {
            return new Cycler(handle);
        }
    };
    static final class Cycler {
        private String value;
        public void setValue(String value) {
            this.value = value;
        }
        private Recycler.Handle<Cycler> handle;
        public Cycler(Recycler.Handle<Cycler> handle) {
            this.handle = handle;
        }
        public void recycle() {
            handle.recycle(this);
        }
    }
    public static void  main(String[] args) {
        Thread t = Thread.currentThread();
        // 1、从回收池获取对象
        Cycler cycler1 = CyclerRecycler.get();
        // 2、开始使用对象
        cycler1.setValue("hello,java");
        // 3、回收对象到对象池
        cycler1.recycle();
        // 4、从回收池获取对象
        Cycler cycler2 = CyclerRecycler.get();
        //比较从对象池中获取的对象即为之前释放的对象
        System.out.print(cycler1 == cycler2);
    }
}
