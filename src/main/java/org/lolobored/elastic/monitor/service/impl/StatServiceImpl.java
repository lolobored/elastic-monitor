package org.lolobored.elastic.monitor.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.lolobored.elastic.monitor.model.NodeStat;
import org.lolobored.elastic.monitor.service.StatService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class StatServiceImpl implements StatService {
  @Override
  public List<NodeStat> parseNodesStats(String stats, List<String> nodes, LocalDateTime now, List<NodeStat> previousStats, long timeout) {
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
      //search stats
      String searchStat = StringUtils.substringAfter(currentStat, "\"search\":");
      long searchTime = Long.parseLong(StringUtils.substringBetween(searchStat, "\"query_time_in_millis\":", ","));
      long searchNb = Long.parseLong(StringUtils.substringBetween(searchStat, "\"query_total\":", ","));
      nodeStat.setSearchLatency(BigDecimal.valueOf(searchTime).divide(BigDecimal.valueOf(searchNb), 2, RoundingMode.CEILING));
      nodeStat.setSearchTotal(searchNb);
      for (NodeStat previousStat : previousStats) {
        if (previousStat.getNodeId().equals(node)){
          long searchDiff= searchNb - previousStat.getSearchTotal();
          long timeDiff = timeout / 1000;
          nodeStat.setSearchRate(BigDecimal.valueOf(searchDiff).divide(BigDecimal.valueOf(timeDiff),2, RoundingMode.CEILING));
        }
      }

      //indexing stats
      String indexingStat = StringUtils.substringAfter(currentStat, "\"indexing\":");
      long indexTime = Long.parseLong(StringUtils.substringBetween(indexingStat, "\"index_time_in_millis\":", ","));
      long indexNb = Long.parseLong(StringUtils.substringBetween(indexingStat, "\"index_total\":", ","));
      nodeStat.setIndexingLatency(BigDecimal.valueOf(indexTime).divide(BigDecimal.valueOf(indexNb), 2, RoundingMode.CEILING));
      nodeStat.setIndexingTotal(indexNb);
      for (NodeStat previousStat : previousStats) {
        if (previousStat.getNodeId().equals(node)){
          long indexDiff= indexNb - previousStat.getIndexingTotal();
          long timeDiff = timeout / 1000;
          nodeStat.setIndexingRate(BigDecimal.valueOf(indexDiff).divide(BigDecimal.valueOf(timeDiff),2, RoundingMode.CEILING));
        }
      }

      result.add(nodeStat);
    }
    return result;
  }
}
