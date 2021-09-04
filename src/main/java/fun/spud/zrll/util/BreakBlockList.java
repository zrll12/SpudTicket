package fun.spud.zrll.util;

import fun.spud.zrll.varclass.Equal;

import java.util.Arrays;

public class BreakBlockList {

    public Object[] canBreakSigns;
    public int tailOfCBS = 0;

    public BreakBlockList() {
        canBreakSigns = new Object[10];
    }

    /**
     * Let the player break the protected sign.
     *
     * @param name Player's uuid
     */
    public void insert(Object name, Equal equal) {
        boolean flag = true;
        this.expandCapacity(tailOfCBS + 1);
        for (int i = 0; i < tailOfCBS; i++) {
            if (equal.cmp(canBreakSigns[i], name)) {
                flag = false;
                break;
            }
        }
        if (flag) {
            canBreakSigns[tailOfCBS] = name;
            tailOfCBS++;
        }
    }

    /**
     * Do not let the player break the protected sign.
     *
     * @param name Player's uuid
     * @return True if success, false if player is not exist
     */
    public boolean remove(Object name, Equal equal) {
        boolean flag = false;
        for (int i = 0; i < tailOfCBS; i++) {
            if (equal.cmp(canBreakSigns[i], name)) {
                for (int j = i; j < tailOfCBS - 1; j++) {
                    canBreakSigns[i] = canBreakSigns[i + 1];
                }
                flag = true;
                tailOfCBS--;
            }
        }
        return flag;
    }

    /**
     * Check whether the player can break the sign.
     *
     * @param name Player's uuid
     * @return True if the player can break the sign, false if the player can not break the sign
     */
    public Object check(Object name, Equal equal) {
        Object find = new Object();
        boolean flag = true;
        for (int i = 0; i < tailOfCBS; i++) {
            if (equal.cmp(canBreakSigns[i], name)) {
                find = canBreakSigns[i];
                flag = false;
                break;
            }
        }
        if (flag) {
            return null;
        }
        return find;
    }

    /**
     * Expand the size if it is not enough.
     *
     * @param size the size
     */
    public void expandCapacity(int size) {
        int len = canBreakSigns.length;
        if (size > len) {
            size = size * 3 / 2 + 1;
            canBreakSigns = Arrays.copyOf(canBreakSigns, size);
        }
    }
}
