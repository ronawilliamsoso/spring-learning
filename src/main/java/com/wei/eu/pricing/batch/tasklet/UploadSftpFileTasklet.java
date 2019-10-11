package com.wei.eu.pricing.batch.tasklet;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.wei.eu.pricing.configuration.JSchUtils;
import com.wei.eu.pricing.configuration.PricingProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileNotFoundException;

public class UploadSftpFileTasklet
                implements Tasklet {
    private final Logger LOGGER = LoggerFactory.getLogger( UploadSftpFileTasklet.class );

    @Autowired
    JSchUtils jSchUtils;

    @Autowired
    private PricingProperties pricingProperties;

    @Override public RepeatStatus execute( final StepContribution contribution, final ChunkContext chunkContext ) {

        try {
            jSchUtils.uploadFileToSftp( pricingProperties.getExportCrawlCsvFile(),
                            pricingProperties.getAimondoSftpServerDirectory(),
                            pricingProperties.getAimondoSftpServerUsername(),
                            pricingProperties.getAimondoSftpServerHost(),
                            pricingProperties.getAimondoSftpServerPort(),
                            pricingProperties.getAimondoSftpServerPassword()
            );

        }
        catch ( final JSchException | SftpException | FileNotFoundException e ) {
            e.printStackTrace();
            LOGGER.error( e.getMessage() );
            return RepeatStatus.FINISHED;
        }
        finally {
            jSchUtils.closeAll();

        }
        return RepeatStatus.FINISHED;
    }

}
