package org.lolobored.elastic.monitor.service;

import org.lolobored.elastic.monitor.model.NodeStat;

import java.io.IOException;
import java.util.List;

public interface CsvService {
  void appendToCSV(String csvPath, List<NodeStat> stats) throws IOException;
}
