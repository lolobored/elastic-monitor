package org.lolobored.elastic.monitor.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.lolobored.elastic.monitor.model.NodeStat;
import org.lolobored.elastic.monitor.service.StatService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class StatServiceImpl implements StatService {
  @Override
  public List<NodeStat> getNodesStats(String stats, List<String> nodes, LocalDateTime now) {
    List<NodeStat> result= new ArrayList<>();
    for (String node : nodes) {
      NodeStat nodeStat= new NodeStat();
      nodeStat.setNodeId(node);
      nodeStat.setTimestamp(now);
      String currentStat = StringUtils.substringAfter(stats, "\"" + node + "\"");
      //mem
      String memStat = StringUtils.substringAfter(currentStat, "\"mem\":");
      String mem = StringUtils.substringBetween(memStat, "\"jvm\":", "}");
      mem = StringUtils.substringBetween(mem, "\"heap_used_percent\":", ",");
      nodeStat.setHeapConsumed(Integer.parseInt(mem.trim()));
      //cpu
      String cpuStat = StringUtils.substringAfter(currentStat, "\"cpu\":");
      cpuStat = StringUtils.substringAfter(currentStat, "\"cpu\":");
      String cpu = StringUtils.substringBetween(cpuStat, "\"percent\":", ",");
      nodeStat.setCpu(Integer.parseInt(cpu.trim()));
      result.add(nodeStat);
    }
    return result;
  }
}
