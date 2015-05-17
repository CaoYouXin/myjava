package rotate;

import java.util.Arrays;

/**
 * Created by 又心 on 2015/5/7.
 */
public class RotateArray {

    /*
    * 向右旋转整型数组
    * @param testArray 待旋转整型数组
    * @param step 向右旋转的数组元素个数
    * */
    public static void rotating(int[] testArray, int step) {
        if (step < testArray.length / 2) {
            int[] tmp = new int[step];
            System.arraycopy(testArray, testArray.length - step, tmp, 0, step);
            System.arraycopy(testArray, 0, testArray, step, testArray.length - step);
            System.arraycopy(tmp, 0, testArray, 0, step);
        } else {
            int[] tmp = new int[testArray.length - step];
            System.arraycopy(testArray, 0, tmp, 0, testArray.length - step);
            System.arraycopy(testArray, testArray.length - step, testArray, 0, step);
            System.arraycopy(tmp, 0, testArray, step, testArray.length - step);
        }
    }

    public static void main(String[] args) {
        int[] testArray = {32, 22, 10, 20, 80, 9, 5};

        System.out.println("before rotating, array values: " + Arrays.toString(testArray));
        rotating(testArray, 5);
        // 输出结果：[9, 5, 32, 22, 10, 20, 80]
        System.out.println("after rotating, array values: " + Arrays.toString(testArray));
    }

}
