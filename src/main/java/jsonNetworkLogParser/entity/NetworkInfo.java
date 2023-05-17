package jsonNetworkLogParser.entity;

import java.util.Set;

public record NetworkInfo(int id, Security security, Set<Capability> capabilities, Status status, String debug,
                          NetworkAudit networkAudit, Network network, int level) {
}
