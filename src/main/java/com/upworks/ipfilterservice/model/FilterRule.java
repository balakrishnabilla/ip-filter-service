package com.upworks.ipfilterservice.model;

import com.upworks.ipfilterservice.validator.CIDR;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Getter
@Setter
public class FilterRule {
  private Long id;
  @CIDR private String sourceCidr;
  @CIDR private String destinationCidr;
  @NotNull private int allowedDenyInd;
  private String service;
}
