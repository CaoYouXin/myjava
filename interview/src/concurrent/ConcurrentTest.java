package concurrent;

/**
 * Created by 又心 on 2015/5/7.
 */
public class ConcurrentTest {

    public static void main(String[] args) {
        final MyQueue queue = new MyQueue();
        final int count = 50000;

        Thread writer = new Thread() {
            @Override
            public void run() {
                int i = 0;
                while (i++ < count) {
                    queue.in(i);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Thread reader = new Thread() {
            @Override
            public void run() {
                int i = 0;
                while (i++ < count) {
                    Object out = queue.out();
                    if (null != out) {
                        System.out.println(out);
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        writer.start();
        reader.start();
    }

}

class MyQueue {

    private static class MyNode {
        private Object data;
        private MyNode next;

        public MyNode(Object data, MyNode next) {
            this.data = data;
            this.next = next;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public void setNext(MyNode next) {
            this.next = next;
        }

        public Object getData() {
            return data;
        }

        public MyNode getNext() {
            return next;
        }
    }

    private MyNode inPoint = new MyNode(null, null);
    private MyNode outPoint = new MyNode(null, inPoint);

    public void in(Object v) {
        this.inPoint.setData(v);
        this.inPoint.setNext(new MyNode(null, null));
        this.inPoint = this.inPoint.getNext();
    }

    public Object out() {
        if (this.outPoint.getNext() == this.inPoint) {
            return null;
        }

        this.outPoint = this.outPoint.getNext();
        return this.outPoint.getData();
    }
}
