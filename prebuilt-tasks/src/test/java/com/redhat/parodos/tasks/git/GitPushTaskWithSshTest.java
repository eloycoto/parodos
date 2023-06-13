package com.redhat.parodos.tasks.git;

import com.redhat.parodos.workflow.utils.WorkContextUtils;
import com.redhat.parodos.workflows.work.WorkContext;
import com.redhat.parodos.workflows.work.WorkReport;
import com.redhat.parodos.workflows.work.WorkStatus;
import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.common.file.FileSystemFactory;
import org.apache.sshd.common.file.nativefs.NativeFileSystemFactory;
import org.apache.sshd.common.keyprovider.FileKeyPairProvider;
import org.apache.sshd.server.config.keys.AuthorizedKeysAuthenticator;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.util.test.EchoShellFactory;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.RemoteAddCommand;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.URIish;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.util.test.CoreTestSupportUtils;

import javax.swing.filechooser.FileSystemView;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.UUID;
import org.apache.sshd.git.GitLocationResolver;
import org.apache.sshd.git.pack.GitPackCommandFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@Slf4j
public class GitPushTaskWithSshTest {
    private static SshServer sshd;

    private GitPushTask task;

    private Path gitRepoPath;

    private Path tempDir;

    private Path remoteRepoPath;

    private static String remoteRepoPathName = "remote-repo";
    private Repository repository;

    @BeforeEach
    public void setUp() throws Exception {
        task = new GitPushTask();
        task.setBeanName("git-push-task");

        tempDir = Files.createTempDirectory("git-repo");
        Path remoteTempDir = Files.createTempDirectory("git-repo-remote");
        remoteRepoPath = Files.createDirectory(remoteTempDir.resolve(remoteRepoPathName),
                PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxrwxrwx")));
        assertDoesNotThrow(() -> {
            Repository repo = initRepo(remoteRepoPath);
        });

        sshd = SshServer.setUpDefaultServer();
        sshd.setPort(2222);
        FileKeyPairProvider keyPairProvider = new FileKeyPairProvider(Path.of("/opt/keys/id_rsa"));
        sshd.setKeyPairProvider(keyPairProvider);
        AuthorizedKeysAuthenticator authorizedKeysAuthenticator = new AuthorizedKeysAuthenticator(Path.of("/opt/keys/id_rsa.pub"));
        sshd.setPublickeyAuthenticator(authorizedKeysAuthenticator);
        sshd.setShellFactory(new EchoShellFactory());

        GitLocationResolver gitLocationResolver = new GitLocationResolver()
        {
            @Override
            public Path resolveRootDirectory( String command, String[] args, ServerSession session, FileSystem fs )
                    throws IOException
            {
                Path path = remoteRepoPath.getParent();
                return remoteRepoPath.getParent();
            }
        };

        sshd.setCommandFactory( new GitPackCommandFactory()
                .withGitLocationResolver(gitLocationResolver));

        sshd.setPasswordAuthenticator((username, password, session) -> false);
        assertDoesNotThrow(() -> {
            sshd.start();
        });


        assertDoesNotThrow(() -> {
            CloneCommand command = new CloneCommand();
            command.setDirectory(tempDir.toFile());
            command.setURI("ssh://eloy@localhost:2222/remote-repo");
            command.setTransportConfigCallback(GitUtils.getTransport(Path.of("/opt/keys/id_rsa")));
            command.call();
        });


//        assertDoesNotThrow(() -> {
//            tempDir = Files.createTempDirectory("git-repo");
//            gitRepoPath = tempDir.resolve(GitConstants.GIT_FOLDER);
//            repository = initRepo(gitRepoPath);
//        });
    }

    @AfterEach
    public void tearDown() throws Exception {
        sshd.stop();
    }

    private Repository initRepo(Path path) throws Exception {
        InitCommand command = new InitCommand();
        command.setInitialBranch("main");
        command.setBare(true);
        command.setDirectory(path.toFile());
        Git git = command.call();
        assertThat(git).isNotNull();
        assertThat(git.getRepository().getFullBranch()).isEqualTo("refs/heads/main");
        assertThat(git.getRepository().getBranch()).isEqualTo("main");


//        RemoteAddCommand remoteAddCommand = git.remoteAdd();
//        remoteAddCommand.setName("origin");
//        remoteAddCommand.setUri(new URIish("ssh://user@localhost:2222/%s".formatted(remoteRepoPathName)));
//        remoteAddCommand.call();
//
        return git.getRepository();
    }


    @Test
    public void test() {
//        log.error("Sleep here!");
//        assertDoesNotThrow(() -> {
//            Thread.sleep(2000000);
//        });

        // given
        WorkContext ctx = getSampleContext();
        ctx.put("path", tempDir.toString());
        WorkContextUtils.addParameter(ctx, "remote", "origin");
        WorkContextUtils.addParameter(ctx, "credentials", "/opt/keys/id_rsa");

        // then
        task.preExecute(ctx);
        WorkReport report = task.execute(ctx);

        // given
        assertThat(1).isEqualTo(1);
        assertThat(report.getError()).isNull();
        assertThat(report.getStatus()).isEqualTo(WorkStatus.COMPLETED);
    }

    private WorkContext getSampleContext() {
        WorkContext context = new WorkContext();
        WorkContextUtils.setMainExecutionId(context, UUID.randomUUID());
        return context;
    }
}
