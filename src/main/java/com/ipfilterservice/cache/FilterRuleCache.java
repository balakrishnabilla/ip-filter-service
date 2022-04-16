package com.ipfilterservice.cache;

import com.ipfilterservice.entity.FilterRuleEntity;
import com.ipfilterservice.model.FilterRule;
import com.ipfilterservice.repository.FilterRuleRepository;
import com.ipfilterservice.utils.ModelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FilterRuleCache {
  @Autowired private FilterRuleRepository filterRuleRepository;
  private Map<String, List<FilterRule>> destinationMap = new ConcurrentHashMap<>();

  public void load() {
    List<FilterRuleEntity> list = filterRuleRepository.findAll();
    for (FilterRuleEntity rule : list) {
      String destinationIp = rule.getDestinationCidr();
      FilterRule filterRule = ModelUtil.buildFilterRuleModel(rule);
      if (!destinationMap.containsKey(destinationIp)) {
        List<FilterRule> filterRules = new ArrayList<>();
        filterRules.add(filterRule);
        destinationMap.put(rule.getDestinationCidr(), filterRules);
      } else {
        List<FilterRule> filterRules = destinationMap.get(destinationIp);
        filterRules.add(filterRule);
      }
    }
  }

  public Map<String, List<FilterRule>> getMap() {
    return new HashMap<>(destinationMap);
  }


}
