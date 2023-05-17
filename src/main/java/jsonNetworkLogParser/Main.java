package jsonNetworkLogParser;

import jsonNetworkLogParser.entity.*;
import jsonNetworkLogParser.repository.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        final File folder = new File("archive");

        if (!folder.exists()) {
            System.out.println("Folder archive not exists");
            return;
        }

        File[] files = folder.listFiles();
        if (files == null) {
            System.out.println("Folder archive is empty");
            return;
        }

        for (File file : files) {
            System.out.println(file.getName());
            try {
                parseFile(file);
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                JDBCConnection.closeConnection();
                throw e;
            }
        }

        JDBCConnection.closeConnection();
    }

    private static void parseFile(File file) throws ParseException {
        if (!file.isFile()) {
            System.out.println(file.getName() + " is not a file");
            return;
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }

        if (lines.size() != 1) {
            System.out.println("Illegal file content");
            return;
        }

        String line = lines.get(0);
        JSONObject jsonObject = new JSONObject(line);

        System.out.println("Reading data for: " + jsonObject.get("startDate"));

        JSONArray jsonArray = jsonObject.getJSONArray("networks");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        LocalDateTime startDate = LocalDateTime.parse(jsonObject.getString("startDate"), formatter);
        UUID uuid = UUID.fromString(jsonObject.getString("uuid"));
        String instance = jsonObject.getString("instance");

        NetworkAudit networkAudit = new NetworkAudit(NetworkAuditRepository.getMaxId() + 1, startDate, uuid, instance);
        NetworkAuditRepository.save(networkAudit);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);

            Network network = findOrCreateNetwork(object.getString("SSID"), object.getString("BSSID"));

            String debug = object.has("debug") ? object.getString("debug") : "";
            int level = object.getInt("level");
            Security security = findOrCreateSecurity(object.getString("security"));
            Status status = findOrCreateStatus(object.getString("status"));
            Set<Capability> capabilities = getCapabilities(object.getString("capabilities"));

            NetworkInfo networkInfo = new NetworkInfo(NetworkInfoRepository.getMaxId() + 1, security, capabilities, status, debug, networkAudit, network, level);
            NetworkInfoRepository.save(networkInfo);
        }
    }

    private static Network findOrCreateNetwork(String ssid, String bssid) {
        return NetworkRepository.findBySsidAndBssid(ssid, bssid).orElseGet(() -> {
            Network network = new Network(NetworkRepository.getMaxId() + 1, ssid, bssid);
            NetworkRepository.save(network);
            return network;
        });
    }

    private static Security findOrCreateSecurity(String name) {
        return SecurityRepository.findByName(name).orElseGet(() -> {
            Security security = new Security(SecurityRepository.getMaxId() + 1, name);
            SecurityRepository.save(security);
            return security;
        });
    }

    private static Status findOrCreateStatus(String message) {
        return StatusRepository.findByMessage(message).orElseGet(() -> {
                    Status status = new Status(StatusRepository.getMaxId() + 1, message);
                    StatusRepository.save(status);
                    return status;
                });
    }

    private static Set<Capability> getCapabilities(String capabilities) {
        Set<Capability> capabilitiesSet = new HashSet<>();

        String[] capabilitiesArray = Arrays.stream(capabilities.split("[\\[\\]]"))
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);

        for (String capability : capabilitiesArray) {
            capabilitiesSet.add(findOrCreateCapability(capability));
        }
        return capabilitiesSet;
    }

    private static Capability findOrCreateCapability(String capabilityName) {
        return CapabilityRepository.findByName(capabilityName).orElseGet(() -> {
            Capability capability = new Capability(CapabilityRepository.getMaxId() + 1, capabilityName);
            CapabilityRepository.save(capability);
            return capability;
        });
    }

}