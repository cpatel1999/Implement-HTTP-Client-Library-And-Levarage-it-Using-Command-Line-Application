import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HTTPClientLibrary {

    String hostName, rawPath, rawQuery;
    int portNumber;

    public String fetchValidURL(String URLString) {
        if (URLString.contains(" ")) {
            URLString = URLString.split(" ")[0];
        }
        return URLString;
    }

    public void extractDatafromURL(URI uri) {
        hostName = uri.getHost();
        rawPath = uri.getRawPath();
        rawQuery = uri.getRawQuery();
        portNumber = uri.getPort();

        if (rawPath == null || rawPath.length() == 0) {
            rawPath = "/";
        }

        if (rawQuery != null) {
            rawQuery = "?" + rawQuery;
        } else {
            rawQuery = "";
        }

        if (portNumber == -1) {
            portNumber = 80;
        }
    }

    public StringBuilder createHeaderList(List<String> dataList, StringBuilder requestString) {
        ArrayList<String> headers = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).equals("-h")) {
                String temp;
                //headerList.add(data.get(i + 1));

                //TODO: Check this condition
                if (dataList.get(i + 1).charAt(0) == '\'') {
                    temp = dataList.get(i + 1).substring(1, dataList.get(i + 1).length() - 1);
                } else {
                    temp = dataList.get(i + 1);
                }
                headers.add(temp);
            }
        }

        if (!headers.isEmpty()) {
            for (int i = 0; i < headers.size(); i++) {
                String header = headers.get(i);
                requestString.append(header.split(":")[0] + ": " + header.split(":")[1] + "\n");
            }
        }
        return requestString;
    }

    public String checkVerbosity(String data, String response) {
        if (!data.contains("-v")) {
            response = response.substring(response.indexOf("{"), response.lastIndexOf("}"));
        }
        return response + "}";
    }

    public void writeResponseToFile(String fileName, String data) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("Files/" + fileName));

            bufferedWriter.write(data);
            bufferedWriter.close();

            System.out.println("Response successfully saved to " + fileName);

        } catch (IOException ex) {
            System.out.println("Error Writing file named '" + fileName + "'" + ex);
        }
    }

    public String readDataFromFile(String fileName) {
        StringBuilder lines = new StringBuilder("");
        String line = null;

        try {

            BufferedReader bufferedReader = new BufferedReader(new FileReader("Files/" + fileName));

            while ((line = bufferedReader.readLine()) != null) {
                lines.append(line + "\n");

            }
            bufferedReader.close();
        } catch (IOException ex) {
            System.out.println("Error reading file named '" + fileName + "'" + ex);
        }

        return lines.toString();
    }

    public String get(String data) throws URISyntaxException, IOException {

        StringBuilder responseString = new StringBuilder("");
        StringBuilder requestString = new StringBuilder("");

        //Fetch URL from argument
        String URLString = data.substring(data.indexOf("http://"), data.length() - 1);

        //Check if URL is valid
        URLString = fetchValidURL(URLString);

        //Splits user arguments in dataList.
        List<String> dataList = Arrays.asList(data.split(" "));

        //Create URI object for URL
        URI uri = new URI(URLString);
        extractDatafromURL(uri);

        //Form the request string
        requestString.append("GET " + rawPath + rawQuery + " HTTP/1.0" + "\n" + "Host: " + hostName + "\n");

        // Create header list
        requestString = createHeaderList(dataList, requestString);
        requestString.append("\n");

        //Socket created to the host
        Socket socket = new Socket(hostName, portNumber);

        //Request sent
        PrintWriter writer = new PrintWriter(socket.getOutputStream());
        writer.write(requestString.toString());
        writer.flush();

        //Response received
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String line;
        String response;

        //Read response
        while ((line = br.readLine()) != null) {
            responseString.append(line + "\n");
        }
        response = responseString.toString();

        //Check verbosity [-v]
        response = checkVerbosity(data, response);

        // Writing Response to a file using -o
        if (data.contains("-o ")) {

            String fileName = dataList.get(dataList.indexOf("-o") + 1);

            writeResponseToFile(fileName, response);
            return "";

        }
        return response;
    }

    public String post(String data) throws URISyntaxException, IOException {

        StringBuilder responseString = new StringBuilder("");
        StringBuilder requestString = new StringBuilder("");
        String arguments = "";
        int length = 0;

        //Fetch URL from argument
        String URLString = data.substring(data.indexOf("http://"));

        //Check if URL is valid
        URLString = fetchValidURL(URLString);

        //Splits user arguments in dataList.
        List<String> dataList = Arrays.asList(data.split(" "));

        // Create URI Object from URL
        URI uri = new URI(URLString);
        extractDatafromURL(uri);

        //Form request string
        requestString.append("POST " + rawPath + " HTTP/1.0" + "\n" + "Host: " + hostName + "\n");

        // Read associated inline data represented using -d
        if (data.contains("-d ")) {
            arguments = data.substring(data.indexOf("{", data.indexOf("-d")), data.indexOf("}") + 1);
            length = arguments.length();
        }

        // Read file Data using -f
        if (data.contains("-f ")) {

            String fileName = dataList.get(dataList.indexOf("-f") + 1);

            arguments = readDataFromFile(fileName);
            length = arguments.length();
        }

        requestString.append("Content-Length: " + length + "\n");

        // Create header list
        requestString = createHeaderList(dataList, requestString);
        requestString.append("\n");
        requestString.append(arguments);

        //Socket created to the host
        Socket socket = new Socket(hostName, portNumber);

        //Request sent
        PrintWriter writer = new PrintWriter(socket.getOutputStream());
        writer.write(requestString.toString());
        writer.flush();

        // Response received
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String line;
        String response;

        //Read response
        while ((line = br.readLine()) != null) {
            responseString.append(line + "\n");
        }
        response = responseString.toString();

        //Check verbosity [-v]
        response = checkVerbosity(data, response);

        // Writing Response to a file using -o
        if (data.contains("-o ")) {
            String fileName = dataList.get(dataList.indexOf("-o") + 1);
            writeResponseToFile(fileName, response);
            return "";
        }

        return response;
    }

}
