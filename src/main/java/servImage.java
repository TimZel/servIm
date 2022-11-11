

import java.io.*;
import java.net.ServerSocket;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class servImage {
    public static HashMap<String, String> mapContentType = new HashMap<>()
    {{
        put(".txt", "text/html");
        put(".html", "text/html");
        put(".gif", "image/gif");
        put(".jpeg", "image/jpeg");
        put(".png", "image/png");
    }};

    public static final  String notFound = "HTTP/1.0 404 Not Found\r\n" + "Content-Type: text/html\r\n" + "\r\n";
    public static final Pattern pattern = Pattern.compile("(/[^\s]*)(\\.[^\s]*)");
    public static void main(String[] args) throws IOException {

        int visit = 0;
       
        ServerSocket servsock = new ServerSocket(12000);//серверсокет с указанным портом
        //паттернs
        do {
            try ( var socket = servsock.accept();// сокет соединения с клиентом
                 var isr = new BufferedReader(new InputStreamReader(socket.getInputStream()));//принимаю поток
                 var out = new DataOutputStream(socket.getOutputStream()) ) //вывожу поток
            {
                String request = isr.readLine();//считываю входящую инфу
                String filePath = "";
                String exeType = "";
                Matcher matcher = pattern.matcher(request);//создаю объект matcher типа Matcher на основе поступившей от клиента информации
                if (matcher.find()) {
                    filePath = matcher.group(0);//сохраняю путь
                    exeType = matcher.group(2);
                }
                String contentType = mapContentType.get(exeType);
                System.out.println("Client " + (++visit) + " accepted.");//информирую об обращении клиента
                File file = new File(filePath);//создаю файл  и передаю в него полученный путь
                try (var inF = new FileInputStream(file)) { //открываю поток для чтения файла
                    byte[] bytes = new byte[5*1024];//создаю байт-массив для хранения информации из файла
                    String httpResponse = "HTTP/1.0 200 OK\r\n" + "Content-Type: " + contentType + "\r\n" + "\r\n";
                    out.write(httpResponse.getBytes());//отправляю хттп-ответ
                    int count = 0;
                    while ((count = inF.read(bytes)) > -1) {
                        out.write(bytes, 0, count);//отправляю содержимое файла
                    }
                } catch (FileNotFoundException ex) {
                    System.out.println("Внимание! Розыск: " + ex);//шуткую, если файла нет
                    out.write(notFound.getBytes());
                }
            }
        } while (visit < 5);
        servsock.close();//закрываю сервер
        System.out.println("Server closed due to settings.");
    }
}
