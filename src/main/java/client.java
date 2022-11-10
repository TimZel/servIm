import java.io.*;
import java.net.Socket;
import java.util.Scanner;


public class client {
    public static void main(String[] args) throws IOException {
        Socket client = new Socket("localhost", 12000);//создаю сокет подключения к серверу
        //с заданными портом и айпи
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));//открываю выводящий поток для запросов
        Scanner reader = new Scanner(client.getInputStream());//открываю входящий поток для считывания ответов сервера

        writer.write("GET /home/zelyanin/Desktop/testFolder/test2.txt HTTP/1.0\n"); //отправляю запрос (если менять в строке вид метода, то будет разный результат)
        writer.flush();//сбрасываю поток

        while(reader.hasNext()) { //вывожу в консоль ответ сервера
            System.out.println(reader.nextLine());//вывожу инфо в консоль
        }
        //закрываю потоки
        reader.close();
        writer.close();
        client.close();
    }
}