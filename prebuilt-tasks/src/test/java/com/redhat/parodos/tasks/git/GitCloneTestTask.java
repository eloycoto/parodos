package com.redhat.parodos.tasks.git;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.ssh.jsch.JschConfigSessionFactory;
import org.eclipse.jgit.transport.ssh.jsch.OpenSshConfig;
import org.junit.Test;

//import org.eclipse.jgit.api.TransportCommand;
import org.eclipse.jgit.api.TransportConfigCallback;
//import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.util.FS;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class GitCloneTestTask {

//    private String sshKeyPath = "/home/eloy/.ssh/id_rsa";
    private String sshKeyPath = "/tmp/keys/id_rsa";
    private String repoPath = "git@github.com:eloycoto/merda.git";


    @BeforeEach
    public void setup() throws Exception {
    }

	@Test
	public void test() throws Exception {
        log.error("Test-->");
        Path tempDir = Files.createTempDirectory("git-repo");
        Git git = Git.open(new File("/tmp/merda/"));
        git.fetch().setTransportConfigCallback(this.setTransport()).call();
//        Git git = Git.cloneRepository()
//                .setURI(repoPath)
//                // .setDirectory(tempDir.toFile())
//                .setDirectory(new File("/tmp/merda"))
//                .setTransportConfigCallback(this.setTransport())
//                .call();

        git.close();

	}

	public TransportConfigCallback setTransport() {
        var sshSessionFactory = new JschConfigSessionFactory() {
            @Override
            protected void configure(OpenSshConfig.Host host, Session session ) {
                session.setConfig("StrictHostKeyChecking", "no");
                session.setConfig("PreferredAuthentications", "publickey");
//                session.setConfig("IdentifyFile", sshKeyPath);
                //session.setConfig("user", "eloycoto");
            }

            @Override
            protected JSch createDefaultJSch(FS fs) throws JSchException {
                JSch defaultJSch = super.createDefaultJSch(fs);
                defaultJSch.setLogger(new com.jcraft.jsch.Logger() {
                    public boolean isEnabled(int level) {
                        // Enable debug logging for all levels
                        return true;
                    }

                    public void log(int level, String message) {
                        System.out.println("JSch Debug [" + level + "]: " + message);
                    }
                });
                log.error("-------------------------------------------------------");
                log.error("-------------------------------------------------------");
                log.error("-------------------------------------------------------");
                log.error("-------------------------------------------------------");
                log.error("-------------------------------------------------------");
                defaultJSch.addIdentity(sshKeyPath, "");
                log.error("xxxxxxxxxxxxxxxxxxxxxxxxxxx");
                log.error("xxxxxxxxxxxxxxxxxxxxxxxxxxx");
                return defaultJSch;
            }

        };
        return new TransportConfigCallback() {
            @Override
            public void configure(Transport transport) {
                SshTransport sshTransport = (SshTransport) transport;
                sshTransport.setSshSessionFactory(sshSessionFactory);
            }
        };
    }

}
