package deldup;

import java.util.Arrays;

/**
 * Created by 又心 on 2015/5/7.
 */
public class DeleteDuplicateValues {

    /*
    * 删除有序数组中的重复元素
    * @param testArray 待处理的整型数组
    * */
    public static int[] deleteDuplicateValues(int[] testArray) {
        int length = testArray.length;
        int now = testArray[0] - 1;
        int point = 0;
        for (int i = 0; i < length; i++) {
            if (now != testArray[i]) {
                now = testArray[i];
                testArray[point++] = testArray[i];
            }
        }
        int[] newData = new int[point];
        System.arraycopy(testArray, 0, newData, 0, point);
        return newData;
    }

    public static void main(String[] args) {
        int[] testArray = {1, 3, 3, 10, 20, 32, 32, 80, 100};

        System.out.println("before deleting, array values: " + Arrays.toString(testArray));
        testArray = deleteDuplicateValues(testArray);
        // 输出结果：[1, 3, 10, 20, 32, 80, 100];
        System.out.println("after deleting, array values: " + Arrays.toString(testArray));
    }

}
