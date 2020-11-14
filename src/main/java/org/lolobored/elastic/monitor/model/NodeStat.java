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
  private Long searchTotal=(long)0;
  private BigDecimal searchRate=BigDecimal.ZERO;
  private Long indexingTotal=(long)0;
  private BigDecimal indexingRate=BigDecimal.ZERO;
}
