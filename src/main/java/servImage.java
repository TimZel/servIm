import java.io.*;
import java.net.ServerSocket;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class servImage {
    public static final HashMap<String, String> mapContentType = new HashMap<>();
    public static final  String notFound = "HTTP/1.0 404 Not Found\r\n" + "Content-Type: text/html\r\n" + "\r\n";
    public static void main(String[] args) throws IOException {
        mapContentType.put(".txt", "text/html");
        mapContentType.put(".html", "text/html");
        mapContentType.put(".gif", "image/gif");
        mapContentType.put(".jpeg", "image/jpeg");
        mapContentType.put(".png", "image/png");
        int visit = 0;
        String request = "";
        String filePath = "";
        String exeType = "";
        String contentType = "";
        ServerSocket servsock = new ServerSocket(12000);//серверсокет с указанным портом
        Pattern pattern = Pattern.compile("(/[^\s]*)(\\.[^\s]*)");//паттернs
        do {
            try ( var socket = servsock.accept();// сокет соединения с клиентом
                 var isr = new BufferedReader(new InputStreamReader(socket.getInputStream()));//принимаю поток
                 var out = new DataOutputStream(socket.getOutputStream()) ) //вывожу поток
            {
                request = isr.readLine();//считываю входящую инфу
                Matcher matcher = pattern.matcher(request);//создаю объект matcher типа Matcher на основе поступившей от клиента информации
                if (matcher.find()) {
                    filePath = matcher.group(0);//сохраняю путь
                    exeType = matcher.group(2);
                }
                contentType = mapContentType.get(exeType);
                System.out.println("Client " + (++visit) + " accepted.");//информирую об обращении клиента
                File file = new File(filePath);//создаю файл  и передаю в него полученный путь
                try (var inF = new FileInputStream(file)) { //открываю поток для чтения файла
                    int qtyBytes = (int) getFileSizeBytes(file);;//инициирую переменную
                    byte[] bytes = new byte[qtyBytes];//создаю байт-массив для хранения информации из файла
                    String httpResponse = "HTTP/1.0 200 OK\r\n" + "Content-Type: " + contentType + "\r\n" + "\r\n";
                    out.write(httpResponse.getBytes());//отправляю хттп-ответ
                    while ((inF.read(bytes)) > -1) {
                        out.write(bytes, 0, qtyBytes);//отправляю содержимое файла
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
    private static long getFileSizeBytes(File file) { //узнаю объем файла
        return file.length();
    }
}
