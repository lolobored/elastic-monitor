package org.lolobored.elastic.monitor.service;

import java.util.List;

public interface ElasticService {
  List<String> getESNodes(String elasticUrl, String username, String password);
  String getNodesStats(String elasticUrl, String username, String password);
}
