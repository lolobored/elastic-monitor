package org.lolobored.elastic.monitor.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class NodeStat {
  private String nodeId;
  private LocalDateTime timestamp;
  private Integer heapConsumed;
  private Integer cpu;
  private BigDecimal searchLatency;
  private BigDecimal indexingLatency;
  private Long searchTotal;
  private BigDecimal searchRate;
  private Long indexingTotal;
  private BigDecimal indexingRate;
}
