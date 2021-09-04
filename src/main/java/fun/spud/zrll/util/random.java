package fun.spud.zrll.util;

import java.util.UUID;

public class random {

    /**
     * Get a random ticket id.
     *
     * @param TIDprefix The prefix of the tid
     * @return The tid
     */
    public static String getRandomTID(String TIDprefix) {
        UUID motheruuid = UUID.randomUUID();
        StringBuilder tid = new StringBuilder(TIDprefix);
        String[] stringuuid = motheruuid.toString().split("-");
        for (int i = 0; i < 5; i++) {
            tid.append(stringuuid[i]);
        }
        return tid.toString();
    }
}
