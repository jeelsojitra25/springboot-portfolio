package com.jeel.portfolio;

import com.jeel.portfolio.config.AnalyticsProperties;
import com.jeel.portfolio.config.SiteProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({SiteProperties.class, AnalyticsProperties.class})
public class PortfolioApplication {

	public static void main(String[] args) {
		SpringApplication.run(PortfolioApplication.class, args);
	}

}
