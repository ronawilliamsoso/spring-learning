package com.wei.eu.pricing.batch.tasklet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CopyFileTasklet
                implements Tasklet {

    private static final Logger LOGGER = LoggerFactory.getLogger( CopyFileTasklet.class );

    private final String sourceFileName;

    private final String targetDirectoryName;

    private final Boolean addCurrentTimestamp;

    public CopyFileTasklet( final String sourceFileName, final String targetDirectoryName, final Boolean addCurrentTimestamp ) {
        this.sourceFileName = sourceFileName;
        this.targetDirectoryName = targetDirectoryName;
        this.addCurrentTimestamp = addCurrentTimestamp;
    }

    @Override
    public RepeatStatus execute( final StepContribution stepContribution, final ChunkContext chunkContext ) {

        CopyFileTasklet.LOGGER.info( "Start CopyFileTasklet to copy files to target directory." );

        final File exportFile = new File( sourceFileName );
        if ( exportFile.exists() && exportFile.isFile() ) {
            try {
                String targetFileName = FilenameUtils.getName( sourceFileName );
                if ( addCurrentTimestamp ) {

                    final Date date = Calendar.getInstance().getTime();
                    final DateFormat dateFormat = new SimpleDateFormat( "yyyyMMddHHmmss" );
                    final String createdTime = dateFormat.format( date );

                    targetFileName = FilenameUtils.getBaseName( sourceFileName )
                                    + "_"
                                    + createdTime
                                    + "."
                                    + FilenameUtils.getExtension( sourceFileName );
                }

                final File targetFile = new File( targetDirectoryName, targetFileName );
                FileUtils.moveFile( exportFile, targetFile );
                changeFilePermissions( targetFile );
            }
            catch ( final IOException | NullPointerException e ) {
                CopyFileTasklet.LOGGER.error( "Error while copping file {}. Message: {}", sourceFileName, e.getMessage() );
                return RepeatStatus.FINISHED.and( false );
            }
            return RepeatStatus.FINISHED.and( true );
        }
        else {
            return RepeatStatus.FINISHED.and( true );
        }
    }

    private void changeFilePermissions( final File file ) {
        file.setReadable( true, false );
        file.setWritable( true, false );
        file.setExecutable( true, false );
    }
}
