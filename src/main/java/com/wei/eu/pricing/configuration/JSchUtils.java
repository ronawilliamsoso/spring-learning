package com.wei.eu.pricing.configuration;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Component
@Scope( ConfigurableBeanFactory.SCOPE_PROTOTYPE )
public class JSchUtils {

    private ChannelSftp sftpChannel;

    private Session session;

    private Channel channel;

    public void downloadFileFromSftp( final String remoteFile,
                                      final String localFile,
                                      final String username,
                                      final String host,
                                      final Integer port,
                                      final String password )
                    throws JSchException, SftpException {

        sftpChannel = createChannelSftp( username, host, port, password );
        final File file = new File( localFile );
        FileUtils.deleteQuietly( file );
        sftpChannel.get( remoteFile, localFile, null, ChannelSftp.OVERWRITE );
        sftpChannel.rm( remoteFile );

    }

    public void downloadFileAndDeleteRemoteFromSftp( final ChannelSftp sftpChannel, final String remoteFile, final String localFile )
                    throws SftpException {

        final File file = new File( localFile );
        FileUtils.deleteQuietly( file );
        sftpChannel.get( remoteFile, localFile, null, ChannelSftp.OVERWRITE );
        sftpChannel.rm( remoteFile );

    }

    public void uploadFileToSftp( final String localFile,
                                  final String remoteDirectory,
                                  final String username,
                                  final String host,
                                  final Integer port,
                                  final String password )
                    throws JSchException, SftpException, FileNotFoundException {

        sftpChannel = createChannelSftp( username, host, port, password );
        sftpChannel.cd( remoteDirectory );
        final File tobeUpload = new File( localFile );
        sftpChannel.put( new FileInputStream( tobeUpload ), tobeUpload.getName(), ChannelSftp.OVERWRITE );
    }

    public ChannelSftp createChannelSftp( final String username, final String host, final Integer port, final String password )
                    throws JSchException {
        final JSch jsch = new JSch();

        session = jsch.getSession( username, host, port );
        session.setConfig( "StrictHostKeyChecking", "no" );
        session.setPassword( password );
        session.connect();
        channel = session.openChannel( "sftp" );
        channel.connect();
        sftpChannel = (ChannelSftp) channel;
        return sftpChannel;

    }

    public void closeAll() {

        if ( sftpChannel != null && sftpChannel.isConnected() ) {
            sftpChannel.exit();
        }
        if ( session != null && session.isConnected() ) {
            session.disconnect();
        }
        if ( channel != null && channel.isConnected() ) {
            channel.disconnect();
        }

    }
}
