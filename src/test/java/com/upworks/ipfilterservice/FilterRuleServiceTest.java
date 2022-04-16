package com.upworks.ipfilterservice;

import com.upworks.ipfilterservice.cache.FilterRuleCache;
import com.upworks.ipfilterservice.entity.FilterRuleEntity;
import com.upworks.ipfilterservice.model.FilterRule;
import com.upworks.ipfilterservice.model.RuleInput;
import com.upworks.ipfilterservice.repository.FilterRuleRepository;
import com.upworks.ipfilterservice.service.FilterRuleService;
import com.upworks.ipfilterservice.utils.ModelUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class FilterRuleServiceTest {

  public static final String DENIED = "Denied";
  public static final String ALLOWED = "Allowed";
  @MockBean private FilterRuleRepository filterRuleRepository;
  @MockBean private FilterRuleCache filterRuleCache;

  private FilterRuleService filterRuleService;

  @Before
  public void setUp() {
    filterRuleService = new FilterRuleService(filterRuleRepository, filterRuleCache);
  }

  @AfterEach
  void tearDown() {
    clearInvocations(filterRuleRepository);
  }

  @Test
  public void testAddFilterRule() {
    // input
    FilterRule filterRule = new FilterRule();
    filterRule.setSourceCidr("10.2.0.1/11");
    filterRule.setDestinationCidr("192.169.0.1/23");
    filterRule.setAllowedDenyInd(1);

    // output mock
    FilterRuleEntity entity = new FilterRuleEntity();
    entity.setId(3L);
    entity.setSourceCidr("10.2.0.1/11");
    entity.setDestinationCidr("192.169.0.1/23");
    entity.setAllowDenyInd(1);

    when(filterRuleRepository.save(any())).thenReturn(entity);

    Long id = filterRuleService.addFilterRule(filterRule);

    verify(filterRuleRepository, times(1)).save(any(FilterRuleEntity.class));
    verify(filterRuleCache, times(1)).load();
    assertEquals(java.util.Optional.of(3L), java.util.Optional.of(id));
  }

  @Test
  public void testAddFilterRuleWithNoInput() {
    when(filterRuleRepository.save(any())).thenReturn(null);
    Long id = filterRuleService.addFilterRule(null);
    verify(filterRuleRepository, times(0)).save(any(FilterRuleEntity.class));
    verify(filterRuleCache, times(0)).load();
    assertEquals(java.util.Optional.of(0L), java.util.Optional.of(id));
  }

  @Test
  public void testRemoveFilterRule() {
    doNothing().when(filterRuleRepository).deleteById(any());
    filterRuleService.removeFilterRule(1L);
    verify(filterRuleRepository, times(1)).deleteById(any());
    verify(filterRuleCache, times(1)).load();
  }

  @Test
  public void testGetFilterRuleForNullInput() {

    String ruleAllowed = filterRuleService.getFilterRuleAllowed(null);
    verify(filterRuleCache, times(0)).getMap();
    assertEquals(null, ruleAllowed);
  }

  @Test
  public void testGetFilterRuleAllowedWhenNoRulesAvailable() {
    RuleInput ruleInput = new RuleInput();
    ruleInput.setSourceIp("10.2.0.1");
    ruleInput.setDestinationIp("192.169.0.1");

    when(filterRuleCache.getMap()).thenReturn(new HashMap<>());
    String ruleAllowed = filterRuleService.getFilterRuleAllowed(ruleInput);
    verify(filterRuleCache, times(1)).getMap();
    assertEquals(DENIED, ruleAllowed);
  }

  @Test
  public void testGetFilterRuleAllowedWhenNotMatching() {
    RuleInput ruleInput = new RuleInput();
    ruleInput.setSourceIp("10.0.0.1");
    ruleInput.setDestinationIp("192.168.0.1");

    when(filterRuleCache.getMap()).thenReturn(buildMap());
    String ruleAllowed = filterRuleService.getFilterRuleAllowed(ruleInput);
    verify(filterRuleCache, times(2)).getMap();
    assertEquals(ALLOWED, ruleAllowed);
  }

  @Test
  public void testGetFilterRuleAllowedWhenNoDestinationAvailable() {
    RuleInput ruleInput = new RuleInput();
    ruleInput.setSourceIp("10.2.0.1");
    ruleInput.setDestinationIp("192.140.0.1");

    when(filterRuleCache.getMap()).thenReturn(buildMap());
    String ruleAllowed = filterRuleService.getFilterRuleAllowed(ruleInput);
    verify(filterRuleCache, times(1)).getMap();
    assertEquals(DENIED, ruleAllowed);
  }

  @Test
  public void testGetFilterRuleDeniedWhenDestNotFound() {
    RuleInput ruleInput = new RuleInput();
    ruleInput.setSourceIp("10.101.150.1");
    ruleInput.setDestinationIp("10.136.1.1");

    when(filterRuleCache.getMap()).thenReturn(buildMap());
    String ruleAllowed = filterRuleService.getFilterRuleAllowed(ruleInput);
    verify(filterRuleCache, times(1)).getMap();
    assertEquals(DENIED, ruleAllowed);
  }

  @Test
  public void testGetFilterRuleDeniedWhenSourceNotFound() {
    RuleInput ruleInput = new RuleInput();
    ruleInput.setSourceIp("10.2.20.1");
    ruleInput.setDestinationIp("192.168.0.1");

    when(filterRuleCache.getMap()).thenReturn(buildMap());
    String ruleAllowed = filterRuleService.getFilterRuleAllowed(ruleInput);
    verify(filterRuleCache, times(2)).getMap();
    assertEquals(DENIED, ruleAllowed);
  }

  @Test
  public void testRetrieveFilterRulesWithNoRules() {
    List<FilterRuleEntity> entities = new ArrayList<>();
    when(filterRuleRepository.findAll()).thenReturn(entities);
    List<FilterRule> filterRules = filterRuleService.retrieveFilterRules();
    assertEquals(0, filterRules.size());
  }

  @Test
  public void testRetrieveFilterRulesWhenRulesAvailable() {
    List<FilterRuleEntity> entities = new ArrayList<>();
    FilterRuleEntity entity = new FilterRuleEntity();
    entity.setId(1L);
    entity.setSourceCidr("10.0.0.1/25");
    entity.setDestinationCidr("192.168.0.1/25");
    entity.setAllowDenyInd(1);
    entities.add(entity);
    when(filterRuleRepository.findAll()).thenReturn(entities);
    List<FilterRule> filterRules = filterRuleService.retrieveFilterRules();
    assertEquals(1, filterRules.size());
  }

  // test data setup
  private Map<String, List<FilterRule>> buildMap() {
    Map<String, List<FilterRule>> destinationMap = new ConcurrentHashMap<>();
    FilterRuleEntity filterRuleEntity = new FilterRuleEntity();
    filterRuleEntity.setId(1L);
    filterRuleEntity.setAllowDenyInd(1);
    filterRuleEntity.setSourceCidr("10.0.0.1/25");
    filterRuleEntity.setDestinationCidr("192.168.0.1/25");

    List<FilterRuleEntity> list = new ArrayList<>();
    list.add(filterRuleEntity);
    FilterRuleEntity filterRuleEntity2 = new FilterRuleEntity();
    filterRuleEntity2.setId(2L);
    filterRuleEntity2.setAllowDenyInd(1);
    filterRuleEntity2.setSourceCidr("10.1.0.1/30");
    filterRuleEntity2.setDestinationCidr("192.168.1.1/30");

    list.add(filterRuleEntity2);
    FilterRuleEntity filterRuleEntity3 = new FilterRuleEntity();
    filterRuleEntity3.setId(2L);
    filterRuleEntity3.setAllowDenyInd(0);
    filterRuleEntity3.setSourceCidr("10.101.150.1/30");
    filterRuleEntity3.setDestinationCidr("10.136.1.1/30");

    list.add(filterRuleEntity2);
    FilterRuleEntity filterRuleEntity4 = new FilterRuleEntity();
    filterRuleEntity4.setId(2L);
    filterRuleEntity4.setAllowDenyInd(0);
    filterRuleEntity4.setSourceCidr("10.101.150.1/30");
    filterRuleEntity4.setDestinationCidr("192.168.0.1/25");
    list.add(filterRuleEntity4);
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
    return destinationMap;
  }
}
