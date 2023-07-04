package server;

import java.util.HashSet;

public class ApprovedFileName {
    private static ApprovedFileName approvedFileName;

    private static HashSet<String> approvedFileNameSet;

    private ApprovedFileName() {
    }

    public static ApprovedFileName getInstance() {
        if (approvedFileName == null) {
            approvedFileName = new ApprovedFileName();
            approvedFileNameSet = new HashSet<>();
            for (int i = 1; i <= 10; i++) {
                approvedFileNameSet.add("file" + String.valueOf(i));
            }
        }
        return approvedFileName;
    }

    public boolean isApproved(String name) {
        return approvedFileNameSet.contains(name);
    }
}
