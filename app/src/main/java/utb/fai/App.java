package utb.fai;

public class App {

    public static void main(String[] args) {
        if(args.length != 2){
            System.out.println("error while parsing arguments");
            return;
        }
        TelnetClient telnetClient = new TelnetClient(args[0], Integer.parseInt(args[1]));
        telnetClient.run(); // run telnet client
    }
}
