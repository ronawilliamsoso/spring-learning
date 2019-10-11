package com.wei.eu.pricing.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Component
@Getter
@Setter
@Validated
@ConfigurationProperties( prefix = "com.zoro.eu.pricing" )
public class PricingProperties {

    @NotBlank
    public String aimondoSftpServerHost;

    public Integer aimondoSftpServerPort;

    @NotBlank
    public String aimondoSftpServerUsername;

    @NotBlank
    public String aimondoSftpServerPassword;

    @NotBlank
    public String aimondoSftpServerDirectory;

    private String priceFeedFileName;

    private String priceFeedTargetDirectory;

    private String[] extractorNames;

    private String fileHeaders;

    @NotBlank
    private String inboundQueueName;

    @NotBlank
    private String outboundNotifySnsTopic;

    private Integer maxNumberOfMessages;

    private boolean exportEnabled;

    @NotBlank
    private String importCrawlRemoteFile;

    @NotBlank
    private String importCrawLocalFile;

    private String[] importCrawlCsvHeader;

    private String exportCrawlCsvHeader;

    private String exportCrawlCsvFile;

    private String[] exportCrawlCsvExtractorNames;

}
