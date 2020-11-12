package org.lolobored.elastic.monitor.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NodeStat {
  private String nodeId;
  private LocalDateTime timestamp;
  private Integer heapConsumed;
  private Integer cpu;
}
