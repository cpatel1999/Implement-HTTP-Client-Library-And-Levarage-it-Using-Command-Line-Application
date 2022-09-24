import java.util.Scanner;

public class HTTPCommandLineApplication {

    public static void main(String args[]) {
        String userInput;
        Scanner sc = new Scanner(System.in);

        do {
            userInput = sc.nextLine();
            if(userInput.startsWith("httpc")) {
                if(userInput.contains("help") && userInput.indexOf("help") == 6) {

                    if(userInput.endsWith("help")) {

                        System.out.println("\nhttpc is a curl-like application but supports HTTP protocol only.\n"
                                + "Usage:\n"
                                + "\t\thttpc command [arguments]\n"
                                + "The commands are:\n"
                                + "\t\tget     get executes a HTTP GET request and prints the response.\n"
                                + "\t\tpost    post executes a HTTP POST request and prints the response.\n"
                                + "\t\thelp    prints this screen.\n");

                    } else if (userInput.contains("get") && userInput.indexOf("get") == 11 && userInput.endsWith("get")) {

                        System.out.println("\nusage: httpc get [-v] [-h key:value] URL\n"
                                + "Get executes a HTTP GET request for a given URL.\n"
                                + "\t -v \t\t Prints the detail of the response such as protocol, status,\n"
                                + "and headers.\n"
                                + "\t -h key:value \t\t Associates headers to HTTP Request with the format\n"
                                + "'key:value'");

                    }  else if (userInput.contains("post") && userInput.indexOf("post") == 11 && userInput.endsWith("post")) {

                        System.out.println("\nusage: httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL\n"
                                + "Post executes a HTTP POST request for a given URL with inline data or from file.\n"
                                + "\t -v \t\t Prints the detail of the response such as protocol, status,\n" + "and headers.\n"
                                + "\t -h key:value \t\t Associates headers to HTTP Request with the format\n" + "'key:value'.\n"
                                + "\t -d string \t\t Associates an inline data to the body HTTP POST request.\n"
                                + "\t -f file \t\t Associates the content of a file to the body HTTP POST\n" + "request.\n"
                                + "\nEither [-d] or [-f] can be used but not both.\n");

                    } else {
                        System.out.println("Invalid help Command");
                    }
                }
            }

        } while(!userInput.equals("exit"));

    }

}
