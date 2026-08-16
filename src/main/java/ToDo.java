import java.util.Arrays;

public class ToDo extends Task{

    public ToDo(String[] words){
        super(String.join(" ", Arrays.copyOfRange(words, 1, words.length)));
    }

    @Override
    public String toString(){
        return "[T]" + super.toString();
    }
}
