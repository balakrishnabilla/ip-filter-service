package com.upworks.ipfilterservice.controller;

import com.upworks.ipfilterservice.model.FilterRule;
import com.upworks.ipfilterservice.model.RuleInput;
import com.upworks.ipfilterservice.service.FilterRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(path = "/filterRules")
public class FilterRuleController {

    @Autowired
    private FilterRuleService filterRuleService;

    @GetMapping()
    public List<FilterRule> retrieveFilterRules() {
        return filterRuleService.retrieveFilterRules();
    }


    @PostMapping()
    public ResponseEntity<Object> addFilterRule(@Valid @RequestBody FilterRule filterRule) {

        Long ruleId = filterRuleService.addFilterRule(filterRule);
        URI location =
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(ruleId)
                        .toUri();

        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> removeFilterRule(@PathVariable long id) {
        filterRuleService.removeFilterRule(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/allowed")
    public ResponseEntity<Object> checkFilterRuleAllowed(@Valid @RequestBody RuleInput ruleInput) {
        String allowedOrDenyResponse = filterRuleService.getFilterRuleAllowed(ruleInput);
        return ResponseEntity.ok(allowedOrDenyResponse);
    }


}
