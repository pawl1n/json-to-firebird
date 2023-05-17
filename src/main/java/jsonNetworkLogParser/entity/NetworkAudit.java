package jsonNetworkLogParser.entity;

import java.time.LocalDateTime;
import java.util.*;

public record NetworkAudit(int id, LocalDateTime startDate, UUID uuid, String instance) {}
