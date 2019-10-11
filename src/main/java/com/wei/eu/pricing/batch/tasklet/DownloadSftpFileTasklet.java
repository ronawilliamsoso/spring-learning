package com.wei.eu.pricing.batch.tasklet;

import com.jcraft.jsch.ChannelSftp;
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
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class DownloadSftpFileTasklet
                implements Tasklet {
    private final Logger LOGGER = LoggerFactory.getLogger( DownloadSftpFileTasklet.class );

    @Autowired
    JSchUtils jSchUtils;

    @Autowired
    private PricingProperties pricingProperties;

    @Override
    public RepeatStatus execute( @NonNull final StepContribution contribution, @NonNull final ChunkContext chunkContext ) {

        try {

            final ChannelSftp sftpChannel = jSchUtils.createChannelSftp( pricingProperties.getAimondoSftpServerUsername(),
                            pricingProperties.getAimondoSftpServerHost(),
                            pricingProperties.getAimondoSftpServerPort(),
                            pricingProperties.getAimondoSftpServerPassword()
            );
            sftpChannel.lstat( pricingProperties.getImportCrawlRemoteFile() );// if the remote file doesnt exist, a JSchException generated
            jSchUtils.downloadFileAndDeleteRemoteFromSftp( sftpChannel, pricingProperties.getImportCrawlRemoteFile(),
                            pricingProperties.getImportCrawLocalFile()
            );
        }
        catch ( final JSchException e ) {
            LOGGER.info( "downloading file from  sftp  failed" );
        }
        catch ( final SftpException e ) {
            if ( e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE ) {
                LOGGER.info( "[" + pricingProperties.getImportCrawlRemoteFile() + "] this file does not exist on sftp" );
            }
            else {
                LOGGER.info( "downloading file from  sftp  failed" );
            }
        }
        finally {
            jSchUtils.closeAll();

        }
        return RepeatStatus.FINISHED;
    }

}
