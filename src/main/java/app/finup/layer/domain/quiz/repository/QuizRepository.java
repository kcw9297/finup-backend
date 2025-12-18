package app.finup.layer.domain.quiz.repository;

import app.finup.layer.domain.quiz.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository  extends JpaRepository<Quiz, Long> {
}
