package util;

import java.util.ArrayList;
import java.util.Arrays;

public class TestCompilerFolderBuilder {
    private String folder = "";
    private String prefix = "";
    private ArrayList<String> command_line_flags = new ArrayList<>();

    public TestCompilerFolderBuilder(String folder, String prefix){
        this.folder = folder;
        this.prefix = prefix;
    }

    public void addCompilerFlags(String... flags){
        command_line_flags.addAll(Arrays.asList(flags));
    }
    public String getStringFlags(){
        return command_line_flags.stream().reduce("", ( arg1, arg2)->" "+arg1+" "+arg2);
    }
    public String getFolder() {
        return folder;
    }
}
