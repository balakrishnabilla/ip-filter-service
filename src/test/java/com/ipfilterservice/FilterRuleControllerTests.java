package com.ipfilterservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ipfilterservice.cache.FilterRuleCache;
import com.ipfilterservice.model.RuleInput;
import com.ipfilterservice.controller.FilterRuleController;
import com.ipfilterservice.model.FilterRule;
import com.ipfilterservice.service.FilterRuleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@RunWith(SpringRunner.class)
@WebMvcTest(FilterRuleController.class)
public class FilterRuleControllerTests {
  public static final String BASE_URL = "/filterRules";
  @Autowired private MockMvc mockMvc;

  @MockBean private FilterRuleService filterRuleService;

  @MockBean private FilterRuleCache filterRuleCache;

  private static final ObjectMapper mapper = new ObjectMapper();

  public FilterRuleControllerTests() {}

  @Test
  public void testCreateAndReturnResponseCreated() throws Exception {
    // Input
    FilterRule filterRule = getFilterRule("10.2.0.1/11", "192.169.0.1/23");
    String jsonString = getJsonString(filterRule);
    // mock service
    when(filterRuleService.addFilterRule(any(FilterRule.class))).thenReturn(1L);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(BASE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(mvcResult -> mvcResult.getResponse().getHeader(HttpHeaders.LOCATION))
        .andReturn();
  }

  @Test
  public void testCreateForBadDestCidrBlock() throws Exception {
    // Input
    FilterRule filterRule = getFilterRule("10.2.0.1/11", "192.169.0.1/231");
    String jsonString = getJsonString(filterRule);
    // mock service
    when(filterRuleService.addFilterRule(any(FilterRule.class))).thenReturn(1L);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(BASE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void testCreateForBadScrCidrBlock() throws Exception {
    // Input
    FilterRule filterRule = getFilterRule("10.2.0.1/111", "192.169.0.1/23");
    String jsonString = getJsonString(filterRule);
    // mock service
    when(filterRuleService.addFilterRule(any(FilterRule.class))).thenReturn(1L);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(BASE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void testRemoveRules() throws Exception {
    doNothing().when(filterRuleService).removeFilterRule(1);
    mockMvc
        .perform(
            MockMvcRequestBuilders.delete(BASE_URL + "/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());
  }

  @Test
  public void testRetrieveRules() throws Exception {
    List<FilterRule> list = new ArrayList<>();
    FilterRule filterRule = getFilterRule("10.10.0.1/31", "10.10.0.1/32");
    list.add(filterRule);

    when(filterRuleService.retrieveFilterRules()).thenReturn(list);
    MvcResult result = mockMvc.perform(get(BASE_URL)).andExpect(status().isOk()).andReturn();

    List<FilterRule> jsonResponse =
        mapper.readValue(
            result.getResponse().getContentAsString(), new TypeReference<List<FilterRule>>() {});
    assertEquals(1, jsonResponse.size());
    assertEquals(list.get(0).getSourceCidr(), jsonResponse.get(0).getSourceCidr());
    assertEquals(list.get(0).getDestinationCidr(), jsonResponse.get(0).getDestinationCidr());
    assertEquals(list.get(0).getAllowedDenyInd(), jsonResponse.get(0).getAllowedDenyInd());
  }

  private FilterRule getFilterRule(String sourceCidr, String destCidr) {
    FilterRule filterRule = new FilterRule();
    filterRule.setSourceCidr(sourceCidr);
    filterRule.setDestinationCidr(destCidr);
    filterRule.setAllowedDenyInd(1);
    return filterRule;
  }

  @Test
  public void testCheckFilterRuleAllowed() throws Exception {
    RuleInput ruleInput = new RuleInput();
    ruleInput.setSourceIp("10.0.0.1");
    ruleInput.setDestinationIp("192.168.0.1");

    when(filterRuleService.getFilterRuleAllowed(any())).thenReturn("Allowed");

    String requestJson = getJsonString(ruleInput);

    MvcResult result =
        mockMvc
            .perform(
                get(BASE_URL + "/allowed")
                    .content(requestJson)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    assertEquals("Allowed", result.getResponse().getContentAsString());
  }

  @Test
  public void testCheckFilterRuleForBadDestIpAddress() throws Exception {
    RuleInput ruleInput = new RuleInput();
    ruleInput.setSourceIp("10.0.0.1");
    ruleInput.setDestinationIp("192.168.0.1.1");

    String requestJson = getJsonString(ruleInput);

    mockMvc
        .perform(
            get(BASE_URL + "/allowed").content(requestJson).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andReturn();
  }

  @Test
  public void testCheckFilterRuleForBadScrIpAddress() throws Exception {
    RuleInput ruleInput = new RuleInput();
    ruleInput.setSourceIp("10.0.0.1.2");
    ruleInput.setDestinationIp("192.168.0.1");

    String requestJson = getJsonString(ruleInput);

    mockMvc
        .perform(
            get(BASE_URL + "/allowed").content(requestJson).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andReturn();
  }

  private String getJsonString(Object ruleInput) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
    String requestJson = ow.writeValueAsString(ruleInput);
    return requestJson;
  }
}
