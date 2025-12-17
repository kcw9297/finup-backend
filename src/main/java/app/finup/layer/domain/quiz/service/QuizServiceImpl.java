package app.finup.layer.domain.quiz.service;

import app.finup.layer.domain.member.entity.Member;
import app.finup.layer.domain.member.repository.MemberRepository;
import app.finup.layer.domain.quiz.entity.Quiz;
import app.finup.layer.domain.quiz.repository.QuizRepository;
import app.finup.layer.domain.stock.entity.Stock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * QuizService 구현 클래스
 * @author lky
 * @since 2025-12-17
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final MemberRepository memberRepository;

    @Override
    public void save(Long memberId, int score) {
        Member member = memberRepository.getReferenceById(memberId);
        Quiz quiz = Quiz.builder()
                .member(member)
                .score(score)
                .build();

        quizRepository.save(quiz);
    }
}
