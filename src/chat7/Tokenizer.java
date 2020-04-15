package chat7;

import java.util.StringTokenizer;

public class Tokenizer {
	/*
	 * 문자열을 잘라주는 클래스 StringTokeniozer입니다. 비슷한 메소드로 String 클래스에 split()메소드가 있죠
	 * 
	 * StringTokenizer생성자 2개를 소개한다.
	 * 
	 * StringTokenizer(String str) //str문자열을 자르기 위한 토크나이저 객체를 생성한다. 자르는 기준을 기입하지
	 * 않았으므로 '스페이스바'를 기준으로 문자열을 자른다.
	 * 
	 * StringTokenizer(String str, String split) // str문자열을 자르기 위한 토크나이저 객체를 생성한다.
	 * split에 입력된 문자열을 기준으로 문자를 잘라냄 기준 문자는 문자열에서 소멸된다.
	 */

	public static void main(String[] args) {

		String str = "1101,한송이,45,67,89,100";
		StringTokenizer st = new StringTokenizer(str, ",");
		String[] array = new String[st.countTokens()]; // countTokens() :문자열을 잘라낸 결과로 가지게될 토근의 객수를 리턴합ㄴ디ㅏ. 즉 몇개로 짤리는지 볼
														// 수있음
		int i = 0;
		while (st.hasMoreElements()) { // 현재 토크나이저 안에 받아낼 토근이 더 있는지 체크한다.
			array[i++] = st.nextToken(); // 토크나이저는 넥스트 토큰을 통해 순차적으로 토근을 받아냅니다. 앞에서부터 1토큰을 짤라낸다 다음번 nexToken()에서는 이번에 받은
											// 문자열의
			// 다음 문자열을 리턴한다.

		}
		for (int j = 0; j < array.length; j++) {
			System.out.println(array[j]);
		}
	}

}
