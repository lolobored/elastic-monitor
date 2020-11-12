package org.lolobored.elastic.monitor.service;

import org.lolobored.elastic.monitor.model.NodeStat;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {
  List<NodeStat> getNodesStats(String stats, List<String> nodes, LocalDateTime now);
}
