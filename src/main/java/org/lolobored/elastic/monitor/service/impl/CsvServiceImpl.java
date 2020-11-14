package org.lolobored.elastic.monitor.service.impl;

import org.apache.commons.io.FileUtils;
import org.lolobored.elastic.monitor.model.NodeStat;
import org.lolobored.elastic.monitor.service.CsvService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.List;

@Service
public class CsvServiceImpl implements CsvService {
  @Override
  public void appendToCSV(String csvPath, List<NodeStat> stats) throws IOException {
    if (!Paths.get(csvPath).toFile().exists()){
      createHeader(csvPath, stats);
    }
    addMetrics(csvPath, stats);
  }

  private void createHeader(String csvPath, List<NodeStat> stats) throws IOException {
    StringBuilder header = new StringBuilder();
    header.append("Time;");
    for (NodeStat stat : stats) {
      header.append("Memory ["+stat.getNodeId()+"];");
    }
    for (NodeStat stat : stats) {
      header.append("CPU ["+stat.getNodeId()+"];");
    }
    for (NodeStat stat : stats) {
      header.append("Search Latency ["+stat.getNodeId()+"];");
    }
    for (NodeStat stat : stats) {
      header.append("Search Rate ["+stat.getNodeId()+"];");
    }
    for (NodeStat stat : stats) {
      header.append("Indexing Rate ["+stat.getNodeId()+"];");
    }
    header.append("\n");
    FileUtils.write(Paths.get(csvPath).toFile(), header.toString(), Charset.defaultCharset(), true);
  }

  private void addMetrics(String csvPath, List<NodeStat> stats) throws IOException {
    StringBuilder metrics = new StringBuilder();
    metrics.append(stats.get(0).getTimestamp()).append(";");
    for (NodeStat stat : stats) {
      metrics.append(stat.getHeapConsumed()).append(";");
    }
    for (NodeStat stat : stats) {
      metrics.append(stat.getCpu()).append(";");
    }
    for (NodeStat stat : stats) {
      metrics.append(stat.getSearchLatency()).append(";");
    }
    for (NodeStat stat : stats) {
      metrics.append(stat.getSearchRate()).append(";");
    }
    for (NodeStat stat : stats) {
      metrics.append(stat.getIndexingRate()).append(";");
    }
    metrics.append("\n");
    FileUtils.write(Paths.get(csvPath).toFile(), metrics.toString(), Charset.defaultCharset(), true);
  }
}
