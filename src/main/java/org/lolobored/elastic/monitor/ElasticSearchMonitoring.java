package org.lolobored.elastic.monitor;

import org.apache.commons.io.FileUtils;
import org.lolobored.elastic.monitor.model.NodeStat;
import org.lolobored.elastic.monitor.service.CsvService;
import org.lolobored.elastic.monitor.service.ElasticService;
import org.lolobored.elastic.monitor.service.StatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.io.Console;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
public class ElasticSearchMonitoring implements ApplicationRunner {
  private Logger logger = LoggerFactory.getLogger(ElasticSearchMonitoring.class);

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

    if (!args.containsOption("elastic")) {
      logger.error("Option --elastic is mandatory and should contain the URL to Elastic Search master node");
      System.exit(-1);
    }
    if (!args.containsOption("csv")) {
      logger.error("Option --csv is mandatory and should contain the path to the CSV file that will get produced");
      System.exit(-1);
    }
    if (!args.containsOption("user")) {
      logger.error("Option --user is mandatory and should contain the username that accesses Elastic");
      System.exit(-1);
    }
    if (!args.containsOption("csv")) {
      logger.error("Option --csv is mandatory and should contain the path to the CSV file that will get produced");
      System.exit(-1);
    }
    if (!args.containsOption("interval")) {
      logger.error("Option --interval is mandatory and should contain the interval in seconds to fetch the stats");
      System.exit(-1);
    }
    if (!args.containsOption("total")) {
      logger.error("Option --total is mandatory and should contain the total time to record the stats");
      System.exit(-1);
    }

    Console console = System.console();

    String elasticUrl= args.getOptionValues("elastic").get(0);
    String userName= args.getOptionValues("user").get(0);
    char[] passwordArray = console.readPassword("Enter ES password for "+userName+": ");
    String password= new String(passwordArray);
    String csvPath= args.getOptionValues("csv").get(0);
    Long sleepTime= Long.parseLong(args.getOptionValues("interval").get(0));
    sleepTime= sleepTime*1000;
    String totalTime= args.getOptionValues("total").get(0);
    int multiplicator= 1000;

    if (totalTime.endsWith("m")){
      multiplicator = 60*multiplicator;
    }
    else if (totalTime.endsWith("h")){
      multiplicator = 60*60*multiplicator;
    }
    else if (!totalTime.endsWith("s")){
      logger.error("Total time is supposed to be under the form 15s or 15m or 15h (where 15 can be any integer)");
      System.exit(-1);
    }
    Long timeout = multiplicator * Long.parseLong(totalTime.substring(0, totalTime.length()-1));
    FileUtils.deleteQuietly(Paths.get(csvPath).toFile());

    while (timeout > 0) {
      LocalDateTime now = LocalDateTime.now();
      List<String> nodeList = elasticService.getESNodes(elasticUrl, userName, password);
      String stats = elasticService.getNodesStats(elasticUrl, userName, password);
      List<NodeStat> nodesStats = statService.getNodesStats(stats, nodeList, now);
      csvService.appendToCSV(csvPath, nodesStats);
      logger.info("Recording stats at "+now);
      Thread.sleep(sleepTime);
      timeout= timeout-sleepTime;
    }
  }
}
