package com.project_team09.api.service;

import com.project_team09.api.util.ExcelTestGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ExcelParserServiceTest {

    @Autowired
    private ExcelParserService excelParserService;

    @Test
    public void testExcelParsing() throws Exception {
        // Test Excel dosyası oluştur
        String testFilePath = "test-cases.xlsx";
        ExcelTestGenerator.generateSampleTestFile(testFilePath); // Dosyayı oku ve parse et
        File file = new File(testFilePath);
        MockMultipartFile multipartFile;
        try (FileInputStream input = new FileInputStream(file)) {
            multipartFile = new MockMultipartFile("file",
                    file.getName(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    Files.readAllBytes(file.toPath()));
        }

        // Parse et
        var testCases = excelParserService.parseExcelFile(multipartFile);

        // Sonuçları kontrol et
        assertNotNull(testCases);
        assertTrue(testCases.size() > 0);

        // İlk test case'i kontrol et
        var firstTestCase = testCases.get(0);
        assertEquals("US01", firstTestCase.getUserStoryId());
        assertEquals("TC01", firstTestCase.getTestCaseId());
        assertEquals("Login işlemi başarılı olmalıdır", firstTestCase.getTestObjective());
        assertEquals(6, firstTestCase.getSteps().size());

        // Adımları kontrol et
        var firstStep = firstTestCase.getSteps().get(0);
        assertEquals(1, firstStep.getStepNumber());
        assertEquals("Go to Site", firstStep.getStepDescription());
        assertEquals("https://www.allovercommerce.com/", firstStep.getTestData());
        assertTrue(firstStep.getIsHome()); // Home olarak işaretli

        // Highlightları kontrol et
        var secondStep = firstTestCase.getSteps().get(1);
        assertTrue(secondStep.getIsHighlighted()); // Click içerdiği için sarı highlight olmalı

        // Cleanup
        file.delete();
    }
}
