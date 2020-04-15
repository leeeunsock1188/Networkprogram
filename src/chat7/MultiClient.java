package chat7;

import java.awt.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.Scanner;

public class MultiClient {

	public static void main(String[] args) {

		System.out.println("이름을 입력하세요.,");
		Scanner scan = new Scanner(System.in);
		String s_name = scan.nextLine();

		PrintWriter out = null;


		//Sender가 기능을 가져가므로 여기서는 필요없음
		//PrintWriter out = null;
		
		//Receiver가 기능을 가져가므로 여기서는 필요없음.
		//BufferedReader in = null;
		try {
		
			String ServerIP = "localhost";
			if (args.length > 0) {
				ServerIP = args[0];
			}
			Socket socket = new Socket(ServerIP, 9999);
			System.out.println("서버와 연결되었습니다.");

			//서버에서 보내는 Echo메세지를 클라이어트에 출력하기 위한 쓰레드 생성
			Thread receiver = new Receiver(socket);
			
			receiver.start();
			
			//클라이언트
			out = new PrintWriter(socket.getOutputStream(), true);
			out.println(URLEncoder.encode(s_name,"UTF-8"));

			while (out != null) {
				try {
					String s2 = scan.nextLine();
					if (s2.equals("q") || s2.equals("Q")) {
						break;
						
					}
					
					
					else {
						//클라이언트의 메세지를 서버로 전송한다. 
						out.println(URLEncoder.encode(s2,"UTF-8"));
					}
					
				} catch (Exception e) {
					System.out.println("예외:" + e);
				}
			}
			//스트림과 소켓을 종료한다. 
			out.close();
			socket.close();
		} catch (Exception e) {
			System.out.println("예외발생[MultiClient]" + e);
		}

	}

}
