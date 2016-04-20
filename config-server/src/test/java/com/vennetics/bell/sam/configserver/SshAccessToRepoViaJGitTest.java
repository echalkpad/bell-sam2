package com.vennetics.bell.sam.configserver;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
//import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.After;
//import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.vennetics.shared.test.utils.categories.IntegrationTest;

/**
 * Test Case to verify that we have a working ssh connection to the github repo
 * for the config server. The Spring Cloud Config uses JGit underneath to create
 * a local clone of the repo so this test verifys that our ssh foundation is as
 * expected.
 * 
 * @author markcorkery
 *
 */
@Category(IntegrationTest.class)
public class SshAccessToRepoViaJGitTest {

    private String localPath, remotePath;
    private File file;

    @Before
    public void init() throws IOException {
        localPath = "build/testRepo";
        file = new File(localPath);
        remotePath = "git@bell-sam-config.github.com:Vennetics/bell-sam-config.git";
    }

    @After
    public void cleanUp() throws IOException {
        FileUtils.deleteDirectory(file);
    }

    @Test
    public void testClone() throws IOException, GitAPIException {
        Git.cloneRepository().setURI(remotePath).setDirectory(new File(localPath)).call();
    }

}
