package com.wenresearch.mogaway.test;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.wenresearch.mogaway.Application;
import com.wenresearch.mogaway.client.ArchiveServiceClient;
import com.wenresearch.mogaway.model.Document;
import com.wenresearch.mogaway.model.DocumentMetadata;
import com.wenresearch.mogaway.service.ArchiveService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class TestArchiveClient {

private static final Logger LOG = LoggerFactory.getLogger(TestArchiveClient.class);
    
    private static final String TEST_FILE_DIR = "test-uploads";

    ApplicationContext applicationContext;
    
    @Autowired
    @Qualifier("archiveServiceClient")
    ArchiveService client;

    @Before
    public void setUp() throws IOException {
        testUpload();
    }

    @After
    public void tearDown() {
        deleteDirectory(new File(TEST_FILE_DIR));
    }

//    @Test
//    public void testFindDocuments() {
//        List<DocumentMetadata> result = client.findDocuments(getPersonName(), null);
//        assertNotNull("Result is null", result);
//        assertTrue("Result is empty", !result.isEmpty());
//        for (DocumentMetadata documentMetadata : result) {
//            assertEquals("Person name is not : " + getPersonName(), getPersonName(), documentMetadata.getPersonName());
//        }
//    }

    @Test
    public void testUpload() throws IOException {
        List<String> fileList = getFileList();
        for (String fileName : fileList) {
            uploadFile(fileName);
        }
        // testFindDocuments();
    }

    private void uploadFile(String fileName) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(TEST_FILE_DIR).append(File.separator).append(fileName);
        // Path path = Paths.get(sb.toString());
        byte[] fileData = null; //Files.readAllBytes(path);
        Date today = Calendar.getInstance().getTime();
        String personName = getPersonName();        
        DocumentMetadata metadata = client.save(new Document(fileData, fileName, today, personName));
        if (LOG.isDebugEnabled()) {
            LOG.debug("Document saved, uuid: " + metadata.getUuid());
        }
    }

    private String getPersonName() {
        return this.getClass().getSimpleName();
    }

    private List<String> getFileList() {
        File file = new File(TEST_FILE_DIR);
        String[] files = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isFile();
            }
        });
        return Arrays.asList(files);
    }

    public static boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }
}
