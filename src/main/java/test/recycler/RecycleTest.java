package test.recycler;

import io.netty.util.Recycler;

public class RecycleTest {
    private static final Recycler<User> RECYCLER = new Recycler<User>() {
        //没有对象的时候，新建一个对象， 会传入一个handler，在Recycler池里，所有的对象都会转成DefaultHandle对象
        @Override
        protected User newObject(Handle<User> handle) {
            return new User(handle);
        }
    };

    private static class User {
        private final Recycler.Handle<User> handle;
        public long num = 123;

        public User(Recycler.Handle<User> handle) {
            this.handle = handle;
        }

        public void recycle() {
            //通过handler进行对象的回收
            handle.recycle(this);
        }
    }

    public static void main(String[] args) {
        User user = RECYCLER.get();
        //直接调用user方法进行对象的回收
        user.recycle();

        User user1 = RECYCLER.get();
        //这里会返回true
        System.out.println(user1 == user);
    }
}