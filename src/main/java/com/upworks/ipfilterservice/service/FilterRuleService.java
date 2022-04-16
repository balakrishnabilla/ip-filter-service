package com.upworks.ipfilterservice.service;

import com.upworks.ipfilterservice.cache.FilterRuleCache;
import com.upworks.ipfilterservice.model.FilterRule;
import com.upworks.ipfilterservice.model.RuleInput;
import com.upworks.ipfilterservice.repository.FilterRuleRepository;
import com.upworks.ipfilterservice.utils.ModelUtil;
import org.apache.commons.net.util.SubnetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FilterRuleService {
  public static final int ALLOWED_IND_ALLOWED = 1;
  public static final String ALLOWED = "Allowed";
  public static final String DENIED = "Denied";
  private FilterRuleRepository filterRuleRepository;
  private FilterRuleCache filterRuleCache;

  @Autowired
  public FilterRuleService(
      FilterRuleRepository filterRuleRepository, FilterRuleCache filterRuleCache) {
    this.filterRuleRepository = filterRuleRepository;
    this.filterRuleCache = filterRuleCache;
  }

  public List<FilterRule> retrieveFilterRules() {
    List<FilterRule> filterRules = new ArrayList<>();
    filterRuleRepository
        .findAll()
        .forEach(
            entity -> {
              filterRules.add(ModelUtil.buildFilterRuleModel(entity));
            });

    return filterRules;
  }

  public Long addFilterRule(FilterRule filterRule) {
    if (filterRule == null) {
      return 0L;
    }
    Long id = filterRuleRepository.save(ModelUtil.buildFilterRuleEntity(filterRule)).getId();
    filterRuleCache.load();
    return id;
  }

  public void removeFilterRule(long id) {
    filterRuleRepository.deleteById(id);
    filterRuleCache.load();
  }

  public String getFilterRuleAllowed(RuleInput ruleInput) {
    if (ruleInput == null) {
      return null;
    }

    String destinationIp = ruleInput.getDestinationIp();
    String sourceIp = ruleInput.getSourceIp();

    Optional<String> destKeyOptional =
        filterRuleCache.getMap().keySet().stream()
            .filter(cidr -> isInRange(cidr, destinationIp))
            .findAny();

    if (!destKeyOptional.isPresent()) {
      return "Denied";
    }

    List<FilterRule> filterRules = filterRuleCache.getMap().get(destKeyOptional.get());
    Optional<FilterRule> filterRuleOpt =
        filterRules.stream().filter(rule -> isInRange(rule.getSourceCidr(), sourceIp)).findAny();

    return filterRuleOpt.isPresent()
            && filterRuleOpt.get().getAllowedDenyInd() == ALLOWED_IND_ALLOWED
        ? ALLOWED
        : DENIED;
  }

  private boolean isInRange(String cidr, String destinationIp) {
    return new SubnetUtils(cidr).getInfo().isInRange(destinationIp);
  }
}
