package example.secureqr.util;

import org.springframework.stereotype.Component;

@Component
public class GeoLocationResolver {

    public String approximateLocation(String ipAddress) {
        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            return "UNKNOWN";
        }

        if (ipAddress.startsWith("10.") || ipAddress.startsWith("192.168") || ipAddress.startsWith("127.")) {
            return "INTERNAL_NETWORK";
        }

        int segment = ipAddress.hashCode();
        int index = Math.abs(segment % 4);
        if (index == 0) {
            return "BENGALURU";
        }
        if (index == 1) {
            return "HYDERABAD";
        }
        if (index == 2) {
            return "MUMBAI";
        }
        return "DELHI";
    }
}
