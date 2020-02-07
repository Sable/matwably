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
        return Util.createTestsFromDir(pathToDir,glob,null);
    }
    public static Collection<DynamicTest> createTestsFromDir(String pathToDir, String glob, String flag){

        try{
            List<String> files =
                    listFilesUsingDirectoryStream(Paths.get(pathToDir), glob);
            return files.stream().map((pathToFile) ->
                    DynamicTest.dynamicTest(pathToFile,
                            ()->{
                                String[] args = (flag == null)? new String[]{pathToFile, "--args", "\"[DOUBLE&1*1&REAL]\""}
                                    :new String[]{pathToFile, "--args", "\"[DOUBLE&1*1&REAL]\"", flag};
                                System.out.println(String.join(" ", args));
                                Main.main(args);
                                assertTrue(true,  String.join(" ", args));
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
                    fileList.addAll(listFilesUsingDirectoryStream(path, glob));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileList;
    }
}
