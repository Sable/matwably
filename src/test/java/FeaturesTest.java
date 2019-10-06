/*
 * This Java source file was generated by the Gradle 'init' task.
 */

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.Collection;

import static util.Util.createTestsFromDir;

public class FeaturesTest {
    @TestFactory public Collection<DynamicTest> testCompilerFeatures(){
        return createTestsFromDir("src/test/features","");
    }
}
