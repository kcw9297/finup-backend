package app.finup.layer.domain.quiz.redis;

import java.util.List;

/**
 * quiz 관련 데이터 캐싱을 위한 저장 인터페이스
 * @author kcw
 * @since 2025-01-10
 */
public interface QuizRedisStorage {

    /**
     * 응시한 퀴즈 내 단어번호 저장
     * @param memberId 퀴즈 응시대상 회원번호
     * @param quizIds 응시한 퀴즈번호
     */
    void storePrevWordsIds(Long memberId, List<Long> quizIds);

    /**
     * 이전에 응시했던 퀴즈 내 단어번호 목록 조회
     * @param memberId 퀴즈 응시대상 회원번호
     * @return 이전에 응시한 퀴즈 내 단어번호 목록
     */
    List<Long> getPrevWordsIds(Long memberId);
}
