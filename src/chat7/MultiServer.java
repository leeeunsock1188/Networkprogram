package chat7;

import java.awt.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

public class MultiServer extends ConnectDB{
   
   static ServerSocket serverSocket = null;
   static Socket socket = null;
   //클라이언트 정보 저장을 위한 Map컬렉션 정의
   //키(key)와 값(value)의 쌍으로 이루어진 데이터의 집합
   //순서는 유지되지 않고, 키는 중복을 허용하지 않으며 값의 중복을 허용 
   Map<String, PrintWriter> clientMap;
   
   //생성자
   public MultiServer() {
      super();
      
      //클라이언트의 이름과 출력스트림을 저장할 HashMap생성
      clientMap = new HashMap<String, PrintWriter>(); //HashMap (중복과 순서가 허용되지 않으며 null값이 올 수 있다.)
      //HashMap동기화 설정. 쓰레드가 사용자정보에 동시에 접근하는것을 차단한다.
      Collections.synchronizedMap(clientMap);
   }
   
   //서버 초기화 
   public void init() {
      
      try {
         serverSocket = new ServerSocket(9999);
         System.out.println("서버가 시작되었습니다..");
         
         while(true) {
            socket = serverSocket.accept();
            System.out.println(socket.getInetAddress()+":"+socket.getPort());
            /*
            클라이언트의 메세지를 모든 클라이언트에게 전달하기 위한
            쓰레드 생성 및 start.
             */
            Thread mst = new MultiServerT(socket);
            mst.start();
         }
      }
      catch (Exception e) {
         e.printStackTrace();
       
      }
      finally {
         try {
            serverSocket.close();
         }
         catch (Exception e) {
            e.printStackTrace();
         }
      }
   }
   
   //메인메소드 : Server객체를 생성한후 초기화한다.
   public static void main(String[] args) {
      MultiServer ms = new MultiServer();
      ms.init();
   }
   
   //접속된 모든 클라이언트에게 메세지를 전달하는 역항의 메소드
   public void sendAllMsg(String name, String msg) {
	   
      //Map에 저장된 객체의 키값(이름)을 먼저 얻어온다.
	   if(msg.equals("/lest")) {
		   Iterator<String> it = clientMap.keySet().iterator();
		   while(it.hasNext()) {
		PrintWriter it_out = (PrintWriter)clientMap.get(it.next());
			it_out.println("접속자수"+it.next());
		
		   }
	   }
      Iterator<String> it = clientMap.keySet().iterator();
      
      //저장된 객체(클라이언트)의 갯수만큼 반복한다.
      
      while(it.hasNext()) {
    	  String name2=it.next();
         try {
            //각 클라이언트의 PrintWriter객체를 얻어온다.
            PrintWriter it_out = (PrintWriter) clientMap.get(name2);
            
            //클라이언트에게 메세지를 전달한다.
            /*
            매개변수 name이 있는 경우에는 이름+메세지
            없는경우에는 메세지만 클라이언트로 전달한다.
             */
           
            if(name.equals("")) {
               it_out.println(URLEncoder.encode(msg,"UTF-8"));
            }
            
            else if(name2.equals(name)){
            	it_out.println(msg);
            }
            else {
               it_out.println("["+name+"]:"+msg);
            }
            
            
         }
         catch (UnsupportedEncodingException e1) {
        	 e1.printStackTrace();
			
		}
         catch (Exception e) {
            System.out.println("예외:"+e);
            
         }
         
      }
   }
   
   //내부클래스
   class MultiServerT extends Thread{
      
      //멤버변수
      Socket socket;
      PrintWriter out = null;
      BufferedReader in = null;
      
      //생성자 : Socket을 기반으로 입출력 스트림을 생성한다.
      public MultiServerT(Socket socket) {
         this.socket = socket;
         try {
            out = new PrintWriter(this.socket.getOutputStream(),true);
            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream(),"UTF-8"));
                  
         }
         catch (Exception e) {
            System.out.println("예외:"+e);
         }
      }
      
      @Override
      public void run() {
         //클라이언트로부터 전송된 "대화명"을 저장할 변수
         String name = "";
         //메세지 저장용 변수
         String s = "";
         
         try {
            //클라이언트의 이름을 읽어와서 저장
            name = in.readLine();//클라이언트에서 처음으로 보내는 메세지는
            name = URLDecoder.decode(name,"UTF-8");//클라이언트가 사용할 이름이다.
            //접속한 클라이언트에게 새로운 사용자의 입장을 알림.
            //접속자를 제외한 나머지 클라이언트만 입장메세지를 받는다.
            sendAllMsg("",name+ "님이 입장하셨습니다.");
            
            //현재 접속한 클라이언트를 HashMap에 저장한다.
            clientMap.put(name, out);
            
            //HashMap에 저장된 객체의 수로 접속자수를 파악할수 있다.
            System.out.println(name+" 접속");
            System.out.println("현재 접속자 수는"+clientMap.size()+"명 입니다.");
            
            //입력한 메세지는 모든 클라이언트에게 Echo된다.
            
            while(in!=null) {
            	
               
               s = in.readLine();
               s = URLDecoder.decode(s,"UTF-8");
               
             
               
               if(s==null) break;
                  
            
            	
            	   
              else {//일반대화
            	   sendAllMsg(name,s);
               }
               //일반대화
               //**db처리는 여기서 진행
               String query = "INSERT INTO chatting_td VALUES (seq_chatting.nextval,?,?,sysdate)";
               
               psmt = con.prepareStatement(query);
               
               psmt.setString(1, name);
               psmt.setString(2, s);
               psmt.executeUpdate();
               /////////////////////////쿼리작성문
               
               //읽어온 메세지를 콘솔에 출력하고...
               System.out.println(name+ " >> " + s);
               //클라이언트에게 Echo해준다.
              StringTokenizer st = new StringTokenizer(s + " ");
              String[] st_array = new String[st.countTokens()];
              int i=0; 
              while(st.hasMoreElements()) {
            	  st_array[i++]=st.nextToken();
              }
              Iterator<String> iterator  = clientMap.keySet().iterator();
              if(s.charAt(0)=='/') {
            	  switch (st_array[0].substring(1)) {
            	  case "list":
            		  while (iterator.hasNext()) {
            			  out.println("접속자명:"+ iterator.next());
            		  }
            		  break;
				case "to":
					System.out.println("to 들어옴!");
					
					
					break;

				default:
					break;
				}
              }
               
               
            }
         }
         catch (Exception e) {
            e.printStackTrace();
         }
         finally {
            /*
            클라이언트가 접속을 종료하면 예외가 발생하게 되어 finally로
            넘어오게된다. 이때 "대화명"을 통해 remove()시켜준다.
             */
            clientMap.remove(name);
            sendAllMsg("",name+"님이 퇴장하셨습니다.");
            //퇴장하는 클라이언트의 쓰레드명을 보여준다.
            System.out.println(name + " ["+
            Thread.currentThread().getName()+ "] 퇴장");
            System.out.println("현재 접속자 수는"+clientMap.size()+"명 입니다.");
            
            try {
               in.close();
               out.close();
               socket.close();
            }
            catch (Exception e) {
               e.printStackTrace();
            }
         }
      }
      
      
   }
 

}