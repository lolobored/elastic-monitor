package org.lolobored.elastic.monitor.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.lolobored.elastic.monitor.service.ElasticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;

@Service
public class ElasticServiceImpl implements ElasticService {

  @Autowired
  RestTemplate restTemplate;

  @Override
  public List<String> getESNodes(String elasticUrl, String username, String password) {
    int currentLevel=0;
    List<String> nodeIds= new ArrayList<>();
    StringBuilder nodeName= new StringBuilder();
    String stats = callES(elasticUrl+"/_nodes", username, password);
    stats = StringUtils.substringAfter(stats, "\"nodes\":");
    stats = StringUtils.substringAfter(stats, "{");
    StringCharacterIterator st= new StringCharacterIterator(stats);
    for (char ch = st.current(); ch != st.DONE; ch = st.next()) {
      if (ch == '{'){
        currentLevel++;
        if (currentLevel == 1){
          nodeIds.add(StringUtils.substringBetween(nodeName.toString(), "\"", "\""));
          nodeName = new StringBuilder();
        }
      }
      else if (ch == '}'){
        currentLevel--;
      }
      if (currentLevel == 0){
        nodeName.append(ch);
      }
    }
    return nodeIds;
  }

  private String callES(String elasticUrl, String username, String password) {
    return restTemplate.exchange(elasticUrl, HttpMethod.GET,
            new HttpEntity(createHeaders(username, password)), String.class).getBody();
  }

  @Override
  public String getNodesStats(String elasticUrl, String username, String password) {
    return callES(elasticUrl+"/_nodes/stats", username, password);
  }

  private HttpHeaders createHeaders(String username, String password){
    return new HttpHeaders() {{
      if (!username.isEmpty()) {
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.encodeBase64(
                auth.getBytes(Charset.forName("US-ASCII")));
        String authHeader = "Basic " + new String(encodedAuth);
        set("Authorization", authHeader);
      }
    }};
  }
}
