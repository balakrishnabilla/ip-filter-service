package com.upworks.ipfilterservice.model;

import com.upworks.ipfilterservice.validator.IPAddress;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class RuleInput {
  @IPAddress private String sourceIp;
  @IPAddress private String destinationIp;
}
