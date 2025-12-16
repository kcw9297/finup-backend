package app.finup.layer.domain.quiz.service;

import app.finup.layer.domain.quiz.dto.QuizDto;
import app.finup.layer.domain.studyword.dto.StudyWordDto;

import java.util.List;

public class QuizAiServiceImpl implements QuizAiService {

    //private final

    @Override
    public List<QuizDto.Question> getQuizAi(StudyWordDto.Quiz studyword) {
        // [1] studyWord AI 데이터(랜덤 30개 단어)
        //List<StudyWordDto.Quiz> studyword = Stu;

        // [2] QuizAi 생성
        // [3] DTO 매핑

        return List.of();
    }
}
