package reference;

import javax.swing.plaf.TreeUI;
import java.lang.ref.*;

/**
 * @version 1.0.0
 * @auther Alexander Zero
 * @date 2020/12/5
 */
public class StrongReference {
    public static void main(String[] args) {
Object o = new Object();
ReferenceQueue<Object> queue = new ReferenceQueue<>();//作用
SoftReference<Object> softReference = new SoftReference<>(new Object(), queue);//可不传queue
WeakReference<Object> weakReference = new WeakReference<>(new Object(), queue);//可不传queue
PhantomReference<Object> phantomReference = new PhantomReference<>(new Object(), queue);//必传queue
Object object = phantomReference.get();//虚引用无法获取object,即object=null

new Thread(() -> {
    while (true) {
        Reference<?> poll = queue.poll();
        if (poll != null) {
            System.out.println("---对象被回收了---");
        }
    }
}).start();
    }
}
