package app.finup.layer.domain.quiz.service;

public interface QuizService {
    //DB에 점수 저장하기
    void save(Long memberId, int score);
}
