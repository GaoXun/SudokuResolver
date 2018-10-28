/**
 * @author: GaoXun
 * @email: gao.x@live.com
 */

package me.gaoxun;

import java.util.HashSet;
import java.util.Set;

/**
 * This is an optimized Sudoku solution which use recursive method
 * The detailed docs is same as {@link me.gaoxun.SudokuResolver}
 */
public class SudokuResolverRecursive2 {
    private static Set<Integer> FIXED_LIST = new HashSet<>();

    static {
        for (int i = 1; i < 10; ++i) FIXED_LIST.add(i);
    }

    private boolean resolved = false;
    private int[][] nodes = new int[10][10];
    private Set<Integer>[] v = new HashSet[10];
    private Set<Integer>[] h = new HashSet[10];
    private Set<Integer>[][] a = new HashSet[3][3];

    public SudokuResolverRecursive2(int[] vals) {
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
    }

    public void resolve() {
        this._resolve(1, 1);
    }

    private void _resolve(int x, int y) {
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

        if (nodes[x][y] != 0) {
            resolved = true;
            return;
        }
        HashSet<Integer> probables = new HashSet<>(FIXED_LIST);
        probables.removeAll(h[x]);
        probables.removeAll(v[y]);
        probables.removeAll(getSpecifiedArea(x, y));
        for (Integer p : probables) {
            if (resolved) return;
            this.set(x, y, p);
            this._resolve(x, y);
            if (!resolved) this.unset(x, y);
        }
    }

    private void set(int x, int y, int val) {
        getSpecifiedArea(x, y).add(val);
        v[y].add(val);
        h[x].add(val);
        nodes[x][y] = val;
    }

    private void unset(int x, int y) {
        int oldVal = nodes[x][y];
        nodes[x][y] = 0;
        v[y].remove(oldVal);
        h[x].remove(oldVal);
        getSpecifiedArea(x, y).remove(oldVal);
    }

    private Set<Integer> getSpecifiedArea(int x, int y) {
        int _x = (int) Math.floor((x - 0.5) / 3);
        int _y = (int) Math.floor((y - 0.5) / 3);
        return a[_x][_y];
    }

    private boolean isValid() {
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

        SudokuResolverRecursive2 resolver = new SudokuResolverRecursive2(vals);
        resolver.resolve();
        resolver.print();
    }
}
