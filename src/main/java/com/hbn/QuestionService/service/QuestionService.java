package com.hbn.QuestionService.service;

import com.hbn.QuestionService.model.QuestionWrapper;
import com.hbn.QuestionService.model.Questions;
import com.hbn.QuestionService.repo.QuestionsRepo;
import com.hbn.QuestionService.repo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {

    @Autowired
    QuestionsRepo questionsRepo;

    @Autowired

    public ResponseEntity<List<Questions>> getAllQuestions() {
       try{
           return new ResponseEntity<>(questionsRepo.findAll(), HttpStatus.OK);
       }
       catch (Exception e){
           e.printStackTrace();
       }
        return new ResponseEntity<>(questionsRepo.findAll(), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<List<Questions>> getQuestionsByCategory(String category) {
        try {
            return new ResponseEntity<>(questionsRepo.findByCategoryIgnoreCase(category), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);

    }

    public ResponseEntity<String> addQuestion(Questions questions) {
       questionsRepo.save(questions);
       return new ResponseEntity<>("success",HttpStatus.CREATED);
    }

    public ResponseEntity<List<Integer>> getQuestionsForQuiz(String categoryName, Integer numQuestions) {
        List<Integer> questions = questionsRepo.findRandomQuestionsByCategory(categoryName, numQuestions);
        return new ResponseEntity<>(questions, HttpStatus.OK);
    }

    public ResponseEntity<List<QuestionWrapper>> getQuestionsFromId(List<Integer> questionIds) {
        List<QuestionWrapper> questionWrappers = new ArrayList<>();
        List<Questions> questions = new ArrayList<>();
        for (Integer ids : questionIds){
            questions.add(questionsRepo.findById(ids).get());
        }

        for(Questions question : questions){
            QuestionWrapper qw = new QuestionWrapper();
            qw.setId(question.getId());
            qw.setQuestionTitle(question.getQuestionTitle());
            qw.setOption1(question.getOption1());
            qw.setOption2(question.getOption2());
            qw.setOption3(question.getOption3());
            qw.setOption4(question.getOption4());

            questionWrappers.add(qw);
        }

        return new ResponseEntity<>(questionWrappers, HttpStatus.OK);
    }

    public ResponseEntity<Integer> getScore(List<Response> responses) {

        int right = 0;

        for(Response response : responses){
            Questions questions = questionsRepo.findById(response.getId()).get();
            if(response.getResponse().equals(questions.getRightAnswer()))
                right++;
        }
        return new ResponseEntity<>(right, HttpStatus.OK);
    }
}
