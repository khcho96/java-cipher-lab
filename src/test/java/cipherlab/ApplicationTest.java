package cipherlab;

import static camp.nextstep.edu.missionutils.test.Assertions.assertRandomNumberInRangeTest;
import static org.assertj.core.api.Assertions.assertThat;

import camp.nextstep.edu.missionutils.test.NsTest;
import java.util.List;
import org.junit.jupiter.api.Test;

class ApplicationTest extends NsTest {

    /**
     * E+K(대소문자 구분 + 숫자 유지)에서 사용되는 랜덤 시퀀스. - 대문자 26개 치환키 생성 + 소문자 26개 치환키 생성 (총 52회 호출) - README의 "랜덤 치환 키 생성 규칙" 예시
     * 코드(남은 후보 리스트에서 인덱스를 뽑아 제거)를 기준으로 설계됨
     */
    private static final List<Integer> RANDOM_E_K = List.of(
            // A-Z targets: B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z,A
            67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 65,
            // a-z targets: b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z,a
            98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119,
            120, 121, 122, 97
    );

    /**
     * I+S(대소문자 미구분 + 숫자 치환)에서 사용되는 랜덤 시퀀스. - 소문자 26개 치환키 생성 + 숫자 10개 치환키 생성 (총 36회 호출)
     */
    private static final List<Integer> RANDOM_I_S = List.of(
            // a-z targets: b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z,a
            99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119,
            120, 121, 122, 97,
            // 0-9 targets: 1,2,3,4,5,6,7,8,9,0
            49, 50, 51, 52, 53, 54, 55, 56, 57, 48
    );

    @Override
    protected void runMain() {
        Application.main(new String[]{});
    }

    @Test
    void 예시1_EK_정상_동작한다() {
        assertRandomNumberInRangeTest(() -> {
            run("Hello, World!", "E", "K", "8");

            String out = output();
            assertThat(out).contains("암호화를 시작합니다.");
            assertThat(out).contains("암호문: Ifmmp, Xpsme!");
            assertThat(out).contains("체크섬: 1107");

            // 키 미리보기(각 섹션별 N개)
            assertThat(out).contains("[대문자]");
            assertThat(out).contains("- A -> B", "- B -> C", "- C -> D", "- D -> E", "- E -> F", "- F -> G", "- G -> H",
                    "- H -> I");
            assertThat(out).contains("[소문자]");
            assertThat(out).contains("- a -> b", "- b -> c", "- c -> d", "- d -> e", "- e -> f", "- f -> g", "- g -> h",
                    "- h -> i");

            // 빈도 리포트(공백/구두점 제외 기준)
            assertThat(out).contains("빈도 리포트 (TOP 3)");
            assertThat(out).contains("1) m : 3", "2) p : 2", "3) I : 1");

            assertThat(out).contains("복호화 검증: OK");
        }, 66, RANDOM_E_K.toArray(new Integer[0]));
    }

    @Test
    void 예시2_IS_입력오류_재입력후_정상_동작한다() {
        assertRandomNumberInRangeTest(() -> {
            run(
                    "Hello@World",   // 원문 형식 오류
                    "Hello 2026!",   // 정상 원문
                    "X",             // 모드 오류
                    "I",             // 정상 모드
                    "Z",             // 숫자 모드 오류
                    "S",             // 정상 숫자 모드
                    "3",             // 미리보기 개수 오류
                    "20"             // 정상
            );

            String out = output();
            assertThat(out).contains("[ERROR] 원문 메시지 형식이 올바르지 않습니다.");
            assertThat(out).contains("[ERROR] 모드 입력이 올바르지 않습니다.");
            assertThat(out).contains("[ERROR] 숫자 처리 모드 입력이 올바르지 않습니다.");
            assertThat(out).contains("[ERROR] 키 미리보기 출력 개수가 올바르지 않습니다.");

            assertThat(out).contains("암호문: ifmmp 3137!");
            assertThat(out).contains("체크섬: 776");

            assertThat(out).contains("[소문자]");
            assertThat(out).contains("- p -> q", "- o -> p"); // 20개 중 일부
            assertThat(out).contains("[숫자]");
            assertThat(out).contains("- 0 -> 1", "- 9 -> 0");

            assertThat(out).contains("빈도 리포트 (TOP 3)");
            assertThat(out).contains("1) 3 : 2", "2) m : 2", "3) 1 : 1");

            assertThat(out).contains("복호화 검증: OK");
        }, 98, RANDOM_I_S.toArray(new Integer[0]));
    }

    @Test
    void 원문_검증_실패시_해당_입력부터_재입력한다() {
        assertRandomNumberInRangeTest(() -> {
            run(
                    "Hello@@",       // 잘못된 원문
                    "Hello, World!", // 정상 원문
                    "E",
                    "K",
                    "8"
            );

            String out = output();
            assertThat(out).contains("[ERROR] 원문 메시지 형식이 올바르지 않습니다.");
        }, 66, RANDOM_E_K.toArray(new Integer[0]));
    }

    @Test
    void 모드와_미리보기개수_검증_실패시_해당_입력부터_재입력한다() {
        assertRandomNumberInRangeTest(() -> {
            run(
                    "Hello 2026!",
                    "I",
                    "Z",   // 숫자 모드 오류
                    "S",
                    "100", // 미리보기 개수 오류
                    "5"    // 정상(최소값)
            );

            String out = output();
            assertThat(out).contains("[ERROR] 숫자 처리 모드 입력이 올바르지 않습니다.");
            assertThat(out).contains("[ERROR] 키 미리보기 출력 개수가 올바르지 않습니다.");
        }, 98, RANDOM_I_S.toArray(new Integer[0]));
    }

    @Test
    void 키미리보기_개수가_작으면_각섹션별로_해당개수만_출력한다() {
        assertRandomNumberInRangeTest(() -> {
            run("Hello, World!", "E", "K", "5");

            String out = output();
            assertThat(out).contains("키 미리보기");
            // 대문자 5개까지만
            assertThat(out).contains("- A -> B", "- B -> C", "- C -> D", "- D -> E", "- E -> F");
            assertThat(out).doesNotContain("- F -> G");
            // 소문자 5개까지만
            assertThat(out).contains("- a -> b", "- b -> c", "- c -> d", "- d -> e", "- e -> f");
            assertThat(out).doesNotContain("- f -> g");

            assertThat(out).contains("복호화 검증: OK");
        }, 66, RANDOM_E_K.toArray(new Integer[0]));
    }
}
