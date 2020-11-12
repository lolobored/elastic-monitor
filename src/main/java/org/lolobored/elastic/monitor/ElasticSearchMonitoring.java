package org.lolobored.elastic.monitor;

import org.apache.commons.io.FileUtils;
import org.lolobored.elastic.monitor.model.NodeStat;
import org.lolobored.elastic.monitor.service.CsvService;
import org.lolobored.elastic.monitor.service.ElasticService;
import org.lolobored.elastic.monitor.service.StatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
public class ElasticSearchMonitoring implements ApplicationRunner {

  @Autowired
  ElasticService elasticService;
  @Autowired
  StatService statService;
  @Autowired
  CsvService csvService;

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  public static void main(String[] args)  {
    SpringApplication application = new SpringApplication(ElasticSearchMonitoring.class);
    application.setWebApplicationType(WebApplicationType.NONE);
    application.run(args);
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    String elasticUrl= "http://localhost:9200";
    String userName= "http://localhost:9200";
    String password= "http://localhost:9200";
    String csvPath= "./result.csv";
    Long sleepTime= Long.parseLong("1");
    sleepTime= sleepTime*1000;
    String totalTime= "1m";
    int multiplicator= 1000;

    if (totalTime.endsWith("m")){
      multiplicator = 60*multiplicator;
    }
    else if (totalTime.endsWith("h")){
      multiplicator = 60*60*multiplicator;
    }
    else if (totalTime.endsWith("s")){

    }
    else{
      throw new Exception("Total time is supposed to be under the form 15s or 15m or 15h (where 15 can be any integer)");
    }
    Long timeout = multiplicator * Long.parseLong(totalTime.substring(0, totalTime.length()-1));
    FileUtils.deleteQuietly(Paths.get(csvPath).toFile());

    while (timeout > 0) {
      LocalDateTime now = LocalDateTime.now();
      List<String> nodeList = elasticService.getESNodes(elasticUrl, userName, password);
      String stats = elasticService.getNodesStats(elasticUrl, userName, password);
      List<NodeStat> nodesStats = statService.getNodesStats(stats, nodeList, now);
      csvService.appendToCSV(csvPath, nodesStats);
      System.out.println("Recording stats at "+now);
      Thread.sleep(sleepTime);
      timeout= timeout-sleepTime;
    }
  }
}
