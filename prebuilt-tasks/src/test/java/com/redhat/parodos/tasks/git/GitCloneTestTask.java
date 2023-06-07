package com.redhat.parodos.tasks.git;

import java.nio.file.Files;
import java.nio.file.Path;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

@Slf4j
public class GitCloneTestTask {

	// private String sshKeyPath = "/home/eloy/.ssh/id_rsa";
	private String sshKeyPath = "/opt/keys/id_rsa";

	private String repoPath = "git@github.com:eloycoto/spring-helloworld.git";

	@BeforeEach
	public void setup() throws Exception {
	}

	@Test
	public void test() throws Exception {
		log.error("Test-->");
		Path tempDir = Files.createTempDirectory("git-repo");

		Path path = Path.of(sshKeyPath);
		Git git = Git.cloneRepository().setURI(repoPath).setBranch("refs/heads/main").setDirectory(tempDir.toFile())
				.setTransportConfigCallback(GitUtils.getTransport(path)).call();

		git.close();

	}

	// public TransportConfigCallback setTransport() {
	// var sshSessionFactory = new JschConfigSessionFactory() {
	// @Override
	// protected void configure(OpenSshConfig.Host host, Session session ) {
	// session.setConfig("StrictHostKeyChecking", "no");
	// session.setConfig("PreferredAuthentications", "publickey");
	// session.setConfig("IdentifyFile", sshKeyPath);
	// //session.setConfig("user", "eloycoto");
	// }
	//
	// @Override
	// protected JSch createDefaultJSch(FS fs) throws JSchException {
	// JSch defaultJSch = super.createDefaultJSch(fs);
	// defaultJSch.setLogger(new com.jcraft.jsch.Logger() {
	// public boolean isEnabled(int level) {
	// // Enable debug logging for all levels
	// return true;
	// }
	//
	// public void log(int level, String message) {
	// }
	// });
	// log.error("-------------------------------------------------------");
	// log.error("-------------------------------------------------------");
	// log.error("-------------------------------------------------------");
	// log.error("-------------------------------------------------------");
	// log.error("-------------------------------------------------------");
	// defaultJSch.removeAllIdentity();
	// defaultJSch.addIdentity(sshKeyPath, "");
	// log.error("GetIdentityNames --> {}", defaultJSch.getIdentityNames());
	// log.error("xxxxxxxxxxxxxxxxxxxxxxxxxxx");
	// log.error("xxxxxxxxxxxxxxxxxxxxxxxxxxx");
	// return defaultJSch;
	// }
	//
	// };
	// return new TransportConfigCallback() {
	// @Override
	// public void configure(Transport transport) {
	// SshTransport sshTransport = (SshTransport) transport;
	// sshTransport.setSshSessionFactory(sshSessionFactory);
	// }
	// };
	// }

}
