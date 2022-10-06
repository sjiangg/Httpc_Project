import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;


public class Httpc {
//The implemented client should be named httpc (the name of the produced executable).

    public static boolean isV; // -v enables a verbose output from the command-line
    // expected to print all the status, and its headers, then the contents of the response


    // could contain parameters of the HTTP operation.

    // -h [pass the headers value]; setting the header of the request in the format "key: value."
    // can have multiple headers by having the -h option before each header parameter.
    // -d
    /**
     * get URL
     * get -v URL
     * get -h URL
     * get -v -h URL
     *
     * post URL
     */

    public static ArrayList<String> headers = new ArrayList<String>();
    public static URL url; // URL determines the targeted HTTP server.
    public static String host;

    public static void main( String[] args )
    {
        System.out.println(Arrays.toString(args));
        //check for invalid input
        if(args.length <= 1){
            System.out.println("insufficient length");
            System.out.println(invalidInputMessage());
                System.exit(0);
            }
        else{

            if(!args[0].equals("httpc") ){
                System.out.println("did not put httpc");
                System.out.println(invalidInputMessage());
                System.exit(0);
            }

            if(!(args[1].equals("get") || args[1].equals("post") || args[1].equals("help"))){
                System.out.println("did not put an operation");
                System.out.println(invalidInputMessage());
                System.exit(0);
            }

            if(args[1].equals("help")) {
                System.out.println(help(args));
                System.exit(0);
            }

            try
            {
                for(String arg: args){
                    if(arg.startsWith("http:")) url = new URL(arg);
                }

                System.out.println(url);
                System.out.println("HOST : " + url.getHost());
                System.out.println("PATH : " + url.getPath());
                System.out.println("QUERY : " + url.getQuery());
                host = url.getHost();
                //URL url = new URL(server);



                //URLConnection urlc = url.openConnection();
                // Create a socket to the web server listening on port 80.
                // Connect to the server, Creates a stream socket and connects it to the specified port number on the named host.
                Socket socket = new Socket(host, 80);
                //TODO: make sure if server is listening...


                // Create input and output streams to read from and write to the server
                // out - write request to server
                PrintWriter out = new PrintWriter( socket.getOutputStream() );
                // in - read response from server
                BufferedReader in = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );

                //GET operation
                if(args[1].equals("get")){
                    for(int i = 0; i < args.length; i++){
                        if(args[i].equals("-v")) isV = true;
                        if(args[i].equals("-h")) {
                            for(int j = i + 1; j < args.length; j++){
                                if(args[j].matches("[a-zA-Z]+\\s+:\\s+[a-zA-Z]+\\s+:\\s+\\d+\\s+:\\s+\\d+") && !args[j].equals(url))
                                    headers.add(args[j]);
                            }
                        }
                    }
                    sendGet(socket, out, in, url);
                }

                //POSY operation
                if(args[1].equals("post")){
                    for(int i = 0; i < args.length; i++){
                        if(args[i].equals("-v")) isV = true;
                        if(args[i].equals("-h")) {
                            for(int j = i + 1; j < args.length; j++) {
                                if (args[j].matches("[a-zA-Z]+\\s+:\\s+[a-zA-Z]+\\s+:\\s+\\d+\\s+:\\s+\\d+") && !args[j].equals(url))
                                    headers.add(args[j]);
                            }
                        }
                        //TODO: --d, -f

                    }
                    sendGet(socket, out, in, url);
                }

                // Close our streams
                in.close();
                out.close();
                socket.close();
            }
            catch( Exception e )
            {
                e.printStackTrace();
            }
        }


    }

    private static void sendGet(Socket socket, PrintWriter out, BufferedReader in, URL url) {

        String result = null;

        out.write("GET " + url.getPath() + "?" + url.getQuery() + " " + "HTTP/1.0");
        out.write("Host: " + url.getHost());

        System.out.println("GET " + url.getPath() + url.getQuery() + " " + "HTTP/1.0");
        // GET /getcourse=networking HTTP/1.0 MISSING ?

        System.out.println("Host: " + url.getHost());

        if (!headers.isEmpty()) {
            for (int i = 0; i < headers.size(); i++) {
                out.write(headers.get(i));
            }
        }

        out.flush();


        try {
            if(isV == true){
                if((result = in.readLine()) != null) System.out.println(result);
                while(result != null){
                    result = in.readLine();
                    System.out.println(result);
                }
            }
            else{
                System.out.println("v: false TODO");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String help(String[] args){

            if(args[1].equals("help") && args.length == 2){
                return("Output:\n" +
                        "httpc is a curl-like application but supports HTTP protocol only.\n" +
                        "Usage:\n" +
                        "httpc command [arguments]\n" +
                        "The commands are:\n" +
                        "get executes a HTTP GET request and prints the response.\n" +
                        "post executes a HTTP POST request and prints the response.\n" +
                        "help prints this screen.\n" +
                        "Use \"httpc help [command]\" for more information about a command.");
            }

            if(args[1].equals("help") && args[2].equals("get")){
                return("Output:\n" + "usage: httpc get [-v] [-h key:value] URL\n" +
                        "Get executes a HTTP GET request for a given URL.\n" +
                        "-v Prints the detail of the response such as protocol, status, and headers.\n" +
                        "-h key:value Associates headers to HTTP Request with the format 'key:value'.\n");
            }
            if(args[1].equals("help") && args[2].equals("post")){
                return("Output:\n" +
                        "usage: httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL\n" +
                        "Post executes a HTTP POST request for a given URL with inline data or from \n" +
                        "file.\n" +
                        "-v Prints the detail of the response such as protocol, status, and headers.\n" +
                        "-h key:value Associates headers to HTTP Request with the format 'key:value'.\n" +
                        "-d string Associates an inline data to the body HTTP POST request.\n" +
                        "-f file Associates the content of a file to the body HTTP POST request.\n" +
                        "Either [-d] or [-f] can be used but not both.");
            }
        return(" invalid input for help command");
        }


        public static String invalidInputMessage(){
            return(" Invalid commands." +
                    "\n Commands:  httpc (get|post) [-v] (-h \"k:v\")* [-d inline-data] [-f file] URL " +
                    "\n For help:  httpc help");
        }
    }


