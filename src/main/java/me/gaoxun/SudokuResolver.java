/**
 * @author: GaoXun
 * @email: gao.x@live.com
 */
package me.gaoxun;

import java.util.*;

/**
 * Provide a sudoku solution which use stack
 */
@SuppressWarnings("unchecked")
public class SudokuResolver {
    private static HashSet<Integer> FIXED_LIST = new HashSet<>();

    static {
        for (int i = 1; i < 10; ++i) FIXED_LIST.add(i);
    }

    private static class SudoStackFrame {
        int x;
        int y;
        Iterator<Integer> currIt;

        SudoStackFrame(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * use 2-dim array to store the value
     */
    private int[][] nodes = new int[10][10];

    /**
     * record the number that appears in the corresponding column
     */
    private Set<Integer>[] v = new HashSet[10];
    /**
     * record the number that appears in the corresponding row
     */
    private Set<Integer>[] h = new HashSet[10];
    /**
     * record the number that appears in the corresponding grid which size is 9
     */
    private Set<Integer>[][] a = new HashSet[3][3];
    /**
     * use stack to store the assume number
     * the top of the stack store the coordinate which will be proceeded
     */
    private Deque<SudoStackFrame> stack;
    /**
     * Store the maximum count of stack.
     * if the size of stack reaches this value, if should be correct answer or recall the process.
     */
    private int fullStackSize = 0;

    /**
     * @param vals the format of the array element is a number between 111-999
     *             and the number 321 replace that <code>nodes[3][2] = 1</code>
     */
    public SudokuResolver(int[] vals) {
        for (int i = 1; i < 10; ++i) {
            for (int j = 1; j < 10; ++j) {
                nodes[i][j] = 0;
            }
            h[i] = new HashSet<>();
            v[i] = new HashSet<>();
        }
        for (int i = 0; i < 3; ++i)
            for (int j = 0; j < 3; ++j)
                a[i][j] = new HashSet<>();
        for (int val : vals) {
            int x = val / 100;
            int v = val % 10;
            int y = (val / 10) % 10;
            this.set(x, y, v);
        }
        this.fullStackSize = 81 - vals.length;
        stack = new ArrayDeque<>(this.fullStackSize);
    }

    /**
     * add the specified value to nodes[x][y] and set the corresponding Set
     *
     * @param x
     * @param y
     * @param val
     */
    private void set(int x, int y, int val) {
        h[x].add(val);
        v[y].add(val);
        getSpecifiedArea(x, y).add(val);
        nodes[x][y] = val;
    }

    /**
     * add the specified value to nodes[frame.x][frame.y] and set the corresponding Set
     * <p>
     * the method will call the <code>next</code> method of <code>Iterator</code>
     * it means that you must assume that the <code>hasNext</code> method of Iterator is <code>true</code>.
     *
     * @param frame
     */
    private void set(SudoStackFrame frame) {
        this.set(frame.x, frame.y, frame.currIt.next());
    }

    /**
     * remove the value nodes[frame.x][frame.y] and remove it from the corresponding Set
     *
     * @param frame
     */
    private void unset(SudoStackFrame frame) {
        int x = frame.x, y = frame.y;
        int oldVal = nodes[x][y];
        h[x].remove(oldVal);
        v[y].remove(oldVal);
        getSpecifiedArea(x, y).remove(oldVal);
        nodes[x][y] = 0;
    }

    private Set<Integer> getSpecifiedArea(int x, int y) {
        int _x = (int) Math.floor((x - 0.5) / 3);
        int _y = (int) Math.floor((y - 0.5) / 3);
        return a[_x][_y];
    }

    public void resolve() {
        SudoStackFrame currFrame = this.getNextFrame(1, 1);
        stack.addLast(currFrame);
        while (stack.size() > 0) {
            currFrame = stack.getLast();
            if (currFrame.currIt.hasNext()) {
                this.set(currFrame);
                if (stack.size() == fullStackSize) {
                    break;
                }
                SudoStackFrame nextFrame = this.getNextFrame(currFrame.x, currFrame.y);
                stack.add(nextFrame);
            } else {
                stack.removeLast();
                if (stack.size() > 0) this.unset(stack.getLast());
            }
        }
    }

    private SudoStackFrame getNextFrame(int x, int y) {
        int j = y;
        for (int i = x; i < 10 && nodes[x][y] != 0; ++i) {
            for (; j < 10; ++j) {
                if (nodes[i][j] == 0) {
                    x = i;
                    y = j;
                    break;
                }
            }
            j = 1;
        }
        SudoStackFrame nextFrame = new SudoStackFrame(x, y);
        Set<Integer> leftPossible = new HashSet<>(FIXED_LIST);
        leftPossible.removeAll(h[nextFrame.x]);
        leftPossible.removeAll(v[nextFrame.y]);
        leftPossible.removeAll(getSpecifiedArea(nextFrame.x, nextFrame.y));
        nextFrame.currIt = leftPossible.iterator();
        return nextFrame;
    }

    /**
     * Check the validation of computed result
     *
     * @return
     */
    @SuppressWarnings("Duplicates")
    public boolean isValid() {
        Set<Integer> set = new HashSet<>();
        for (int i = 1; i < 10; ++i) {
            set.clear();
            for (int j = 1; j < 10; ++j) set.add(nodes[i][j]);
            if (set.size() != 9 || set.contains(0)) return false;
        }
        for (int i = 1; i < 10; ++i) {
            set.clear();
            for (int j = 1; j < 10; ++j) set.add(nodes[j][i]);
            if (set.size() != 9 || set.contains(0)) return false;
        }
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                set.clear();
                for (int m = 1; m <= 3; ++m) {
                    for (int n = 1; n <= 3; ++n) {
                        set.add(nodes[m + i * 3][n + j * 3]);
                    }
                }
                if (set.size() != 9 || set.contains(0)) return false;
            }
        }
        return true;
    }

    public void print() {
        for (int i = 1; i < 10; ++i) {
            for (int j = 1; j < 10; ++j) {
                System.out.print(nodes[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        int[] vals = new int[]{
                156, 182,
                237, 245, 263, 276,
                331, 347, 354, 393,
                423, 459, 472,
                512, 568,
                628, 653, 695,
                721, 735, 748, 773,
                818, 832, 894,
                926, 933, 985
        };

        SudokuResolver resolver = new SudokuResolver(vals);
        resolver.resolve();
        resolver.print();
    }
}
