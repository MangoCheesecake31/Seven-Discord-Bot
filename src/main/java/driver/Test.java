package driver;

public class Test {
    public static void main(String[] args) {

        String name = "Alpha";
        String author = "LAYTO";

        System.out.println(name);
        System.out.println(author);

        String output = String.format("00 | %16s - %-32s", name, author);

        System.out.println(output);
    }
}
