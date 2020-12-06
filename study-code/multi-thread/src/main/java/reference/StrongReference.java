package reference;

import java.lang.ref.SoftReference;

/**
 * @version 1.0.0
 * @auther Alexander Zero
 * @date 2020/12/5
 */
public class StrongReference {
    public static void main(String[] args) {
        Object o = new Object();

        SoftReference<byte[]> softReference = new SoftReference<byte[]>();
    }
}
