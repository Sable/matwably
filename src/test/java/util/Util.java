package util;

import matwably.Main;
import org.junit.jupiter.api.DynamicTest;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Util {
    public static Collection<DynamicTest> createTestsFromDir(String pathToDir, String glob){
        try{
            List<String> files =
                    listFilesUsingDirectoryStream(Paths.get(pathToDir), glob);
            files.forEach(System.out::println);
            return files.stream().map((pathToFile) ->
                    DynamicTest.dynamicTest(pathToFile,
                            ()->{
                                String[] args = {pathToFile, "--args", "\"[DOUBLE&1*1&REAL]\""};
                                Main.main(args);
                                assertTrue(true, pathToFile);
                            })).collect(Collectors.toSet());
        }catch (Exception e){
            throw new Error(e);
        }
    }
    public static List<String> listFilesUsingDirectoryStream(Path dir, String glob) throws IOException {
        ArrayList<String> fileList = new ArrayList<>();
        try (DirectoryStream<Path> stream =
                     Files.newDirectoryStream(dir)) {
            for (Path path : stream) {
                if (!Files.isDirectory(path)) {
                    if(path.getFileName().toString()
                            .startsWith(glob)){
                        fileList.add(path
                                .toString());
                    }
                }else{
                    System.out.println(path+glob);
                    fileList.addAll(listFilesUsingDirectoryStream(path, glob));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileList;
    }
}
