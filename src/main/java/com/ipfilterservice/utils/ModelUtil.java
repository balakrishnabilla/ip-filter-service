package com.ipfilterservice.utils;

import com.ipfilterservice.entity.FilterRuleEntity;
import com.ipfilterservice.model.FilterRule;

public class ModelUtil {

    public static FilterRule buildFilterRuleModel(FilterRuleEntity ruleEntity) {
        FilterRule filterRule = new FilterRule();
        filterRule.setId(ruleEntity.getId());
        filterRule.setDestinationCidr(ruleEntity.getDestinationCidr());
        filterRule.setSourceCidr(ruleEntity.getSourceCidr());
        filterRule.setAllowedDenyInd(ruleEntity.getAllowDenyInd());
        return filterRule;
    }

    public static FilterRuleEntity buildFilterRuleEntity(FilterRule ruleModel) {
        FilterRuleEntity filterRule = new FilterRuleEntity();
        filterRule.setId(ruleModel.getId());
        filterRule.setDestinationCidr(ruleModel.getDestinationCidr());
        filterRule.setSourceCidr(ruleModel.getSourceCidr());
        filterRule.setAllowDenyInd(ruleModel.getAllowedDenyInd());
        return filterRule;
    }
}
