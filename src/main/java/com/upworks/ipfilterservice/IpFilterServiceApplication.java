package com.upworks.ipfilterservice;

import com.upworks.ipfilterservice.cache.FilterRuleCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IpFilterServiceApplication implements CommandLineRunner {
  @Autowired FilterRuleCache filterRuleCache;

  public static void main(String[] args) {
    SpringApplication.run(IpFilterServiceApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    filterRuleCache.load();
  }
}
