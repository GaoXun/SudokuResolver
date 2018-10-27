/*
 * @Author: GaoXun
 * @Email: gao.x@live.com
 */

package me.gaoxun;

import java.util.HashSet;
import java.util.Set;

/**
 * Provide a Sudoku solution which use recursive method
 */
public class SudoResolverRecursive {
    private static class SudoNode {
        static Set<Integer> FIXED_LIST = new HashSet<>();

        static {
            for (int i = 1; i < 10; ++i) FIXED_LIST.add(i);
        }

        int val;
        Set<Integer> probable;
        boolean isCleared = false;

        public SudoNode() {
            this.val = 0;
            this.probable = new HashSet<>(FIXED_LIST);
        }

        public boolean isFixed() {
            return this.probable == null || this.probable.size() < 2;
        }

        public void setVal(int val) {
            this.val = val;
            this.probable = null;
        }

        public void remove(int val) {
            if (!this.isFixed()) {
                this.probable.remove(val);
                if (this.probable.size() == 1) {
                    this.val = this.probable.iterator().next();
                    this.probable = null;
                }
            }
        }
    }

    private static class SudoResolverHelper {

        SudoNode[][] nodes = new SudoNode[10][10];
        Set<Integer>[] v = new HashSet[10];
        Set<Integer>[] h = new HashSet[10];
        Set<Integer>[][] a = new HashSet[3][3];

        public SudoResolverHelper(int[] vals) {
            for (int i = 1; i < 10; ++i) {
                for (int j = 1; j < 10; ++j) {
                    nodes[i][j] = new SudoNode();
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
                nodes[x][y].setVal(v);
                this.addTo(x, y, v);
            }
        }

        void addTo(int x, int y, int val) {
            int startX = (int) Math.floor((x - 0.5) / 3);
            int startY = (int) Math.floor((y - 0.5) / 3);
            a[startX][startY].add(val);
            v[y].add(val);
            h[x].add(val);
            nodes[x][y].val = val;
        }

        void removeFrom(int x, int y) {
            int oldVal = nodes[x][y].val;
            nodes[x][y].val = 0;
            v[y].remove(oldVal);
            h[x].remove(oldVal);
            int startX = (int) Math.floor((x - 0.5) / 3);
            int startY = (int) Math.floor((y - 0.5) / 3);
            a[startX][startY].remove(oldVal);
        }

        boolean isValidToAdd(int x, int y, int val) {
            if (v[y].contains(val) || h[x].contains(val)) return false;
            int _x = (int) Math.floor((x - 0.5) / 3);
            int _y = (int) Math.floor((y - 0.5) / 3);
            return !a[_x][_y].contains(val);
        }
    }

    private SudoResolverHelper helper;
    private boolean resolved = false;

    public SudoResolverRecursive(int[] vals) {
        helper = new SudoResolverHelper(vals);
    }

    public void resolve() {
        this.doStep1();
        this.doStep2(1, 1);
    }

    private void doStep1() {
        SudoNode[][] nodes = helper.nodes;
        boolean modified = true;
        while (modified) {
            modified = false;
            for (int i = 1; i < 10; ++i) {
                for (int j = 1; j < 10; ++j) {
                    SudoNode node = nodes[i][j];
                    if (node.isFixed() && !node.isCleared) {
                        node.isCleared = true;
                        int val = node.val;
                        modified = true;
                        for (int k = 1; k < 10; ++k) nodes[i][k].remove(val);
                        for (int k = 1; k < 10; ++k) nodes[k][j].remove(val);
                        int startX = (int) Math.floor((i - 0.5) / 3) * 3;
                        int startY = (int) Math.floor((j - 0.5) / 3) * 3;
                        for (int _i = 1; _i <= 3; ++_i) {
                            for (int _j = 1; _j <= 3; ++_j) {
                                nodes[startX + _i][startY + _j].remove(val);
                            }
                        }
                    }
                }
            }
        }
    }

    private void doStep2(int x, int y) {
        if (!resolved) {
            int nextX = x, nextY = y;
            if (nextY > 9) {
                ++nextX;
                nextY = 1;
            }
            if (nextX > 9) {
                if (this.isValid()) {
                    resolved = true;
                }
                return;
            }
            SudoNode[][] nodes = helper.nodes;
            if (nodes[nextX][nextY].isFixed()) {
                int j = nextY;
                for (int i = nextX; i < 10; ++i) {
                    for (; j < 10; ++j) {
                        if (!nodes[i][j].isFixed()) {
                            nextX = i;
                            nextY = j;
                            break;
                        }
                    }
                    if (!nodes[nextX][nextY].isFixed()) break;
                    j = 1;
                }
            }
            if (!nodes[nextX][nextY].isFixed()) {
                for (Integer probableVal : helper.nodes[nextX][nextY].probable) {
                    if (helper.isValidToAdd(nextX, nextY, probableVal)) {
                        helper.addTo(nextX, nextY, probableVal);
                        doStep2(nextX, nextY + 1);
                        if (!resolved) helper.removeFrom(nextX, nextY);
                    }
                }
            } else if (this.isValid()) {
                resolved = true;
            }
        }
    }

    private boolean isValid() {
        SudoNode[][] nodes = helper.nodes;
        Set<Integer> set = new HashSet<>();
        for (int i = 1; i < 10; ++i) {
            set.clear();
            for (int j = 1; j < 10; ++j) set.add(nodes[i][j].val);
            if (set.size() != 9 || set.contains(0)) return false;
        }
        for (int i = 1; i < 10; ++i) {
            set.clear();
            for (int j = 1; j < 10; ++j) set.add(nodes[j][i].val);
            if (set.size() != 9 || set.contains(0)) return false;
        }
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                set.clear();
                for (int m = 1; m <= 3; ++m) {
                    for (int n = 1; n <= 3; ++n) {
                        set.add(nodes[m + i * 3][n + j * 3].val);
                    }
                }
                if (set.size() != 9 || set.contains(0)) return false;
            }
        }
        return true;
    }

    public void print() {
        SudoNode[][] nodes = helper.nodes;
        for (int i = 1; i < 10; ++i) {
            for (int j = 1; j < 10; ++j) {
                System.out.print(nodes[i][j].val + " ");
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

        SudoResolverRecursive resolver = new SudoResolverRecursive(vals);
        resolver.resolve();
        resolver.print();
    }
}
